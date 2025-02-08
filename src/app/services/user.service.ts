import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthServicesService } from './auth-services.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http:HttpClient, private auth:AuthServicesService) { }

  private getAuthHeaders(): HttpHeaders{
    const token = this.auth.getToken();
     if (token) {
       return new HttpHeaders({
         'Authorization': `Bearer ${token}`
       });
     } else {
       return new HttpHeaders();
     }
  } 

  //get user list
  public getUserList(page: number, size: number, isHidden?: boolean){
    let  params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString());

    if (isHidden !== undefined) {
      params = params.set('isHidden', isHidden.toString());
  }

  const token = this.auth.getToken();
  const headers = this.getAuthHeaders();
    return this.http.get("http://localhost:8082/dev/user", {params , headers});
  }
}
