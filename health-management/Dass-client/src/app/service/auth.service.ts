import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseService } from './base.service';
import { environment } from '../environment/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})

export class AuthService extends BaseService{

  constructor(http:HttpClient) {
    super(http);
   }

  login(param: any, callback: any) {
    this.http.post(environment.API_URL + '/account/login',
      param).subscribe({
        next: data => { callback((<any>data)); },
        error: error => {
          callback((<any>error));
        }
      })
  };

   register(param:any,callback:any){
    return this.http.post(environment.API_URL + '/signup', param, httpOptions).subscribe({
      next: data => { callback((<any>data)); },
      error: error => {
        callback((<any>error));
      }
    })
  }


}
