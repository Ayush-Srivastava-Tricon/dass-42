import { Component } from '@angular/core';
import Utils from './utils/utils';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'health-management';

  constructor(){}

  isUserLoggedIn(){
    return Utils.isUserLoggedIn();
  }
}
