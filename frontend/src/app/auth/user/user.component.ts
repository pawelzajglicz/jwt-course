import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
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
          if (showNotification) {
            this.notificationService.success(`${users.length} user(s) loaded successfully`);
          }
        },
        error: (errorResponse: HttpErrorResponse) => this.notificationService.error(errorResponse.error.message)
      }
      )
  }

  setTitle(title: string) {
    this.title = title;
  }
}
