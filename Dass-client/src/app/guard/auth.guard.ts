import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router:Router){    
  }
  
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot):boolean {
    const isUserLogged :boolean = JSON.parse(<any>localStorage.getItem("isUserLogged"));

    if(isUserLogged){
      return true;
    } 
    this.router.navigate(['/login']);
    return false;
  }
  
}
