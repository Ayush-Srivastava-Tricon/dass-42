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

  constructor(private _service:ApplicationService){}

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
            this.availableForRetest();
          }
      }
      
    })
    console.log(32);
    
  }

  availableForRetest(){
      const todayDate:any = new Date();
      let previousSubmittedDate:any = new Date(this.testSubmittedDate);
      const diffTime = Math.abs(todayDate - previousSubmittedDate);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 
      this.loader=false;
      if(diffDays >= 14){
        this.isRetestEnable = true;
        this.isRetakenTest = true;
        this.getDass42Questions();
      } else{
        this.fetchDassScore();
      }
  }

  getDass42Questions(){
    this.loader=true;
    this._service.fetchDass42Question((res:any)=>{
      if(res.status && res.data){
        this.loader=false;
        this.questionList = res.data;
        this.setChoices(this.questionList);
      } else{
        //error part
      }
      
    })
  }

  setChoices(data:any){
    data.forEach((e:any)=>{
          e['choices'] = ['Nothing','Sometimes','Often','Heavily'];
    })

  }

  selectChoiceCrossQuestion(event:any,id:any){
    this.questionList.forEach((data:any)=>{
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
            data.id == id ? this.answersList.push({questionId:id,responseStatus:+event.target.value}) : '';
        }
    })
    
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
    this._service.fetchDassScore((res:any)=>{
      if(res.status && res.data){
        console.log(res);
        this.dassScore.anxiety = res.data.anxityScore;
        this.dassScore.depress = res.data.depressionScore;
        this.dassScore.stress = res.data.stressScore;
      }
    })
  }

}
