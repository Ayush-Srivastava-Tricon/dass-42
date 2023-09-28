import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HomepageRoutingModule } from './homepage-routing.module';
import { HomepageComponent } from './homepage.component';
import { HeaderModule } from '../header/header.module';


@NgModule({
  declarations: [HomepageComponent],
  imports: [
    CommonModule,
    HomepageRoutingModule,
    HeaderModule
  ],
  exports:[HomepageComponent]
})
export class HomepageModule { }
