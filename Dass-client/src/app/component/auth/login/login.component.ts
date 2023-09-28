import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import Utils from 'src/app/utils/utils';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  error:any;
  isLoggedIn:boolean=false;
  loginModal:any;

  constructor(private auth:AuthService,private router:Router,private fb:FormBuilder){
    this.loginModal = this.fb.group({
      'username':['',Validators.required],
      'password':['',Validators.required]
    })
  }

  login(){
      this.auth.login(this.loginModal.value,(res:any)=>{
        console.log(32);
        if(res.status && res.data){
          console.log(res);
          this.error = '';
          this.isLoggedIn=true;
          this.setLocalData(res.data);
          this.router.navigate(['homepage']);
        } else{
          this.error = res.error ? res.error : res.message;
        }
      })
  }

  setLocalData(userData:any){
    Utils.setUserData(userData,this.isLoggedIn);
  }
}
