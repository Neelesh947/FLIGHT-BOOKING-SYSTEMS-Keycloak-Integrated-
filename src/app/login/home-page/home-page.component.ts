import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LoginService } from '../../services/login.service';
import { Router } from '@angular/router';
import { AuthServicesService } from '../../services/auth-services.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent implements OnInit{
  loginData={
    username : '',
    password :''
  }

  username: string | null = null;
  roles: string[] = [];

  errorMessage: string | null = null;

  constructor( private snack: MatSnackBar, 
    private login:LoginService, 
    private authService: AuthServicesService,
    private route:Router) {}

  ngOnInit(): void {}

  formSubmit(){
    this.login.generateTokens(this.loginData).subscribe((data:any) =>{
      console.log("Login successful", data);
      this.authService.saveToken(data.accessToken);
      if (this.authService.isAuthenticated()) {
        this.route.navigate(['/navbar']);
      } else {
        this.authService.logout();
      }
    },
    (error) => {
      if(error.error && error.error.errorMessage){
        this.errorMessage = error.error.errorMessage;
      }
    })
  }
}
