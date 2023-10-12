import { Component } from '@angular/core';
import { ApplicationService } from 'src/app/service/application.service';
import Utils from 'src/app/utils/utils';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

  user:any;
  isDassScoreZero:boolean=false;

  constructor(private _service:ApplicationService){
    this._service.subscribeOnValueChange("HeaderComponent",(event:any)=>{
      if(event['action'] == 'addRetestLinkOnNav'){
        this.isDassScoreZero = true;
    }
    })

  }

  ngOnInit(){
      let localData :any = Utils.getUserData();
      if(localData){
        this.user = localData;
      }
  }
  
  logout(){
    location.reload();
    sessionStorage.clear();
    localStorage.clear();
  }

  takeRetest(){
    this._service.deleteUserResponse(true,(res:any)=>{
      if(res.status){
        location.reload();
      }
    })
  }

}
