import { Injectable } from '@angular/core';
import { NotifierService } from 'angular-notifier';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private notifier: NotifierService) { }

  default(message: string) {
    this.notifier.notify('default', message);
  }

  error(message: string = 'An error occured.') {
    this.notifier.notify('error', message);
  }

  info(message: string) {
    this.notifier.notify('info', message);
  }

  success(message: string) {
    this.notifier.notify('success', message);
  }

  warning(message: string) {
    this.notifier.notify('warning', message);
  }
}
