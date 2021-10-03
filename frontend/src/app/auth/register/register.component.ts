import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { NotificationService } from 'src/app/notification-module/notification.service';
import { User } from '../model/user';
import { AuthenticationService } from '../service/authentication.service';

@Component({
  selector: 'nim-register',
  templateUrl: './register.component.html',
  styleUrls: ['../../styles/form-block.scss']
})
export class RegisterComponent implements OnInit {

  showLoading: boolean;

  constructor(private authenticationService: AuthenticationService,
              private notificationService: NotificationService,
              private router: Router) { }

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/user/management');
    }
  }

  onRegister(user: User) {
    this.showLoading = true;
    this.authenticationService.register(user)
      .pipe(
        finalize(() => this.showLoading = false)
      )
      .subscribe({
        next: (userResponse: User) => {
          this.notificationService.success(`A new account was created for ${userResponse.firstName}.
        Please check your email for password to log in.`);
        },
        error: (errorResponse: HttpErrorResponse) => this.notificationService.error(errorResponse.error.message)
      }
    );
  }
}
