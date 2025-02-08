import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NavBarComponent } from './navbar/nav-bar/nav-bar.component';
import { HomePageComponent } from './login/home-page/home-page.component';
import { AdminListComponent } from './super-admin/admin-list/admin-list.component';
import { QuestionListComponent } from './super-admin/question-list/question-list.component';
import { AddAdminComponent } from './super-admin/add-admin/add-admin.component';
import { EditAdminComponent } from './super-admin/edit-admin/edit-admin.component';
import { FlightOperationManagerComponent } from './admin/flight-operation-manager/flight-operation-manager.component';
import { AddFlightOperationManagerComponent } from './admin/add-flight-operation-manager/add-flight-operation-manager.component';
import { EditFlightOperationManagerComponent } from './admin/edit-flight-operation-manager/edit-flight-operation-manager.component';
import { AddUserComponent } from './flightOperationManager/user/add-user/add-user.component';
import { EditUserComponent } from './flightOperationManager/user/edit-user/edit-user.component';
import { UserListComponent } from './flightOperationManager/user/user-list/user-list.component';

const routes: Routes = [
  { path: '', 
    component: HomePageComponent
  },
  {
    path:"navbar",
    component: NavBarComponent,
    children:[
      {
        path: "admin-list",
        component: AdminListComponent        
      },
      {
        path:"add-admin",
        component:AddAdminComponent
      },
      {
        path: "questions-list",
        component: QuestionListComponent
      },
      {
        path:"edit-admin/:id",
        component:EditAdminComponent
      },
      {
        path:"flight-operation-manager-list",
        component:FlightOperationManagerComponent
      },
      {
        path:"add-flight-operation-manager",
        component:AddFlightOperationManagerComponent
      },
      {
        path:"edit-flight-operation-manager/:id",
        component:EditFlightOperationManagerComponent
      },
      {
        path:"userList",
        component:UserListComponent
      },
      {
        path:"addUser",
        component:AddUserComponent
      },
      {
        path:"editUser/:id",
        component:EditUserComponent
      }
    ]
  },  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
