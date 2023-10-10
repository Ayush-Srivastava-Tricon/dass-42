import { Component } from '@angular/core';
import { ApplicationService } from 'src/app/service/application.service';

@Component({
  selector: 'app-quotes',
  templateUrl: './quotes.component.html',
  styleUrls: ['./quotes.component.scss']
})
export class QuotesComponent {
  quote:String='';
  loader:boolean=false;
  errorMsg:string='';
  constructor(private _service:ApplicationService){}

  ngOnInit(){
      this.fetchQuotes();
  }

  fetchQuotes(){
    this.loader=true;
    this._service.fetchQuotes((res:any)=>{
      if(res.status && res.data){
        this.loader=false;
        this.quote = res.data?.[0]?.quotesName;
        this.errorMsg = res.message ? res.message : 'Please Complete the Questionaire First';
      } else{
        this.loader=false;
        this.errorMsg = res.message ? res.message : 'Please Complete the Questionaire First';
      }
    })
  }
}
