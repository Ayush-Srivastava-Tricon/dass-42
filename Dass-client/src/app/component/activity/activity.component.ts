import { Component } from '@angular/core';
import { ApplicationService } from 'src/app/service/application.service';

@Component({
  selector: 'app-activity',
  templateUrl: './activity.component.html',
  styleUrls: ['./activity.component.scss']
})
export class ActivityComponent {

  constructor(private _service:ApplicationService){

  }

  ngOnInit(){
    this.fetchActivity();
  }

  fetchActivity(){
    this._service.fetchActivity((res:any)=>{
      if(res.status == 200){
        console.log(res);
        
      }
    })
  }

}
