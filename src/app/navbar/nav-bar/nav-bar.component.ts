import { Component, OnInit } from '@angular/core';
import { AuthServicesService } from '../../services/auth-services.service';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css'
})
export class NavBarComponent implements OnInit{

  role: string[] = [];

  name: string = '';

  constructor(public auth:AuthServicesService,
      private route:Router,
      private login:LoginService
  ){}

  ngOnInit(): void {
    this.getRoles();
    this.getName();
  }

  getRoles(): void {
    const roles = this.auth.getRoles();
    if (roles && roles.length > 0) {
      this.role = roles; 
      console.log(this.role)
    } else {
      console.log('No roles found for the user.');
      this.role = []; 
    }
  }

  getName() : void{
    const names = this.auth.getName();
    if(names && names.length > 0){
      this.name = names;
      console.log(this.name);
    } else {
      console.log("No names found");
      this.name = "";
    }
  }


  logout(){    
    const userId = this.auth.getUserId();
    console.log(userId)
    this.login.logoutOrClearSession(userId).subscribe((data) =>{
      console.log("session cleared successfully")
      this.auth.logout();
    },(error)=>{
      Swal.fire({
        title: "Something went wrong",
        icon: "error",
        draggable: true
      });
    })
  }
}
