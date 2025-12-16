import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class Alert {
  // --- DARK THEME COLORS ---
  private readonly darkBackground = '#1f2937'; // Slate-800
  private readonly darkText = '#f9fafb';       // Gray-50
  private readonly confirmBtnColor = '#3b82f6'; // Blue-500 (Lighter blue for dark mode)
  private readonly cancelBtnColor = '#ef4444';  // Red-500

  // --- CONFIGURATION: TOAST MIXIN (Dark) ---
  private Toast = Swal.mixin({
    toast: true,
    position: 'top',
    showConfirmButton: false,
    timer: 2000,
    timerProgressBar: true,
    background: this.darkBackground,
    color: this.darkText,
    didOpen: (toast: HTMLElement) => {
      toast.addEventListener('mouseenter', Swal.stopTimer);
      toast.addEventListener('mouseleave', Swal.resumeTimer);
    }
  });

  // --- LOGIN ALERTS ---

  loginSuccess() {
    this.Toast.fire({
      icon: 'success',
      title: 'Login Successful',
      text: 'Welcome back!',
      iconColor: '#4ade80' // Green-400 (Light green for dark bg)
    });
  }

  loginFail(message: string = 'Invalid email or password') {
    Swal.fire({
      icon: 'error',
      title: 'Login Failed',
      text: message,
      background: this.darkBackground,
      color: this.darkText,
      confirmButtonColor: this.cancelBtnColor,
      confirmButtonText: 'Try Again'
    });
  }

  // --- SIGNUP ALERTS ---

  signupSuccess() {
    this.Toast.fire({
      icon: 'success',
      title: 'Registration Successful',
      text: 'Welcome to Milo!',
      iconColor: '#4ade80'
    });
  }

  signupFail(message: string = 'Something went wrong') {
    Swal.fire({
      icon: 'error',
      title: 'Signup Failed',
      text: message,
      background: this.darkBackground,
      color: this.darkText,
      confirmButtonColor: this.cancelBtnColor,
      confirmButtonText: 'Close'
    });
  }

  // --- EMAIL ALERTS ---

  emailSent() {
    this.Toast.fire({
      icon: 'success',
      title: 'Email Sent',
      iconColor: '#4ade80'
    });
  }

  emailSendFail() {
    this.Toast.fire({
      icon: 'error',
      title: 'Failed to send email',
      iconColor: '#ef4444'
    });
  }

  draftSaved() {
    this.Toast.fire({ icon: 'success', title: 'Draft Saved', iconColor: '#60a5fa' }); // Blue-400
  }

  emailDeleted() {
    this.Toast.fire({ icon: 'success', title: 'Email Deleted', iconColor: '#ef4444' });
  }

  emailsMoved(folderName: string) {
    this.Toast.fire({ icon: 'success', title: `Moved to ${folderName}`, iconColor: '#60a5fa' });
  }

  // --- FOLDER ACTIONS ---
  folderCreated(name: string) {
    this.Toast.fire({ icon: 'success', title: `Folder "${name}" Created`, iconColor: '#4ade80' });
  }

  folderRenamed() {
    this.Toast.fire({ icon: 'success', title: 'Folder Renamed', iconColor: '#4ade80' });
  }

  folderDeleted() {
    this.Toast.fire({ icon: 'success', title: 'Folder Deleted', iconColor: '#ef4444' });
  }

  // --- CONTACT ACTIONS ---
  contactSaved() {
    this.Toast.fire({ icon: 'success', title: 'Contact Saved', iconColor: '#4ade80' });
  }

  contactDeleted() {
    this.Toast.fire({ icon: 'success', title: 'Contact Deleted', iconColor: '#ef4444' });
  }

  // --- GENERIC ERROR ---
  error(message: string = 'Operation failed') {
    this.Toast.fire({ icon: 'error', title: message, iconColor: '#ef4444' });
  }
}