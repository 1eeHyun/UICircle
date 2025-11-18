import axios from "@/api/axios";
import { AUTH } from "@/constants/apiRoutes/auth";

export const login = (form) =>
  axios({ method: AUTH.LOGIN.method, url: AUTH.LOGIN.url, data: form });

export const signup = (form) =>
  axios({ method: AUTH.SIGNUP.method, url: AUTH.SIGNUP.url, data: form });

export const fetchMe = () =>
  axios({ method: AUTH.ME.method, url: AUTH.ME.url });

export { login, fetchMe, signup };
