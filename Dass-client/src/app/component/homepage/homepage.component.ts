import { Component } from '@angular/core';
import { ApplicationService } from 'src/app/service/application.service';
import Utils from 'src/app/utils/utils';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.scss']
})
export class HomepageComponent {

  user:any;
  questionList:any=[];
  answersList:any=[];
  isRetakenTest:boolean=false;
  isFirstTimeUser:boolean=false;
  testSubmittedDate:any= new Date();
  isRetestEnable:boolean=false;
  loader:boolean=false;
  dassScore:any = {'anxiety':'','depress':'','stress':''};
  numberOfDassQuestion:number=42;  //42 standard question.
  pageNumber:number=0;
  showDassTable:boolean=false;
  sufferingState:any='';

  questionsPerPage = 14;

  checkboxState: any= new Map();
  constructor(private _service:ApplicationService){}


  setCheckboxState(questionId: number, responseStatus: number) {
    this.checkboxState.set(questionId,responseStatus);
  }

  getCheckboxState(questionId: number) {
    return this.checkboxState.get(questionId);
  }

  ngOnInit(){
      const localData :any = Utils.getUserData();
      if(localData){
        this.user = localData;
        this.checkUserAttemptTestFirstTime();
      }
    
      
  }

  checkUserAttemptTestFirstTime(){
    this.loader=true;
    this._service.checkUserAttemptTest((res:any)=>{
      if(res.status && res.data){
          this.isFirstTimeUser = res.data?.isFirstTimeUser;
          this.testSubmittedDate = res.data?.submittedDate;
          if(this.isFirstTimeUser){
            this.getDass42Questions();
          } else{
            this.availableForRetest(res.data?.isUserReset);
          }
      }
      
    })
    console.log(32);
    
  }

  availableForRetest(isUserReset:boolean){
      const todayDate:any = new Date();
      let previousSubmittedDate:any = new Date(this.testSubmittedDate);
      const diffTime = Math.abs(todayDate - previousSubmittedDate);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
      this.loader=false;
      if(diffDays >= 14){
        this.isRetestEnable = true;
        this.isRetakenTest = true;
        !isUserReset ?  this.resetUserResponse() : '';
        this.getDass42Questions();
      } else{
        this.fetchDassScore();
      }
  }

  resetUserResponse(){
    this._service.deleteUserResponse(this.isRetakenTest,(res:any)=>{
      if(res.status){
        console.log(res);
        
      }
    })
  }

  getDass42Questions(){
    this.loader=true;
    this._service.fetchDass42Question(this.pageNumber,(res:any)=>{
      if(res.status && res.data){
        this.loader=false;
        this.questionList = res.data[0].data;
        this.setChoices(this.questionList);
        console.log(this.checkboxState);
      } else{
        //error part
      }
      
    })
  }

  setChoices(data:any){
    data.forEach((e:any)=>{
          e['choices'] = ['Never','Sometimes','Often','Heavily'];
    })

  }

  selectChoiceCrossQuestion(event:any,id:any){
        if(this.answersList.length>0){
            let responseExist = this.answersList.some((item:any)=>item.questionId == id);
            if(!responseExist){
              this.answersList.push({questionId:id,responseStatus:+event.target.value});
            } else{
                  this.answersList.forEach((e:any)=>{
                        if(e.questionId == id){
                          e.responseStatus = +event.target.value;
                        }
                  })
            }
        } else{
          this.answersList.push({questionId:id,responseStatus:+event.target.value});
        }
        
        this.setCheckboxState(id,+event.target.value);
        console.log(this.answersList);
  }
  
  submitAnswer(){
    let params:any={
      'data':this.answersList,
      "retakeSurvey":this.isRetakenTest
    };

    this._service.submitAnswers(params,(res:any)=>{
        if(res){
          this.questionList=[];
          this.fetchDassScore();

        }
    })
  }

  fetchDassScore(){
    this.loader=true;
    this._service.fetchDassScore((res:any)=>{
      if(res.status && res.data){
        this.loader=false;
        this.dassScore.anxiety = res.data.anxityScore;
        this.dassScore.depress = res.data.depressionScore;
        this.dassScore.stress = res.data.stressScore;
        this.sufferingState = this.checkUserSuffersFrom();
        res.data.finalDassScore.finalScore == 0 ? this._service.emitOnValueChange({action:'addRetestLinkOnNav'}) : '';
      }
    })
  }

  checkUserSuffersFrom(){
    if(this.dassScore.depress !== 0 && this.dassScore.depress >= this.dassScore.anxiety && this.dassScore.depress >=  this.dassScore.stress){
      return "Depression";
    } else if(this.dassScore.anxiety !== 0 && this.dassScore.anxiety >= this.dassScore.depress && this.dassScore.anxiety >=  this.dassScore.stress){
        return "Anxiety";
    } else if(this.dassScore.stress !== 0 && this.dassScore.stress >= this.dassScore.depress && this.dassScore.stress >=  this.dassScore.anxiety){
      return "Stress";
    } else return "Nothing";
  }

  receiveChildren(event:any){
    if(event['action'] === 'getQuestionByPageNumber'){
        this.pageNumber = event.value;
        this.getDass42Questions();
    }
  }

  calculateStartingSerialNumber(): number {
    return this.pageNumber * this.questionsPerPage + 1;
  }

}
