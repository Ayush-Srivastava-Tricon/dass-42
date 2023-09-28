import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService extends BaseService {

  constructor(http:HttpClient) { 
    super(http);
  }

  fetchDass42Question(callback:any){
    this.getData({},this.httpUrl['fetch-question'],callback);
   }
   
  fetchDassScore(callback:any){
    this.getData({},this.httpUrl['fetch-dass-score'],callback);
   }

  checkUserAttemptTest(callback:any){
    this.getData({},this.httpUrl['checkUserAttemptTest'],callback);
   }

   submitAnswers(params:any,callback:any){
    this.postData(params,this.httpUrl['submit-dass-response'],callback);
   }
}
