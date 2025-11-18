import api from "../../api/axios";

const AuthService = {
  login: (email, password) =>
    api.post("/auth/login", { email, password }),

  logout: (refreshToken) =>
    api.post("/auth/logout", refreshToken),

  signup: (data) =>
    api.post("/auth/signup", data),
};

export { AuthService };
