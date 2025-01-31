import { useAuth } from "@/util/auth";
import type { NavigationGuardNext, RouteLocationNormalized } from 'vue-router';

export const authGuard = (to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) => {
  const { getToken, hasRole } = useAuth();
  
  const publicPages = ['/login', '/register'];
  const authRequired = !publicPages.includes(to.path);
  
  if (authRequired && !getToken()) {
    return next('/login');
  }
  
  if (to.meta.roles) {
    const requiredRoles = to.meta.roles as string[];
    const hasRequiredRole = requiredRoles.some(role => hasRole(role));
    if (!hasRequiredRole) {
      return next('/unauthorized');
    }
  }
  
  next();
};