import { jwtDecode } from "jwt-decode";
import { useCookies } from "vue3-cookies";
import { computed } from "vue";

interface DecodedToken {
  sub: string;
  roles: Authority[];
  exp: number;
  userId: number;
  username: string;
  firstname: string;
  lastname: string;
}

interface Authority {
  authority: string;
}

export const useAuth = () => {
  const { cookies } = useCookies();

  const getToken = (): string | null => {
    return cookies.get("jwt");
  };

  const setToken = (token: string): void => {
    const decoded = jwtDecode<DecodedToken>(token);
    const cookieConfig = {
      expires: new Date(decoded.exp * 1000),
      path: "/",
      domain: "",
      secure: false,
      sameSite: "Strict" as const,
    };

    cookies.set(
      "jwt",
      token,
      cookieConfig.expires,
      cookieConfig.path,
      cookieConfig.domain,
      cookieConfig.secure,
      cookieConfig.sameSite,
    );
  };

  const clearToken = (): void => {
    cookies.remove("jwt");
  };

  const getRoles = (): string[] => {
    const token = getToken();
    if (!token) return [];
    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return decoded.roles?.map((roleObj) => roleObj.authority) || [];
    } catch {
      return [];
    }
  };

  const hasRole = (role: string): boolean => {
    const roles = getRoles();
    return roles.includes(role);
  };

  const getUserId = (): number | null => {
    const token = getToken();
    if (!token) return null;
    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return decoded.userId || null;
    } catch {
      return null;
    }
  };

  const getUsername = (): string | null => {
    const token = getToken();
    if (!token) return null;
    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return decoded.username || null;
    } catch {
      return null;
    }
  };

  const getUserData = () => {
    const token = getToken();
    if (!token) return null;

    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return {
        id: decoded.userId,
        username: decoded.username,
        firstname: decoded.firstname,
        lastname: decoded.lastname,
        roles: decoded.roles?.map((roleObj) => roleObj.authority) || [],
      };
    } catch {
      return null;
    }
  };

  const isAuthenticated = computed(() => {
    const token = getToken();
    if (!token) return false;

    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return Date.now() < decoded.exp * 1000;
    } catch {
      return false;
    }
  });

  const isAdmin = computed(() => {
    const token = getToken();
    if (!token) return false;

    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return (
        Date.now() < decoded.exp * 1000 &&
        decoded.roles.some((role) => role.authority === "ROLE_ADMIN")
      );
    } catch {
      return false;
    }
  });

  return {
    getToken,
    setToken,
    clearToken,
    getRoles,
    hasRole,
    getUserId,
    getUsername,
    getUserData,
    isAuthenticated,
    isAdmin,
  };
};
