import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  imports: [CommonModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {

  @Input() collapsed = false;
  @Output() onToggleSidebar = new EventEmitter<void>();
  @Output() onSignOut = new EventEmitter<void>();

  isProfileOpen = signal(false);

  toggleProfile() {
    this.isProfileOpen.update(v => !v);
  }
}
