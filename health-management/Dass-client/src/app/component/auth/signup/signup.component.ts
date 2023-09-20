import { Component } from '@angular/core';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {

  signUpObj:any = {};
  error:any;

  constructor(private auth:AuthService){}

  register(){

    let params:any={
      'firstName':this.signUpObj['firstName'],
      'lastName':this.signUpObj['lastName'],
      'email':this.signUpObj['email'],
      'password':this.signUpObj['password'],
      'userName':this.signUpObj['userName']
    };

    this.auth.register(params,(res:any)=>{
      if(res){
        console.log(res);
      }
    })

  }

}

