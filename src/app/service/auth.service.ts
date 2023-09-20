import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService extends BaseService{

  constructor(http:HttpClient) {
    super(http);
   }

   login(param:any,callback:any){
    this.postData(param,this.httpUrl['login'],callback);
   }

   register(param:any,callback:any){
    this.postData(param,this.httpUrl['register'],callback);
   }


}
