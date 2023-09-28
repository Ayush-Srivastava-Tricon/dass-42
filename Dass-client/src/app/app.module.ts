import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthGuard } from './guard/auth.guard';
import { TokenInterceptor } from './token/token.interceptor';
import { HeaderModule } from './component/header/header.module';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    HeaderModule
  ],
  providers: [AuthGuard,
  {
    provide:HTTP_INTERCEPTORS,useClass:TokenInterceptor,multi:true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
