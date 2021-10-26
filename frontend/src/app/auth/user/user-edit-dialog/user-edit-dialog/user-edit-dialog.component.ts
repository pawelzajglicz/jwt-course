import { Component, Inject } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { User } from 'src/app/auth/model/user';
import { UserService } from 'src/app/auth/service/user.service';
import { NotificationService } from 'src/app/notification-module/notification.service';

@Component({
  selector: 'app-user-edit-dialog',
  templateUrl: './user-edit-dialog.component.html',
  styleUrls: ['./user-edit-dialog.component.scss']
})
export class UserEditDialogComponent {

  isAdmin = true;
  profileImageFile: File;
  startedUsername: string;

  constructor(private dialogRef: MatDialogRef<UserEditDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public editedUser: User,
              private notificationService: NotificationService,
              private userService: UserService) {

                this.startedUsername = editedUser.username;
              }

  onClose() {
    this.dialogRef.close({ wasUserAdded: false });
  }

  onProfileImageChange(event: any) {
    this.profileImageFile = event.target.files[0];
  }

  onUpdateUser(form: NgForm) {
    const formData = this.userService.createUserFormData(this.startedUsername, this.editedUser, this.editedUser.profileImage);
    this.userService.updateUser(formData).subscribe({
      next: (response: User) => {
        this.notificationService.success(`User ${response.username} added succesfully`);
        this.dialogRef.close({ wasUserAdded: true });
      },
      error: (error) => {
        console.log(error);
        this.notificationService.error(error);
      }
    });
  }
}
