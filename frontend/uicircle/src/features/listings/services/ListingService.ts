import instance from "@/api/axios";

export interface CategoryResponse {
  categorySlug: string;
  name: string;
  parentSlug: string | null;
  children: CategoryResponse[];
}

export interface ListingSummaryResponse {
  publicId: string;
  title: string;
  description: string;
  price: number;
  status: string;
  createdAt: string;
  thumbnailUrl?: string;
  categoryName: string;
  sellerUsername: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface CreateListingRequest {
  title: string;
  description: string;
  price: number;
  condition: "NEW" | "LIKE_NEW" | "GOOD" | "FAIR" | "POOR";
  categorySlug: string;
  latitude: number;
  longitude: number;
}

export interface ProfileResponse {
  publicId: string;
  displayName: string;
  avatarUrl: string | null;
  bio: string | null;
  major: string | null;
  soldCount: number;
  buyCount: number;
}

export interface ListingResponse {
  publicId: string;
  title: string;
  description: string;
  price: number;
  condition: "NEW" | "LIKE_NEW" | "GOOD" | "FAIR" | "POOR";
  status: string;
  sellerProfile: ProfileResponse;
  category: {
    categorySlug: string;
    name: string;
  };
  latitude: number;
  longitude: number;
  isNegotiable: boolean;
  viewCount: number;
  favoriteCount: number;
  isFavorited: boolean;
  images: Array<{
    publicId: string;
    imageUrl: string;
  }>;
  createdAt: string;
  updatedAt: string;
}


export const getAllCategories = async () => {
  const response = await instance.get<{ success: boolean; data: CategoryResponse[] }>("/categories");
  return response.data.data;
};

export const getTopLevelCategories = async () => {
  const response = await instance.get<{ success: boolean; data: CategoryResponse[] }>("/categories/parent");
  return response.data.data;
};

export const getSubcategories = async (parentSlug: string) => {
  const response = await instance.get<{ success: boolean; data: CategoryResponse[] }>(
    `/categories/${parentSlug}/subcategories`
  );
  return response.data.data;
};

export const getAllActiveListings = async (
  page = 0,
  size = 20,
  sortBy = "createdAt",
  sortDirection = "DESC"
) => {
  const response = await instance.get<{ success: boolean; data: PageResponse<ListingSummaryResponse> }>(
    "/listings/active",
    {
      params: { page, size, sortBy, sortDirection },
    }
  );
  return response.data.data;
};

export const getListingsByCategory = async (
  categorySlug: string,
  page = 0,
  size = 20,
  sortBy = "createdAt",
  sortDirection = "DESC"
) => {
  const response = await instance.get<{ success: boolean; data: PageResponse<ListingSummaryResponse> }>(
    "/listings/category",
    {
      params: { categorySlug, page, size, sortBy, sortDirection },
    }
  );
  return response.data.data;
};

export const createListing = async (
  request: CreateListingRequest,
  images?: File[]
) => {
  const formData = new FormData();
  
  formData.append("request", new Blob([JSON.stringify(request)], { type: "application/json" }));
  
  if (images && images.length > 0) {
    images.forEach((image) => {
      formData.append("images", image);
    });
  }

  const response = await instance.post<{ success: boolean; data: ListingResponse }>(
    "/listings",
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );
  
  return response.data.data;
};

export const getListing = async (listingId: string) => {
  const response = await instance.get<{ success: boolean; data: ListingResponse }>(
    `/listings`,
    {
      params: { publicId: listingId }
    }
  );
  return response.data.data;
};

export const toggleFavorite = async (publicId: string) => {
  const res = await instance.post<{ success: boolean }>(
    `/listings/favorites/toggle`,
    null,
    { params: { publicId } }
  );
  return res.data.success;
};

export const getFavoriteCount = async (publicId: string) => {
  const res = await instance.get<{ success: boolean; data: number }>(
    `/listings/favorites/count`,
    { params: { publicId } }
  );
  return res.data.data;
};
