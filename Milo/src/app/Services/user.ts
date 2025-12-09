import { Injectable } from '@angular/core';
import { ClientUser } from '../models/ClientUser';

@Injectable({
  providedIn: 'root',
})
export class User {
  private currentUser!: ClientUser;

  constructor() {
  }
  
  getEmail() {

  }
}