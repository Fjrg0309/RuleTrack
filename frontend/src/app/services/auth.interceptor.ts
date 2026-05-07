import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Interceptor HTTP que añade automáticamente el token JWT de autorización
 * a todas las peticiones dirigidas a la API del backend (/api/).
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('rt_token');
  if (token && req.url.includes('/api/')) {
    const cloned = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(cloned);
  }
  return next(req);
};
