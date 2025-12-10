import { Injectable } from '@angular/core';
import { ClientUser } from '../models/ClientUser';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  currentUser!: ClientUser;

  constructor() {
  }
  getName(): string {
    return this.currentUser?.name || '';
  }
  getEmail(): string {
    return this.currentUser?.email || '';
  }
}