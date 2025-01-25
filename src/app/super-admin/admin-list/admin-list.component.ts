import { Component, OnInit, ViewChild } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import { MatPaginator } from '@angular/material/paginator';  // Import MatPaginator
import Swal from 'sweetalert2';

export interface Admin {
  email: string;
  enabled: boolean;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  username: string;
  id: string;
}

@Component({
  selector: 'app-admin-list',
  templateUrl: './admin-list.component.html',
  styleUrls: ['./admin-list.component.css']
})
export class AdminListComponent implements OnInit {

  selectedTab = 0;
  displayedColumns: string[] = ['select', 'First Name', 'Last Name', 'emailId', 'Phone Number', 'Username', 'Status'];
  dataSource: MatTableDataSource<Admin> = new MatTableDataSource<Admin>();
  admins: Admin[] = [];
  selection: SelectionModel<Admin> = new SelectionModel<Admin>(false, []);
  totalAdminsCount: number = 0;
  pageSize: number = 10;  // Default page size
  isHidden: boolean | undefined;
  
  // @ViewChild(MatPaginator) paginator: MatPaginator;  // Add paginator reference

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.getAdmins();
  }

  // Fetch all admins
  fetchAllAdmins(): void {
    this.isHidden = undefined;
    this.getAdmins();
  }

  // Fetch active admins
  fetchActiveAdmins(): void {
    this.isHidden = true;
    this.getAdmins();
  }

  // Fetch inactive admins
  fetchInactiveAdmins(): void {
    this.isHidden = false;
    this.getAdmins();
  }

  // Fetch the list of admins with pagination
  getAdmins(pageIndex: number = 0, pageSize: number = 10): void {
    // Call service to get data with page index and page size
    this.adminService.getListOfAdmins(pageIndex, pageSize, this.isHidden).subscribe(
      (response: any) => {
        this.admins = response.content;
        this.totalAdminsCount = response.totalElements;
        this.dataSource.data = this.admins;
      },
      (error) => {
        console.error('Error fetching admins:', error);
      }
    );
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
    this.getAdmins(0, this.pageSize);  // Reset to page 0 when changing tabs
  }

  // Handle row selection
  onSelect(event: any, admin: Admin): void {
    if (event.checked) {
      this.selection.select(admin);
    } else {
      this.selection.deselect(admin);
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
  activateAdmin(adminId: string): void {
    Swal.fire({
      icon: 'info',
      title: 'Are you sure you want to activate this admin?',
      showCancelButton: true,
      confirmButtonText: 'Activate',
    }).then((result) => {
      if (result.isConfirmed) {
        this.adminService.enableAdmin(adminId).subscribe(() => {
          Swal.fire('Activated!', 'Admin has been activated.', 'success');
          this.getAdmins();  // Refresh data after activation
        }, (error) => {
          Swal.fire('Error', 'Something went wrong', 'error');
        });
      }
    });
  }

  // Deactivate admin
  deactiveAdmin(adminId: string): void {
    Swal.fire({
      icon: 'info',
      title: 'Are you sure you want to Deactivate this admin?',
      showCancelButton: true,
      confirmButtonText: 'Deactivate',
    }).then((result) => {
      if (result.isConfirmed) {
        this.adminService.disableAdmin(adminId, false).subscribe(() => {
          Swal.fire('Deactivated!', 'Admin has been deactivated.', 'success');
          this.getAdmins();  // Refresh data after deactivation
        }, (error) => {
          Swal.fire('Error', 'Something went wrong', 'error');
        });
      }
    });
  }

  // Handle page change event
  pageChanged(event: any): void {
    this.getAdmins(event.pageIndex, event.pageSize);
  }
}
