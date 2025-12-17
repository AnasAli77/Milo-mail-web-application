import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const myToken = sessionStorage.getItem('auth_token'); 

  // 2. If token exists, clone the request and add the header
  if (myToken) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${myToken}`
      }
    });
    // Pass the cloned request to the next handler
    return next(clonedRequest);
  }

  // 3. If no token, pass the original request
  return next(req);
};