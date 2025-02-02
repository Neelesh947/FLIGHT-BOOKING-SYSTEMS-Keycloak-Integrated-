import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { FlightoperationmanagerService } from '../../services/flightoperationmanager.service';
import Swal from 'sweetalert2';

export interface flightOperationManager {
  email: string;
  enabled: boolean;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  username: string;
  id: string;
}

@Component({
  selector: 'app-flight-operation-manager',
  templateUrl: './flight-operation-manager.component.html',
  styleUrl: './flight-operation-manager.component.css'
})
export class FlightOperationManagerComponent implements OnInit{

  selectedTab = 0;
  displayedColumns: string[] = ['select', 'First Name', 'Last Name', 'emailId', 'Phone Number', 'Username', 'Status'];
  dataSource: MatTableDataSource<flightOperationManager> = new MatTableDataSource<flightOperationManager>();
  flightOperationsManagers : flightOperationManager[] = [];
  selection: SelectionModel<flightOperationManager> = new SelectionModel<flightOperationManager>(false, []);
  totalCounts: number = 0;
  pageSize: number = 10;  // Default page size
  isHidden: boolean | undefined;

  constructor(private FOM:FlightoperationmanagerService) {}

  ngOnInit(): void {
    this.getFlightOperationsManager();
  }

  getFlightOperationsManager(pageIndex: number = 0, pageSize: number = 10) {
    this.FOM.getListOfFlightOperationManager(pageIndex, pageSize, this.isHidden).subscribe
    ((data : any) =>{
      this.flightOperationsManagers = data.content;
      this.totalCounts = data.totalElements;
      this.dataSource.data = this.flightOperationsManagers;
    },
    (error)=>{
      console.log(error);
      if (error.response && error.response.status === 401){
        window.location.href = "";
      } else {
        Swal.fire('Error', 'Something went wrong', 'error');
      }
    })
  }

// Handle tab change and apply appropriate filter
 onTabChange(index: number): void {
      this.selectedTab = index;
      if (index === 0) {
        this.isHidden = undefined;  // For 'All'
      } else if (index === 1) {
        this.isHidden = true;  // For 'Active'
      } else if (index === 2) {
        this.isHidden = false;  // For 'Inactive'
      }
      this.updateDataSource();
    }

    // Update data source based on selected tab
  updateDataSource(): void {
      this.getFlightOperationsManager(0, this.pageSize);  // Reset to page 0 when changing tabs
  }
  
  onSelect(event: any, fligthsOM: flightOperationManager): void {
    if (event.checked) {
      this.selection.select(fligthsOM);
    } else {
      this.selection.deselect(fligthsOM);
    }
  }

  // Select all rows
  selectAll(event: any): void {
    if (event.checked) {
      this.selection.select(...this.dataSource.data);
    } else {
      this.selection.clear();
    }
  }

  // Check if all rows are selected
  isAllSelected(): boolean {
    return this.selection.selected.length === this.dataSource.data.length;
  }

  // Check if the selection is indeterminate
  isIndeterminate(): boolean {
    const numSelected = this.selection.selected.length;
    return numSelected > 0 && numSelected < this.dataSource.data.length;
  }

  // Activate admin
  activateFlightOperationManager(adminId: string): void {
    Swal.fire({
      icon: 'info',
      title: 'Are you sure you want to activate this admin?',
      showCancelButton: true,
      confirmButtonText: 'Activate',
    }).then((result) => {
      if (result.isConfirmed) {
        this.FOM.enableFlightOperationManager(adminId).subscribe(() => {
          Swal.fire('Activated!', 'Admin has been activated.', 'success');
          this.getFlightOperationsManager();  // Refresh data after activation
        }, (error) => {
          Swal.fire('Error', 'Something went wrong', 'error');
        });
      }
    });
  }

  // Deactivate admin
  deactiveFlightOperationManager(adminId: string): void {
    Swal.fire({
      icon: 'info',
      title: 'Are you sure you want to Deactivate this admin?',
      showCancelButton: true,
      confirmButtonText: 'Deactivate',
    }).then((result) => {
      if (result.isConfirmed) {
        this.FOM.disableFlightOperationManager(adminId, false).subscribe(() => {
          Swal.fire('Deactivated!', 'Admin has been deactivated.', 'success');
          this.getFlightOperationsManager();  // Refresh data after deactivation
        }, (error) => {
          Swal.fire('Error', 'Something went wrong', 'error');
        });
      }
    });
  }

  // Handle page change event
  pageChanged(event: any): void {
    this.getFlightOperationsManager(event.pageIndex, event.pageSize);
  }
}
