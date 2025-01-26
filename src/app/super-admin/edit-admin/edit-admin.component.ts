import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-edit-admin',
  templateUrl: './edit-admin.component.html',
  styleUrl: './edit-admin.component.css'
})
export class EditAdminComponent implements OnInit{

  id : string = "123";
  admin: any={
    firstName:"",
    lastName:"",
    username:"",
    telecomnumber:"",
    emailId:"",
    phoneNumber:"",
    address:"",
    city:"",
    state:"",
    country:"",
    postalCode:"",
    companyName:""
  }
  constructor(private adminService:AdminService, private route: ActivatedRoute,
    private router:Router
  ) {}

  ngOnInit(): void { 
      this.id = this.route.snapshot.params['id'];
      this.adminService.getAdminById(this.id).subscribe(data =>{
        this.admin=data;
        console.log(this.admin)
      })
   }
  
  formSubmit(){
    const updateAdmin = {
      firstName: this.admin.firstName,
      lastName: this.admin.lastName,
      username: this.admin.username,
      customerSupportNumber: Array.isArray(this.admin.telecomnumber) ? this.admin.telecomnumber[0] : this.admin.telecomnumber,
      email: this.admin.emailId,
      phoneNumber: Array.isArray(this.admin.phoneNumber) ? this.admin.phoneNumber[0] : this.admin.phoneNumber,
      address: Array.isArray(this.admin.address) ? this.admin.address[0] : this.admin.address,
      city: Array.isArray(this.admin.city) ? this.admin.city[0] : this.admin.city,
      state: Array.isArray(this.admin.state) ? this.admin.state[0] : this.admin.state,
      country: Array.isArray(this.admin.country) ? this.admin.country[0] : this.admin.country,
      postalCode: Array.isArray(this.admin.postalCode) ? this.admin.postalCode[0] : this.admin.postalCode
    };
    this.adminService.updateAdmin(this.id, updateAdmin).subscribe(data=>{
      Swal.fire("success","updated successfully",'success').then((e)=>{
      this.router.navigate(['/navbar/admin-list'])
      })
    },(error)=>{
      console.log(error);
      Swal.fire('Error!','error while saving data','error');
    })
  }
}
