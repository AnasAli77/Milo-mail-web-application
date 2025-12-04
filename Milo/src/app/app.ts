import { Component, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { loginComponent } from './components/login/loginComponent';
import { SignUpComponent } from './components/sign-up-component/sign-up-component';
import { CommonModule } from '@angular/common';
import { Layout } from './components/layout/layout';
import { EmailList } from './components/email-list/email-list';
import { Header } from './components/header/header';
import { Sidebar } from './components/sidebar/sidebar';

// If you have a union type or enum for views:
type ViewState = 'login' | 'signup' | 'main';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, loginComponent, SignUpComponent, RouterLink, CommonModule, Layout, Header, EmailList, Sidebar,],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  currentView = signal<ViewState>('login');

  switchView(view: ViewState) {
    this.currentView.set(view);
  }
}
