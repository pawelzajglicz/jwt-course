import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { finalize } from 'rxjs';

import { NotificationService } from 'src/app/notification-module/notification.service';
import { UserInfoDialogComponent } from './info-dialog/user-info-dialog/user-info-dialog.component';
import { UserAddDialogComponent } from './user-add-dialog/user-add-dialog.component';
import { UserAddedInfo } from './user-add-dialog/user-added-info';
import { UserDeleteDialogComponent } from './user-delete-dialog/user-delete-dialog.component';
import { UserDeletedInfo } from './user-delete-dialog/user-deleted-info';
import { UserEditDialogComponent } from './user-edit-dialog/user-edit-dialog/user-edit-dialog.component';
import { User } from '../model/user';
import { UserService } from '../service/user.service';

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

  constructor(private dialog: MatDialog,
              private notificationService: NotificationService,
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

  onAddUser() {
    this.dialog.open(UserAddDialogComponent, {
      autoFocus: false,
      disableClose: true,
      maxHeight: '600px',
      maxWidth: '700px',
      panelClass: 'custom-dialog-container',
      width: 'fit-content'
    }).afterClosed().subscribe((result: UserAddedInfo) => {
      if (result.wasUserAdded) {
        this.getUsers(false);
      }
    });
  }

  onDeleteUser(deleteUser: User) {
    this.dialog.open(UserDeleteDialogComponent, {
      autoFocus: false,
      data: deleteUser,
      disableClose: true,
      maxHeight: '600px',
      maxWidth: '700px',
      panelClass: 'custom-dialog-container',
      width: 'fit-content'
    }).afterClosed().subscribe((result: UserDeletedInfo) => {
      if (result.wasUserDeleted) {
        this.getUsers(false);
      }
    });
  }

  onEditUser(editUser: User) {
    this.dialog.open(UserEditDialogComponent, {
      autoFocus: false,
      data: editUser,
      disableClose: true,
      maxHeight: '600px',
      maxWidth: '700px',
      panelClass: 'custom-dialog-container',
      width: 'fit-content'
    });
  }

  onSelectUser(user: User) {
      this.dialog.open(UserInfoDialogComponent, {
        autoFocus: false,
        data: user,
        disableClose: true,
        maxHeight: '600px',
        maxWidth: '700px',
        panelClass: 'custom-dialog-container',
        width: 'fit-content'
      });
  }

  searchUsers(searchTerm: string) {
    this.dataSource.filter = searchTerm;
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
