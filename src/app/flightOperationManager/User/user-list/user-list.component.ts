import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { UserService } from '../../../services/user.service';

export interface User {
  email: string;
  enabled: boolean;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  username: string;
  id: string;
}

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent implements OnInit{

  selectedTab = 0;
  displayedColumns: string[] = ['select', 'First Name', 'Last Name', 'emailId', 'Phone Number', 'Username', 'Status'];
  dataSource: MatTableDataSource<User> = new MatTableDataSource<User>();
  users: User[] = [];
  selection: SelectionModel<User> = new SelectionModel<User>(false, []);
  totalUsersCount: number = 0;
  pageSize: number = 10;  // Default page size
  isHidden: boolean | undefined;

  constructor(private userService:UserService) {}

  ngOnInit(): void {
    this.getUserList();
  }

  getUserList(pageIndex: number = 0, pageSize: number = 10) : void {
    this.userService.getUserList(pageIndex, pageSize, this.isHidden).subscribe(
      (response : any ) => {
        this.users = response.content;
        this.totalUsersCount = response.totalElements;
        this.dataSource.data = this.users;
      },
      (error) => {
        console.error('Error fetching admins:', error);
      }
    );
  }

  onTabChange(index: number): void{}

  onSelect(event: any, user: User): void {
    if (event.checked) {
      this.selection.select(user);
    } else {
      this.selection.deselect(user);
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

  activateUsers(userId:String){}

  deactivateUsers(userId:String){}

  // Handle page change event
  pageChanged(event: any): void {
  }
}
