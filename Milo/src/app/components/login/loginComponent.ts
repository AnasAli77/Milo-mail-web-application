import { CommonModule, JsonPipe } from '@angular/common';
import { Component, EventEmitter, inject, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';


@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, JsonPipe, RouterModule, CommonModule],
  templateUrl: './loginComponent.html',
  styleUrl: './loginComponent.css',
})
export class loginComponent {
  // @Output() onLogin = new EventEmitter<void>();
  // @Output() onRegisterClick = new EventEmitter<void>();
  // loginForm: FormGroup
  // constructor() {
  //   this.loginForm = new FormGroup(
  //     {
  //       email: new FormControl('', [Validators.required]),
  //       password: new FormControl(''),
  //     }
  //   )
  // }
  route = inject(Router);
  isLoginMode = true;
  authLoginForm: FormGroup;
  private fb = inject(FormBuilder);
  constructor() {
    this.authLoginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  // Toggle between Login and Sign Up

  onSubmit() {
    if (this.authLoginForm.valid) {
      console.log(this.isLoginMode ? 'Logging in...' : 'Signing up...', this.authLoginForm.value);
      sessionStorage.setItem('token', 'aaaaaa61');
      // sessionStorage.setItem('tkn', 'saa61');
      this.route.navigateByUrl('/layout');

      // localStorage.clear();
      // Add your Firebase/Backend logic here
    } else {
      this.authLoginForm.markAllAsTouched();
    }
  }
}