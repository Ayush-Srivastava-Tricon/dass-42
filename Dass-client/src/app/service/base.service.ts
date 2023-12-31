import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environment/environment';
import { switchMap } from 'rxjs';
import Utils from '../utils/utils';

@Injectable({
  providedIn: 'root'
})
export class BaseService {

   httpUrl:any={
    'login':'/login',
    'register':'/register',
    'fetch-question':'/dass-questions',
    'submit-dass-response':'/save_dass_response',
    'fetch-dass-score':'/fetch_dass_score',
    'checkUserAttemptTest':'/checkUserAttemptTest',
    'fetch-task':'/fetch-task',
    'fetch-quotes':'/fetch-quotes',
    'fetch-user-activity':'/fetch-user-activity',
    'save-activity':'/save-activity',
    'reset-user-response':'/reset-user-response',
   };

  constructor(public http: HttpClient) { }

  generateRefreshToken() {
    return this.http.get(environment.API_URL + '/refresh');
  }

  getData(d: any, url: string, callback: any) {
    this.generateRefreshToken().pipe(switchMap(data => {
      Utils.setRefreshToken(data);
      return this.http.get(environment.API_URL + url, d);
    },
    )
    ).subscribe((data) => {
      callback((<any>data));
    },
      (error: any) => {
        console.log(error);
        if (error.status == 401) {
          Utils.logout();
        }
        if (error.status == 500) {
          callback(error);
        }
      },
      () => {
      })

      ;

  }


  postData(d: any, url: string, callback: any) {
    this.generateRefreshToken().pipe(switchMap(data => {
      Utils.setRefreshToken(data);
      return this.http.post(environment.API_URL + url, d);
    },
    )
    ).subscribe((data) => {
      callback((<any>data));
    },
      (error: any) => {
        console.log(error)
        if (error.status == 401) {
          Utils.logout();
        }
        if (error.status == 500) {
          callback(error);
        }
      },
      () => {
      });
  }

}
