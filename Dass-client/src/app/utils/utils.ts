export default class Utils {

    static setRefreshToken(data: any) {
        localStorage.setItem("token", (<any>data)[0].token);
     }

     static logout() {
        localStorage.clear();
        window.location.href = "/login";
     }

     static getToken(){
      return localStorage.getItem("token");
     }
}