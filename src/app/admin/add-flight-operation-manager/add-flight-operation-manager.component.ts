import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { FlightoperationmanagerService } from '../../services/flightoperationmanager.service';

@Component({
  selector: 'app-add-flight-operation-manager',
  templateUrl: './add-flight-operation-manager.component.html',
  styleUrl: './add-flight-operation-manager.component.css'
})
export class AddFlightOperationManagerComponent implements OnInit{

  constructor(private fom:FlightoperationmanagerService, private route:Router){}

  ngOnInit(): void {  }

  flightOperationManager={
      firstName:'',
      lastName:'',
      username:'',
      password:'',
      emailAddress:'',
      phoneNumber:'',
      address:'',
      city:'',
      state:'',
      country:'',
      postalCode:''
  }
  

  formSubmit(){
    this.fom.createFlightOperationManager(this.flightOperationManager).subscribe(data => {
      this.route.navigateByUrl("/navbar/flight-operation-manager-list");
    },(error)=>{
      Swal.fire("Something went wrong");
    })
  }
}
