import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  let router = inject(Router);
  if(localStorage.getItem('token') != null)
  {
    return true;
  }
  else
  {
    router.navigateByUrl('Login');
    return false;
  }
};
