import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { User } from 'src/app/auth/model/user';

@Component({
  selector: 'app-user-info-dialog',
  templateUrl: './user-info-dialog.component.html',
  styleUrls: ['./user-info-dialog.component.scss']
})
export class UserInfoDialogComponent  {

  constructor(private dialogRef: MatDialogRef<UserInfoDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public user: User) {}

  onClose() {
    this.dialogRef.close({ wasUserAdded: false });
  }
}
