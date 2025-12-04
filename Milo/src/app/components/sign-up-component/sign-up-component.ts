import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-signup',
  imports: [],
  templateUrl: './sign-up-component.html',
  styleUrl: './sign-up-component.css',
})
export class SignUpComponent {
  @Output() onCreate = new EventEmitter<void>();
  @Output() onLoginClick = new EventEmitter<void>();
}
