import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

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

  getData(d: any, url: string, callback: any){
    return this.http.get(this.role+url,d).subscribe((data:any)=>callback(<any>data),
    (error: any) => {
      console.log(error);
      if (error.status == 500) {
        callback(error);
      }
    },
    () => {
    })
  }

postData(d: any, url: string, callback: any){
    return this.http.post(this.authUrl+url,d).subscribe((data:any)=>callback(<any>data),
    (error: any) => {
      console.log(error);
      if (error.status == 500) {
        callback(error);
      }
    },
    () => {
    })
  }
}
