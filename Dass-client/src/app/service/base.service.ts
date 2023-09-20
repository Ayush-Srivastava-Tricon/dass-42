import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environment/environment';
import { switchMap } from 'rxjs';
import Utils from '../utils/utils';

@Injectable({
  providedIn: 'root'
})
export class BaseService {

   authUrl:any = "http://localhost:3000/api/auth";
   role:any = "http://localhost:3000/api/roles";

   httpUrl:any={
    'login':'/login',
    'register':'/register'
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
