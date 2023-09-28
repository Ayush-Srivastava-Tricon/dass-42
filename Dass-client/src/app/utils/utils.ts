export default class Utils {

    static setRefreshToken(data: any) {
        localStorage.setItem("token", (<any>JSON.stringify((data)[0].token)));
     }

     static logout() {
        localStorage.clear();
        window.location.href = "/login";
     }

     static getToken(){
      return JSON.parse(<any>localStorage.getItem("token"));
     }

     static setUserData(data:any,userLoggedIn:boolean){
         localStorage.setItem("isUserLogged", JSON.stringify(userLoggedIn));
         localStorage.setItem("userData",JSON.stringify(data));
         localStorage.setItem("token",JSON.stringify(data.token));
     }

     static isUserLoggedIn(){
      return JSON.parse(<any>localStorage.getItem("isUserLogged"))
     }

     static getUserData(){
      return JSON.parse(<any>localStorage.getItem("userData"));
     }
}