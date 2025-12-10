import { Injectable } from '@angular/core';
import { ClientUser } from '../models/ClientUser';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  currentUser: ClientUser = { name: sessionStorage.getItem("name") ?? '', email: sessionStorage.getItem("email") ?? '' };

  constructor() {
  }
  getName(): string {
    return this.currentUser?.name || '';
  }
  getEmail(): string {
    return this.currentUser?.email || '';
  }
}