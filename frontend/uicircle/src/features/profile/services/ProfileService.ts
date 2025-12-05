import instance from "@/api/axios";

export interface ProfileResponse {
  publicId: string;
  username: string;
  displayName: string;
  avatarUrl: string | null;
  bannerUrl: string | null;
  bio: string | null;
  major: string | null;
  soldCount: number;
  buyCount: number;
  createdAt: string;
}

export interface UpdateProfileRequest {
  displayName?: string;
  bio?: string;
  major?: string;
}

// Get current user's profile
export const getMyProfile = async (): Promise<ProfileResponse> => {
  const res = await instance.get<{ success: boolean; data: ProfileResponse }>("/profile/me");
  return res.data.data;
};

// Get public profile by public ID
export const getPublicProfile = async (publicId: string): Promise<ProfileResponse> => {
  const res = await instance.get<{ success: boolean; data: ProfileResponse }>(`/profile/public/${publicId}`);
  return res.data.data;
};

// Get profile by username
export const getProfileByUsername = async (username: string): Promise<ProfileResponse> => {
  const res = await instance.get<{ success: boolean; data: ProfileResponse }>(`/profile/${username}`);
  return res.data.data;
};

// Update profile
export const updateProfile = async (request: UpdateProfileRequest): Promise<ProfileResponse> => {
  const res = await instance.put<{ success: boolean; data: ProfileResponse }>("/profile/me", request);
  return res.data.data;
};

// Upload avatar
export const uploadAvatar = async (file: File): Promise<ProfileResponse> => {
  const formData = new FormData();
  formData.append("file", file);
  
  const res = await instance.post<{ success: boolean; data: ProfileResponse }>("/profile/me/avatar", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
  return res.data.data;
};

// Upload banner
export const uploadBanner = async (file: File): Promise<ProfileResponse> => {
  const formData = new FormData();
  formData.append("file", file);
  
  const res = await instance.post<{ success: boolean; data: ProfileResponse }>("/profile/me/banner", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
  return res.data.data;
};
