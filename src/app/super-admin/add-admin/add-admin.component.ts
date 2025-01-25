import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-admin',
  templateUrl: './add-admin.component.html',
  styleUrl: './add-admin.component.css'
})
export class AddAdminComponent implements  OnInit{

  constructor(private admins:AdminService, private route:Router){}

  ngOnInit(): void {  }

  admin={
    firstName:'',
    lastName:'',
    username:'',
    password:'',
    customerSupportNumber:'',
    email:'',
    phoneNumber:'',
    address:'',
    city:'',
    state:'',
    country:'',
    postalCode:''
  }
  

  formSubmit(){
    this.admins.createAdmin(this.admin).subscribe(data => {
      this.route.navigateByUrl("/navbar/admin-list");
    },(error)=>{
      Swal.fire("Something went wrong");
    })
  }
}
