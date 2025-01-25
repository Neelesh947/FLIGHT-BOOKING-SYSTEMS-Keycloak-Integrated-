import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private http:HttpClient) { }

  //generate tokens(login token)
  public generateTokens(loginData: any){
    return this.http.post('http://localhost:8081/dev/login',loginData);
  }

  //logout url(passes the userId)
  public logoutOrClearSession(userId: any){
    return this.http.post(`http://localhost:8081/dev/logout/${userId}`, userId);
  }
}
