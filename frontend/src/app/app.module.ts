import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table'
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AuthenticationGuard } from './auth/guard/authentication.guard';
import { AuthInterceptor } from './auth/interceptor/auth.interceptor';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { AuthenticationService } from './auth/service/authentication.service';
import { UserComponent } from './auth/user/user.component';
import { UserInfoDialogComponent } from './auth/user/info-dialog/user-info-dialog/user-info-dialog.component';
import { UserService } from './auth/service/user.service';
import { NavigationComponent } from './navigation/navigation.component';
import { NotificationModule } from './notification-module/notification.module';
import { UpperPanelComponent } from './upper-panel/upper-panel.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    NavigationComponent,
    RegisterComponent,
    UpperPanelComponent,
    UserComponent,
    UserInfoDialogComponent
  ],
  entryComponents: [UserInfoDialogComponent],
  imports: [
    AppRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    MatDialogModule,
    MatTableModule,
    NotificationModule
  ],
  providers: [
    AuthenticationGuard,
    AuthenticationService,
    UserService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
