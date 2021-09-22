import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AuthenticationGuard } from './auth/guard/authentication.guard';
import { AuthInterceptor } from './auth/interceptor/auth.interceptor';
import { AuthenticationService } from './auth/service/authentication.service';
import { UserService } from './auth/service/user.service';
import { NavigationComponent } from './navigation/navigation.component';
import { UpperPanelComponent } from './upper-panel/upper-panel.component';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    UpperPanelComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    HttpClientModule
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
