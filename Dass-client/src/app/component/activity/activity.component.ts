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
  constructor(private _service:ApplicationService){

  }

  ngOnInit(){
    this.fetchActivity();
  }

  fetchActivity(){
    this.loader=true;
    this._service.fetchActivity((res:any)=>{
      if(res.status){
        this.loader=false;
        this.splitTaskDescription(res.data);
      }
    })
  }

  splitTaskDescription(data:any){
    let newtaskList:any=[];
      data.forEach((e:any)=>{
        let m = e.taskName.split(":");
        newtaskList.push({'taskName':m[0],'description':m[1]});
      })
      console.log(newtaskList);
      this.tasks = newtaskList;
  }

  markTaskCompleted(event:any){
    console.log(event.target.checked);
    
  }

}
