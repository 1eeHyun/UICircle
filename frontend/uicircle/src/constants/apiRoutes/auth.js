const AUTH = {
    SIGNUP: { method: "POST", url: "/auth/signup" },
    LOGIN: { method: "POST", url: "/auth/login" },
    LOGOUT: { method: "POST", url: "/auth/logout" },
    ME: { method: "GET", url: "/auth/me" },
    REFRESH_TOKEN: { method: "POST", url: "/auth/refresh-token" },
}