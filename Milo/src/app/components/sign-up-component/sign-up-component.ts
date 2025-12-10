import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ClientUser } from '../../models/ClientUser';
import { UserService } from '../../Services/user-service';
import { ApiAuthService } from '../../Services/api-auth-service';

@Component({
  selector: 'app-signup',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './sign-up-component.html',
  styleUrl: './sign-up-component.css',
})
export class SignUpComponent {

  route = inject(Router);
  userService = inject(UserService);

  authSignUpForm: FormGroup;
  private fb = inject(FormBuilder);

  constructor(private _ApiAuthService: ApiAuthService) {
    this.authSignUpForm = this.fb.group({
      fullName: [''],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],

      confirmPassword: ['', [Validators.required]],
    },
      { validators: [this.passwordsMatchValidator] } // group-level validator
    );
  }


  private passwordsMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('password')?.value;
    const confirm = group.get('confirmPassword')?.value;

    // Don't show mismatch until both fields have values
    if (!password || !confirm) return null;

    return password === confirm ? null : { passwordMismatch: true };
  }


  onSubmit() {
    const registerData: ClientUser = {
      name: this.authSignUpForm.get('name')?.value,
      email: this.authSignUpForm.get('email')?.value,
      password: this.authSignUpForm.get('password')?.value,
    };
    this._ApiAuthService.register(registerData).subscribe({
      next: (response) => {
        console.log("Response: " + response);

        if (response.status === 201 && response.body) {
          console.log('Register successful', response);
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
        }
        else {
          alert("5555555555555555555555");
        }
      },
      error: (err) => console.error('Register failed', err)
    });
  }
}
