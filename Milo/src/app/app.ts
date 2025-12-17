import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

type ViewState = 'login' | 'signup' | 'main';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  currentView = signal<ViewState>('login');

  switchView(view: ViewState) {
    this.currentView.set(view);
  }
}
