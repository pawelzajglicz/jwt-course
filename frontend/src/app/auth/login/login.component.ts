import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { NotificationService } from 'src/app/notification-module/notification.service';
import { HeaderType } from '../enum/header-type.enum';
import { User } from '../model/user';
import { AuthenticationService } from '../service/authentication.service';

@Component({
  selector: 'nim-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  showLoading: boolean;

  constructor(private authenticationService: AuthenticationService,
    private notificationService: NotificationService,
    private router: Router) { }

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/user/management');
    } else {
      this.router.navigateByUrl('/');
    }
  }

  onLogin(user: User) {
    this.showLoading = true;
    console.log(user);
    this.authenticationService.logIn(user)
    .pipe(
      finalize(() => this.showLoading = false)
    )
    .subscribe({
      next: (response: HttpResponse<User>) => {
        const token = response.headers.get(HeaderType.JWT_TOKEN);
        this.authenticationService.saveToken(token);
        this.authenticationService.addUserToLocalCache(response.body);
        this.router.navigateByUrl('/user/management');
      },
      error: (errorResponse: HttpErrorResponse) => {
        console.log(errorResponse);
        this.notificationService.error(errorResponse.error.message);
      }}
      );
  }

}
