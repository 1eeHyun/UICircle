import instance from "@/api/axios";

export interface LoginRequest {
  emailOrUsername: string;
  password: string;
}

export interface SignupRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  phoneNumber?: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: {
    username: string;
    email: string;
    firstName: string;
    middleName?: string;
    lastName: string;
  };
}

export const login = async (form: LoginRequest) => {
  const response = await instance.post<{ success: boolean; data: LoginResponse }>("/auth/login", form);
  return response.data;
};

export const signup = async (form: SignupRequest) => {
  const response = await instance.post<{ success: boolean }>("/auth/signup", form);
  return response.data;
};

export const resendVerificationEmail = async (email: string) => {
  const response = await instance.post<{ success: boolean }>("/auth/resend-verification", {
    email,
  });
  return response.data;
};
