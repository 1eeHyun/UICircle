import axios from "axios";
import { AUTH } from "@/constants/apiRoutes/auth.routes";

const login = (form: any) =>
  axios({ method: AUTH.LOGIN.method, url: AUTH.LOGIN.url, data: form });

const signup = (form: { email: string; password: string; }) =>
  axios({ method: AUTH.SIGNUP.method, url: AUTH.SIGNUP.url, data: form });

const fetchMe = () =>
  axios({ method: AUTH.ME.method, url: AUTH.ME.url });

export { login, fetchMe, signup };
