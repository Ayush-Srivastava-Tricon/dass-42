import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseService } from './base.service';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService extends BaseService {

  private seviceEventEmitter = new Subject<any>();
  private seviceEventEmitterReference: any = {};

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

   submitActivity(params:any,callback:any){
    this.postData(params,this.httpUrl['save-activity'],callback);
   }

   fetchActivity(callback:any){
    this.getData({},this.httpUrl['fetch-task'],callback);
   }

   checkUserAlreadySubmittedActivity(callback:any){
    this.getData({},this.httpUrl['fetch-user-activity'],callback);
   }

  //  Below method is to reset user response from Database
   deleteUserResponse(isRetest:boolean,callback:any){
    this.getData({},this.httpUrl[`reset-user-response`]+"/"+isRetest,callback);
   }

   emitOnValueChange(data: any) {
    this.seviceEventEmitter.next(data);
  }

  subscribeOnValueChange(name: any, callback: any) {
    this.seviceEventEmitterReference[name] = this.seviceEventEmitter.subscribe((header) => {
      callback(header)
    });
  }
}
