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

  fetchDass42Question(pageNumber:any,callback:any){
    this.getData({},`${this.httpUrl['fetch-question']}/${pageNumber}`,callback);
   }
   
  fetchDassScore(callback:any){
    this.getData({},this.httpUrl['fetch-dass-score'],callback);
   }
   
  fetchQuotes(callback:any){
    this.getData({},this.httpUrl['fetch-quotes'],callback);
   }

  checkUserAttemptTest(callback:any){
    this.getData({},this.httpUrl['checkUserAttemptTest'],callback);
   }

   submitAnswers(params:any,callback:any){
    this.postData(params,this.httpUrl['submit-dass-response'],callback);
   }

   fetchActivity(callback:any){
    this.getData({},this.httpUrl['fetch-task'],callback);
   }
}
