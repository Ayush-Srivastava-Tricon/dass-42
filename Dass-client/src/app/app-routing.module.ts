import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guard/auth.guard';

const routes: Routes = [
  {
    path:'',
    pathMatch:'full',
    redirectTo:'login'
  },
  {
    path:'login',
    loadChildren:()=>import("./component/auth/login/login.module").then(m=>m.LoginModule),
  },
  {
    path:'signup',
    loadChildren:()=>import("./component/auth/signup/signup.module").then(m=>m.SignupModule)
  },
  {
    path:'homepage',
    loadChildren:()=>import("./component/homepage/homepage.module").then(m=>m.HomepageModule),
    canActivate:[AuthGuard]
  },
  {
    path:'activity',
    loadChildren:()=>import("./component/activity/activity.module").then(m=>m.ActivityModule),
    canActivate:[AuthGuard]
  },
  {
    path:'quotes',
    loadChildren:()=>import("./component/quotes/quotes.module").then(m=>m.QuotesModule),
    canActivate:[AuthGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
