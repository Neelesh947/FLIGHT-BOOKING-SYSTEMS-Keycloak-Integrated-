import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthServicesService } from './auth-services.service';

@Injectable({
  providedIn: 'root'
})
export class FlightoperationmanagerService {

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
  
  public getListOfFlightOperationManager(page: number, size: number, isHidden?: boolean) {
    let  params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

    if (isHidden !== undefined) {
      params = params.set('isHidden', isHidden.toString());
  }

  const token = this.auth.getToken();
  const headers = this.getAuthHeaders();
    return this.http.get("http://localhost:8082/dev/FlightOperationManager/get-flight-operation-manager", {params , headers});
  }

  public enableFlightOperationManager(flightOperationManagerId : String) {
    const url = `http://localhost:8082/dev/FlightOperationManager/updateStatus/${flightOperationManagerId}`;
    const body = { enabled: true };
    return this.http.patch(url, body);
  }

  public disableFlightOperationManager(flightOperationManagerId: string, enabled: boolean) {
    const url = `http://localhost:8082/dev/FlightOperationManager/updateStatus/${flightOperationManagerId}`;
    const body = { enabled };
    return this.http.patch(url, body);
}

public createFlightOperationManager(flightOperationManager:any) {
  const headers = this.getAuthHeaders();
  return this.http.post("http://localhost:8082/dev/FlightOperationManager/create/operation-manager", flightOperationManager, { headers });
}

public updateFlightOperationManager(FOMID: string, flightOperationManager:any){
  const url = `http://localhost:8082/dev/admin/${FOMID}`;
  const headers = this.getAuthHeaders();
  return this.http.patch(url,flightOperationManager,{ headers });
}
}
