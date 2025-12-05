import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [CommonModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {

  @Input() collapsed = false;
  @Output() onToggleSidebar = new EventEmitter<void>();

  route = inject(Router);
  isProfileOpen = signal(false);
  signOut(){
    localStorage.clear();
    this.route.navigateByUrl("");
  }
  toggleProfile() {
    this.isProfileOpen.update(v => !v);
  }
}
