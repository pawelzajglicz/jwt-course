import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';
import { User } from '../model/user';
import { BrowserStorageService } from './../../util/browser-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  public host: string = environment.apiUrl;
  private jwtHelper: JwtHelperService = new JwtHelperService();
  private loggedInUsername: string;
  private token: string;

  constructor(private browserStorage: BrowserStorageService,
              private http: HttpClient) {}

  addUserToLocalCache(user: User) {
    this.browserStorage.set('user', JSON.stringify(user));
  }

  getToken() {
    return this.token;
  }

  getUserFromLocalCache(): User {
    return JSON.parse(this.browserStorage.get('user'));
  }

  isUserLoggedIn() {
    this.loadToken();
    if (this.token != null && this.token !== '') {
      const tokenSubject = this.jwtHelper.decodeToken(this.token).sub;
      if (tokenSubject != null && tokenSubject !== '') {
        if (!this.jwtHelper.isTokenExpired(this.token)) {
            this.loggedInUsername = tokenSubject;
            return true;
        }
      }
    }

    this.logOut();
    return false;
  }

  loadToken() {
    this.token = this.browserStorage.get('token');
  }

  logIn(user: User) {
    return this.http.post<User>(`${this.host}/user/login`, user, {observe: 'response'});
  }

  logOut() {
    this.token = null;
    this.loggedInUsername = null;
    this.browserStorage.remove('user');
    this.browserStorage.remove('token');
    this.browserStorage.remove('users');
  }

  saveToken(token: string) {
    this.token = token;
    this.browserStorage.set('token', token);
  }

  register(user: User) {
    return this.http.post<User>(`${this.host}/user/register`, user);
  }
}
