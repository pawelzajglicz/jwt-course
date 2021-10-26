import { HttpClient, HttpErrorResponse, HttpEvent, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { BrowserStorageService } from 'src/app/util/browser-storage.service';
import { environment } from 'src/environments/environment';
import { CustomHttpRespone } from '../model/custom-http-response';
import { User } from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private host: string = environment.apiUrl;

  constructor(private browserStorage: BrowserStorageService,
              private http: HttpClient) { }

  addUser(formData: FormData) {
    return this.http.post<User>(`${this.host}/user`, formData);
  }

  addUsersToLocalCache(users: User[]) {
    this.browserStorage.set('users', JSON.stringify(users));
  }

  createUserFormData(loggedInUsername: string, user: User, profileImage: File) {
    const formData = new FormData();
    formData.append('currentUsername', loggedInUsername);
    formData.append('firstName', user.firstName);
    formData.append('lastName', user.lastName);
    formData.append('username', user.username);
    formData.append('email', user.email);
    formData.append('role', user.role);
    formData.append('profileImage', profileImage);
    formData.append('isActive', JSON.stringify(user.active));
    formData.append('isNonLocked', JSON.stringify(user.notLocked));

    return formData;
  }

  deleteUser(userId: number | string) {
    return this.http.delete<CustomHttpRespone>(`${this.host}/user/${userId}`);
  }

  getUsers() {
    return this.http.get<User[]>(`${this.host}/user`);
  }

  getUsersFromLocalCache(): User[] {
    if (this.browserStorage.get('users')) {
      return JSON.parse(this.browserStorage.get('users'));
    }
    return null;
  }

  resetPassword(email: string) {
    this.http.get<CustomHttpRespone>(`${this.host}/user/resetpassword/${email}`);
  }

  updateProfileImage(formData: FormData): Observable<HttpEvent<User>> {
    return this.http.put<User>(`${this.host}/user/update-profile-image`, formData,
    {
      reportProgress: true,
      observe: 'events'
    });
  }

  updateUser(formData: FormData) {
    return this.http.patch<User>(`${this.host}/user`, formData);
  }
}
