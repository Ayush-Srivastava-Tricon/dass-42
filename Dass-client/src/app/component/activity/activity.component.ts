import { Component } from '@angular/core';
import { ApplicationService } from 'src/app/service/application.service';

@Component({
  selector: 'app-activity',
  templateUrl: './activity.component.html',
  styleUrls: ['./activity.component.scss']
})
export class ActivityComponent {
  tasks:any=[];
  loader:boolean=false;
  submitActivityBtnDisabled:boolean=true;
  isActivitySubmitted:boolean=false;
  activityConfig:any={};
  errorMsg:string='';
  constructor(private _service:ApplicationService){

  }

  ngOnInit(){
    this.checkUserSubmittedActivity();
    // this.fetchActivity();
  }

  fetchActivity(){
    this.loader=true;
    this._service.fetchActivity((res:any)=>{
      if(res.status && res.data){
        this.loader=false;
        this.splitTaskDescription(res.data);
        this.errorMsg = res.message ? res.message : 'Please Complete the Questionaire First';
      } else{
        this.loader=false;
        this.errorMsg = res.message ? res.message : 'Please Complete the Questionaire First';
      }
    })
  }

  splitTaskDescription(data:any){
    let newtaskList:any=[];
      data.forEach((e:any)=>{
        let m = e.taskName.split(":");
        newtaskList.push({'taskName':m[0],'description':m[1],'taskId':e.taskId});
      })
      console.log(newtaskList);
      this.tasks = newtaskList;
  }

  markTaskCompleted(event:any,idx:any){
    if(this.isActivitySubmitted){
      return;
    }
    const activityKey = `activity${idx + 1}`;
    if (event.target.checked) {
      this.activityConfig[activityKey] = +event.target.value;
    } else {
      delete this.activityConfig[activityKey];
    }

    Object.keys(this.activityConfig).length ==5 ?  this.submitActivityBtnDisabled = false :   this.submitActivityBtnDisabled = true;
  }

  saveActivity(){
      this._service.submitActivity(this.activityConfig,(res:any)=>{
        if(res.status){
          console.log(res);
          this.checkUserSubmittedActivity();
        } 
      })
  }

  checkUserSubmittedActivity(){
    this.loader=true;
    this._service.checkUserAlreadySubmittedActivity((res:any)=>{
      if(res.status && res.data){
        console.log(res);
        this.loader=false;
        let todayDate: any = new Date();
        todayDate = todayDate.setHours(0, 0, 0, 0);
        let updatedDate: any = new Date(res.data.updatedDate);
        updatedDate = updatedDate.setHours(0, 0, 0, 0);
        todayDate == updatedDate ? this.isActivitySubmitted =true : this.isActivitySubmitted =false;

        if(!res.data.isActivitySubmitted){
            this.fetchActivity();
        } else if(res.data.successStatus){
            this.fetchActivity();
        } else if( !res.data.successStatus){
                this.setSubmittedActivityQuestion(res.data);
        }
      } else{
        this.loader=false;
        this.errorMsg = res.message ? res.message : 'Please Complete the Questionaire First';
      }
    })
  }

  setSubmittedActivityQuestion(data:any){
    let newtaskList: any = [];
    let activity_1: any = data.activity1.split(":");
    newtaskList.push({ 'taskName': activity_1[0], 'description': activity_1[1] });
    let activity_2: any = data.activity2.split(":");
    newtaskList.push({ 'taskName': activity_2[0], 'description': activity_2[1] });
    let activity_3: any = data.activity3.split(":");
    newtaskList.push({ 'taskName': activity_3[0], 'description': activity_3[1] });
    let activity_4: any = data.activity4.split(":");
    newtaskList.push({ 'taskName': activity_4[0], 'description': activity_4[1] });
    let activity_5: any = data.activity5.split(":");
    newtaskList.push({ 'taskName': activity_5[0], 'description': activity_5[1] });
    this.tasks = newtaskList;
  }

}
