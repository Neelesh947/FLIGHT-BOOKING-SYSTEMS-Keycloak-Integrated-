import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http:HttpClient) { }

public getAdminById(adminId: string){
  return this.http.get(`http://localhost:8082/dev/admin/${adminId}`);
}
  

  //get list of admins
public getListOfAdmins(page: number, size: number, isHidden?: boolean){
  let  params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString());

    if (isHidden !== undefined) {
      params = params.set('isHidden', isHidden.toString());
  }
    return this.http.get("http://localhost:8082/dev/admin/get-admin-list", {params});
}

  //disable the admin
public disableAdmin(adminId: string, enabled: boolean) {
    const url = `http://localhost:8082/dev/admin/updateStatus/${adminId}`;
    const body = { enabled };
    return this.http.patch(url, body);
}

// Enable the admin
public enableAdmin(adminId: string) {
    const url = `http://localhost:8082/dev/admin/updateStatus/${adminId}`;
    const body = { enabled: true };
    return this.http.patch(url, body);
}


  //create admins
  public createAdmin(adminData:any) {
    return this.http.post("http://localhost:8081/dev/create-user/admin", adminData);
  }

  public updateAdmin(adminId: string, admin:any){
    const url = `http://localhost:8082/dev/admin/${adminId}`;
    return this.http.patch(url,admin);
  }
}
