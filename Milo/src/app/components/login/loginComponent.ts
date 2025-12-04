import { JsonPipe } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, JsonPipe, RouterModule],
  templateUrl: './loginComponent.html',
  styleUrl: './loginComponent.css',
})
export class loginComponent {
  @Output() onLogin = new EventEmitter<void>();
  @Output() onRegisterClick = new EventEmitter<void>();
  // loginForm: FormGroup
  // constructor() {
  //   this.loginForm = new FormGroup(
  //     {
  //       email: new FormControl('', [Validators.required]),
  //       password: new FormControl(''),
  //     }
  //   )
  // }
}