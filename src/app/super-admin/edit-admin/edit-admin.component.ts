import { Component } from '@angular/core';

@Component({
  selector: 'app-edit-admin',
  templateUrl: './edit-admin.component.html',
  styleUrl: './edit-admin.component.css'
})
export class EditAdminComponent {

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
  
  formSubmit(){}
}
