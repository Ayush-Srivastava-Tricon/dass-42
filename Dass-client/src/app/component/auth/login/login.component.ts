import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  loginObj = {'email':'','password':''};
  error:any;
  isLoggedIn:boolean=false;

  constructor(private auth:AuthService,private router:Router){}

  login(){
    let param={
      'email':this.loginObj['email'],
      'password':this.loginObj['password']
    };
      this.auth.login(param,(res:any)=>{
        if(res.status == 200){
          console.log(res);
          this.error = '';
          this.isLoggedIn=true;
          this.setLocalData(res.data);
          this.router.navigate(['homepage']);
        } else{
          this.error = res.message;
        }
      })
  }

  setLocalData(userData:any){
    sessionStorage.setItem("isUserLogged", JSON.stringify(this.isLoggedIn));
    sessionStorage.setItem("userData",JSON.stringify(userData));
  }
}
