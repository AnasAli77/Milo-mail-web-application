import { CommonModule, JsonPipe } from '@angular/common';
import { Component, EventEmitter, inject, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApiAuthService } from '../../Services/api-auth-service';
import { ClientUser } from '../../models/ClientUser';
import { UserService } from '../../Services/user-service';
import { Alert } from '../../Services/alert';


@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, JsonPipe, RouterModule, CommonModule],
  templateUrl: './loginComponent.html',
  styleUrl: './loginComponent.css',
})
export class loginComponent {

  userService = inject(UserService);
  route = inject(Router);
  authLoginForm: FormGroup;
  private fb = inject(FormBuilder);
  alert = inject(Alert);

  constructor(private _ApiAuthService: ApiAuthService) {
    this.authLoginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  // Toggle between Login and Sign Up

  onSubmit() {
    const loginData: ClientUser = {
      email: this.authLoginForm.get('email')?.value,
      password: this.authLoginForm.get('password')?.value,
    };
    this._ApiAuthService.login(loginData).subscribe({
      next: (response) => {
        console.log("Response: ");
        console.log(response);

        if (response.status === 200 && response.body) {
          console.log('Login successful', response);
          const responseBody = response.body;
          if (responseBody.token) {
            sessionStorage.setItem('auth_token', responseBody.token);
          }
          if (responseBody.email && responseBody.name) {
            this.userService.currentUser = {
              name: responseBody.name,
              email: responseBody.email,
              token: responseBody.token
            };
            sessionStorage.setItem('name', responseBody.name);
            sessionStorage.setItem('email', responseBody.email);
          }
          this.route.navigateByUrl('/layout');
          this.alert.loginSuccess();
        }
        else {
          this.alert.loginFail();
        }
      },
      error: (err) => {
        console.error('Login failed', err)
        this.alert.loginFail();
      }
    });
  }
}