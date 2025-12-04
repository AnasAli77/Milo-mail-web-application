import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Email } from '../../models/email';

@Component({
  selector: 'app-email-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './email-viewer.html',
  styleUrl: './email-viewer.css',
})
export class EmailViewComponent {
  @Input() email: Email | null = null;
}
