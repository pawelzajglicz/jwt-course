import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { finalize } from 'rxjs';

import { User } from '../model/user';
import { UserService } from '../service/user.service';
import { NotificationService } from 'src/app/notification-module/notification.service';

@Component({
  selector: 'nim-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {

  refreshing: boolean;
  title = 'Users';
  users: User[];

  dataSource: MatTableDataSource<User>;
  columns = [
    {
      columnDef: 'id',
      header: 'User ID',
      cell: (user: User) => `${user.userId}`
    },
    {
      columnDef: 'firstName',
      header: 'First Name',
      cell: (user: User) => `${user.firstName}`
    },
    {
      columnDef: 'lastName',
      header: 'Last Name',
      cell: (user: User) => `${user.lastName}`
    },
    {
      columnDef: 'username',
      header: 'Username',
      cell: (user: User) => `${user.username}`
    },
    {
      columnDef: 'email',
      header: 'Email',
      cell: (user: User) => `${user.email}`
    },
    {
      columnDef: 'status',
      header: 'Status',
      cell: (user: User) => user.active ? 'Active' : 'Inactive'
    }
  ];
  displayedColumns: string[];

  constructor(private notificationService: NotificationService,
              private userService: UserService) { }

  ngOnInit(): void {
    this.getUsers(true);
  }

  getUsers(showNotification: boolean) {
    this.refreshing = true;
    this.userService.getUsers()
      .pipe(
        finalize(() => this.refreshing = false)
      )
      .subscribe({
        next: (users: User[]) => {
          this.userService.addUsersToLocalCache(users);
          this.users = users;
          this.dataSource = new MatTableDataSource(users);
          this.initDisplayedColumns();
          if (showNotification) {
            this.notificationService.success(`${users.length} user(s) loaded successfully`);
          }
        },
        error: (errorResponse: HttpErrorResponse) => this.notificationService.error(errorResponse.error.message)
      }
      )
  }

  onSelectUser(user: User) {

  }

  setTitle(title: string) {
    this.title = title;
  }

  private initDisplayedColumns() {
    this.displayedColumns = ['photo'];
    this.displayedColumns.push(...this.columns.map(c => c.columnDef));
    this.displayedColumns.push('actions');
  }
}
