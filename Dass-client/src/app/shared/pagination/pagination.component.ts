import { Component, EventEmitter, Input, Output,ElementRef, ViewChildren, QueryList } from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent {
  currentValue:number = 1;
  @Output() emitToParent = new EventEmitter();
  @ViewChildren('link') linkElememtRef!: QueryList<ElementRef>; 
  
  activeLink(e:any){
    this.linkElememtRef.forEach((e:any)=>{
      e.nativeElement.classList.remove("active")
    });
     e.target.classList.add("active");
     this.currentValue = e.target.innerText;
     this.emitToParent.emit({action:'getQuestionByPageNumber',value: this.currentValue - 1});
  }

  prevBtn(){
    if(this.currentValue > 1){
      let link = document.getElementsByClassName('page-link');
      this.linkElememtRef.forEach((e:any)=>{
        e.nativeElement.classList.remove("active")
      });
      this.currentValue--;
      link[this.currentValue-1]?.classList.add("active");
      this.emitToParent.emit({action:'getQuestionByPageNumber',value: this.currentValue - 1});
    }
  }
  
  nxtBtn(){
    if(this.currentValue < 3){
      let link = document.getElementsByClassName('page-link');
      this.linkElememtRef.forEach((e:any)=>{
        e.nativeElement.classList.remove("active")
      });
      this.currentValue++;
      link[this.currentValue-1]?.classList.add("active");
      this.emitToParent.emit({action:'getQuestionByPageNumber',value: this.currentValue - 1});
    }
  }
}
