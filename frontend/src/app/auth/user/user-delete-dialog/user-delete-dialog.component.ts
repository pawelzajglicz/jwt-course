import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { NotificationService } from 'src/app/notification-module/notification.service';
import { CustomHttpRespone } from '../../model/custom-http-response';
import { User } from '../../model/user';
import { UserService } from '../../service/user.service';

@Component({
  selector: 'app-user-delete-dialog',
  templateUrl: './user-delete-dialog.component.html',
  styleUrls: ['./user-delete-dialog.component.scss']
})
export class UserDeleteDialogComponent {

  constructor(private dialogRef: MatDialogRef<UserDeleteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public deleteUser: User,
    private notificationService: NotificationService,
    private userService: UserService) { }

  cancel() {
    this.dialogRef.close();
  }

  confirm() {
    this.userService.deleteUser(this.deleteUser.userId).subscribe({
      next: (response: CustomHttpRespone) => {
        this.notificationService.success(response.message);
        this.dialogRef.close({ wasUserDeleted: true })
      },
      error: (error: HttpErrorResponse) => this.notificationService.error(`Deleting user '${this.deleteUser.username}' failed.`)
    }
    )
  }
}
