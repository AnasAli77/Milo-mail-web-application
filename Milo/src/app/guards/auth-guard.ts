import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../Services/user-service';

export const authGuard: CanActivateFn = (route, state) => {
  let userService = inject(UserService);
  let router = inject(Router);
  if(sessionStorage.getItem('auth_token') === userService.currentUser.token)
  {
    return true;
  }
  else
  {
    router.navigateByUrl('Login');
    return false;
  }
};
