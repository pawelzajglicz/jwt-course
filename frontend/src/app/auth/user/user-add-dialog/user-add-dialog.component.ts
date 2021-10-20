import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

import { NotificationService } from 'src/app/notification-module/notification.service';
import { User } from '../../model/user';
import { UserService } from '../../service/user.service';

@Component({
  selector: 'app-user-add-dialog',
  templateUrl: './user-add-dialog.component.html',
  styleUrls: ['./user-add-dialog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserAddDialogComponent implements OnInit {

  isAdmin = true;
  profileImageFile: File;

  constructor(private dialogRef: MatDialogRef<UserAddDialogComponent>,
    private notificationService: NotificationService,
    private userService: UserService) { }

  ngOnInit(): void {
  }

  onAddNewUser(userForm: NgForm) {
    const formData = this.userService.createUserFormData(null, userForm.value, this.profileImageFile);
    this.userService.addUser(formData).subscribe({
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

  onClose() {
    this.dialogRef.close({ wasUserAdded: false });
  }

  onProfileImageChange(event: any) {
    console.log(event);
    this.profileImageFile = event.target.files[0];
    console.log(this.profileImageFile)
  }
}
