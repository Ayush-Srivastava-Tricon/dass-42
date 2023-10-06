import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ActivityRoutingModule } from './activity-routing.module';
import { ActivityComponent } from './activity.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [ActivityComponent],
  imports: [
    CommonModule,
    ActivityRoutingModule,
    FormsModule
  ],
  exports:[ActivityComponent]
})
export class ActivityModule { }
