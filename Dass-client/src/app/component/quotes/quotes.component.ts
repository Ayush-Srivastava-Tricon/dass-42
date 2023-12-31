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
  authorName:string='';
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
        this.splitAuthorName(this.quote);
        this.errorMsg = res.message ? res.message : 'Please Complete the Questionaire First';
      } else{
        this.loader=false;
        this.errorMsg = res.message ? res.message : 'Please Complete the Questionaire First';
      }
    })
  }

  splitAuthorName(quote:any){
    if (quote!= null) {
      this.quote = quote.split("-")[0];
      this.authorName = quote.split("-")[1];
    }
  }
}
