import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { HomePageComponent } from './login/home-page/home-page.component';
import { FormsModule } from '@angular/forms';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import { HttpClientModule } from '@angular/common/http';
import { NavBarComponent } from './navbar/nav-bar/nav-bar.component';
import { AdminListComponent } from './super-admin/admin-list/admin-list.component';
import { AddAdminComponent } from './super-admin/add-admin/add-admin.component';
import { QuestionListComponent } from './super-admin/question-list/question-list.component';
import { AddQuestionComponent } from './super-admin/add-question/add-question.component';
import {MatTableModule} from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import {MatButtonModule} from '@angular/material/button';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatListModule} from '@angular/material/list';
import {MatTabsModule} from '@angular/material/tabs';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import { EditAdminComponent } from './super-admin/edit-admin/edit-admin.component';
import { CustomerSupportRepresentativeComponent } from './admin/customer-support-representative/customer-support-representative.component';
import { FlightOperationManagerComponent } from './admin/flight-operation-manager/flight-operation-manager.component';
import { MarketingManagerComponent } from './admin/marketing-manager/marketing-manager.component';
import { ItSupportDeveloperComponent } from './admin/it-support-developer/it-support-developer.component';
import { PaymentProcessorComponent } from './admin/payment-processor/payment-processor.component';
import { AddFlightOperationManagerComponent } from './admin/add-flight-operation-manager/add-flight-operation-manager.component';
import { EditFlightOperationManagerComponent } from './admin/edit-flight-operation-manager/edit-flight-operation-manager.component';
import { AddUserComponent } from './flightOperationManager/user/add-user/add-user.component';
import { UserListComponent } from './flightOperationManager/user/user-list/user-list.component';
import { EditUserComponent } from './flightOperationManager/user/edit-user/edit-user.component';
import { AddFlightComponent } from './flightOperationManager/flightManagement/add-flight/add-flight.component';
import { EditFlightComponent } from './flightOperationManager/flightManagement/edit-flight/edit-flight.component';
import { FlightListComponent } from './flightOperationManager/flightManagement/flight-list/flight-list.component';
import { AddAirportsComponent } from './flightOperationManager/Airports/add-airports/add-airports.component';
import { EditAirportsComponent } from './flightOperationManager/Airports/edit-airports/edit-airports.component';
import { AirportListComponent } from './flightOperationManager/Airports/airport-list/airport-list.component';
import { AddTravelAgentComponent } from './flightOperationManager/travelAgent/add-travel-agent/add-travel-agent.component';
import { EditTravelAgentComponent } from './flightOperationManager/travelAgent/edit-travel-agent/edit-travel-agent.component';
import { TravelAgentListComponent } from './flightOperationManager/travelAgent/travel-agent-list/travel-agent-list.component';

@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    NavBarComponent,
    AdminListComponent,
    AddAdminComponent,
    QuestionListComponent,
    AddQuestionComponent,
    EditAdminComponent,
    CustomerSupportRepresentativeComponent,
    FlightOperationManagerComponent,
    MarketingManagerComponent,
    ItSupportDeveloperComponent,
    PaymentProcessorComponent,
    AddFlightOperationManagerComponent,
    EditFlightOperationManagerComponent,
    AddUserComponent,
    UserListComponent,
    EditUserComponent,
    AddFlightComponent,
    EditFlightComponent,
    FlightListComponent,
    AddAirportsComponent,
    EditAirportsComponent,
    AirportListComponent,
    AddTravelAgentComponent,
    EditTravelAgentComponent,
    TravelAgentListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    MatSnackBarModule,
    HttpClientModule ,
    MatTableModule,
    MatCheckboxModule,
    MatButtonModule,
    MatPaginatorModule,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatTabsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule
  ],
  providers: [
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
