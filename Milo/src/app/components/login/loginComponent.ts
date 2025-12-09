import { CommonModule, JsonPipe } from '@angular/common';
import { Component, EventEmitter, inject, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiAuthService } from '../../Services/api-auth-service';
import { ClientUser } from '../../models/ClientUser';


@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, JsonPipe, RouterModule, CommonModule],
  templateUrl: './loginComponent.html',
  styleUrl: './loginComponent.css',
})
export class loginComponent {

  route = inject(Router);
  isLoginMode = true;
  authLoginForm: FormGroup;
  private fb = inject(FormBuilder);
  constructor(private _ApiAuthService: ApiAuthService) {
    this.authLoginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  // Toggle between Login and Sign Up

  onSubmit() {
    if (this.authLoginForm.valid) {
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