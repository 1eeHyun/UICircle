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
