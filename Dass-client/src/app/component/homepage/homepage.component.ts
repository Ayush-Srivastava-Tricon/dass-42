import { Component } from '@angular/core';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.scss']
})
export class HomepageComponent {

  user:any;

  constructor(){}

  ngOnInit(){
      const localData :any = JSON.parse(<any>sessionStorage.getItem("userData"));
      if(localData){
        this.user = localData;
      }
  }

  logout(){
    sessionStorage.clear();
    location.reload();;
  }
}
