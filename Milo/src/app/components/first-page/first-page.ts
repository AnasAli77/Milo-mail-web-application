import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-first-page',
  imports: [],
  templateUrl: './first-page.html',
  styleUrl: './first-page.css',
})
export class FirstPage {
  constructor(private router: Router) {}
  goToLogin()
  {
    this.router.navigateByUrl('/Login');
  }
}
