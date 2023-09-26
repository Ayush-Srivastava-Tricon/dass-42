import { Component } from '@angular/core';
import { ApplicationService } from 'src/app/service/application.service';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.scss']
})
export class HomepageComponent {

  user:any;
  questionList:any=[];
  answersList:any=[];

  constructor(private _service:ApplicationService){}

  ngOnInit(){
      const localData :any = JSON.parse(<any>localStorage.getItem("userData"));
      if(localData){
        this.user = localData;
        console.log(this.user);
        
      }
      this.getDass42Questions();
  }

  getDass42Questions(){
    this._service.fetchDass42Question((res:any)=>{
      console.log(res);
      this.questionList = res.data;
      this.setChoices(this.questionList);
      
    })
  }

  setChoices(data:any){

    data.forEach((e:any)=>{
          e['choices'] = ['Nothing','Sometimes','Often','Heavily'];
    })

  }

  selectChoiceCrossQuestion(event:any,id:any){
    let params=[];
    this.questionList.forEach((data:any,idx:any)=>{
      if(data._id == id){
        this.answersList.push({questionId:id,choice:event.target.value});
      }
    })
    
  }
  
  submitAnswer(){
    this._service.submitAnswers({'answer':this.answersList,userId:this.user._id},(res:any)=>{
      console.log(res);
    })
  }

  logout(){
    sessionStorage.clear();
    localStorage.clear();
    location.reload();
  }
}
