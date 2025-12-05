import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-signup',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './sign-up-component.html',
  styleUrl: './sign-up-component.css',
})
export class SignUpComponent {
  // @Output() onCreate = new EventEmitter<void>();
  // @Output() onLoginClick = new EventEmitter<void>();

  isLoginMode = true;
  authSignUpForm: FormGroup;
  private fb = inject(FormBuilder);
  constructor() {
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
    if (this.authSignUpForm.valid) {
      console.log(this.isLoginMode ? 'Logging in...' : 'Signing up...', this.authSignUpForm.value);
      // Add your Firebase/Backend logic here
    } else {
      this.authSignUpForm.markAllAsTouched();
    }
  }
}
