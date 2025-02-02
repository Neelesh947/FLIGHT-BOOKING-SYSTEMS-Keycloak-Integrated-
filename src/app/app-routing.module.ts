import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NavBarComponent } from './navbar/nav-bar/nav-bar.component';
import { HomePageComponent } from './login/home-page/home-page.component';
import { AdminListComponent } from './super-admin/admin-list/admin-list.component';
import { QuestionListComponent } from './super-admin/question-list/question-list.component';
import { AddAdminComponent } from './super-admin/add-admin/add-admin.component';
import { EditAdminComponent } from './super-admin/edit-admin/edit-admin.component';
import { FlightOperationManagerComponent } from './admin/flight-operation-manager/flight-operation-manager.component';

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
        component:FlightOperationManagerComponent
      },
      {
        path:"edit-flight-operation-manager/:id",
        component:FlightOperationManagerComponent
      }
    ]
  },  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
