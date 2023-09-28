import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {

  error:any;
  signUpModal:any;

  constructor(private auth:AuthService,private fb:FormBuilder){
    this.signUpModal = this.fb.group({
      'firstName':['',Validators.required],
      'lastName':['',Validators.required],
      'email':['',Validators.required],
      'password':['',Validators.required],
    })
  }

  register(){

    this.auth.register( this.signUpModal.value,(res:any)=>{
      if(res.status && !res.data){
        this.error = res.message;
        console.log(res);
      } else{
          this.error = res.data.email;
      }
    })

  }

}

