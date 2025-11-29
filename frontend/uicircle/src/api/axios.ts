// src/api/axios.ts
import axios, { AxiosError } from "axios";

/** Normalize base URL and append /api prefix once. */
const RAW_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const BASE = RAW_BASE.replace(/\/+$/, "");        // strip trailing slashes
const API_PREFIX = "/api";
const baseURL = `${BASE}${API_PREFIX}`;           // e.g. http://localhost:8080/api

/** Create a shared axios instance. */
const instance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
  // withCredentials: true, // enable only if your API uses cookies
});

/** Attach bearer token (if present) on every request. */
instance.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }

  if (config.data instanceof FormData) {
    if (config.headers && "Content-Type" in config.headers) {
      delete config.headers["Content-Type"];
    }
  }
  return config;
});

/** Safely extract the pathname for an axios error (handles relative URLs). */
const getPathname = (error: AxiosError) => {
  const url = error.config?.url || "";
  const base = instance.defaults.baseURL || "http://localhost";
  return new URL(url, base).pathname;
};

/** Global 401/403 handling with ignore list (keep full '/api' paths here). */
instance.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const currentPath = window.location.pathname;
    const isUnauthorized = status === 401;
    const isOnLoginPage = currentPath === "/";

    const ignore401Prefixes = [
      "/api/auth",  // /api/auth/me, /api/auth/verify-email, /api/auth/...
      "/auth",    
    ];

    const pathname = getPathname(error);
    const shouldIgnore = ignore401Prefixes.some((p) =>
      pathname === p || pathname.startsWith(p + "/")
    );

    if (isUnauthorized && !shouldIgnore && !isOnLoginPage) {
      localStorage.removeItem("token");
      localStorage.removeItem("username");
      window.location.href = "/";
    }

    return Promise.reject(error);
  }
);

type ApiRequestArgs = {
  method: string;
  url: string;
  data?: unknown;
  params?: unknown;
};

export const apiRequest = async ({ method, url, data, params }: ApiRequestArgs) => {
  try {
    const res = await instance.request({ method, url, data, params });
    return res.data;
  } catch (error) {
    console.error("API request failed:", error);
    throw error;
  }
};

export default instance;
