import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NavBarComponent } from './navbar/nav-bar/nav-bar.component';
import { HomePageComponent } from './login/home-page/home-page.component';
import { AdminListComponent } from './super-admin/admin-list/admin-list.component';
import { QuestionListComponent } from './super-admin/question-list/question-list.component';
import { AddAdminComponent } from './super-admin/add-admin/add-admin.component';
import { EditAdminComponent } from './super-admin/edit-admin/edit-admin.component';

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
      }
    ]
  },  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
