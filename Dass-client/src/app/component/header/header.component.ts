import { Component } from '@angular/core';
import Utils from 'src/app/utils/utils';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

  user:any;

  constructor(){}

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

}
