import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserService } from '../Services/user-service';

export const isNotLoginGuard: CanActivateFn = (route, state) => {
  let userService = inject(UserService);
  let router = inject(Router);
  const token = sessionStorage.getItem('auth_token');
  if (!token) {
    return true;
  }
  else {
    router.navigateByUrl('/layout');
    return false;
  }
}