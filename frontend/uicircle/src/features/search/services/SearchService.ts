import instance from "@/api/axios";
import { PageResponse, ListingSummaryResponse } from "@/features/listings/services/ListingService";

export interface SearchFilters {
  keyword?: string;
  categorySlug?: string;
  minPrice?: number;
  maxPrice?: number;
  condition?: "NEW" | "LIKE_NEW" | "GOOD" | "FAIR" | "POOR";
  status?: "ACTIVE" | "INACTIVE" | "SOLD";
  sortBy?: string;
  sortOrder?: "asc" | "desc";
  page?: number;
  size?: number;
}

export interface SaveSearchRequest {
  name: string;
  query?: string;
  filters: string; // JSON string
}

export interface SavedSearchResponse {
  publicId: string;
  name: string;
  query?: string;
  filters: string;
  createdAt: string;
}

export const searchListings = async (filters: SearchFilters) => {
  const response = await instance.post<{
    success: boolean;
    data: PageResponse<ListingSummaryResponse>;
  }>("/listings/search", filters);
  return response.data.data;
};

export const saveSearch = async (request: SaveSearchRequest) => {
  const response = await instance.post<{
    success: boolean;
    data: SavedSearchResponse;
  }>("/search", request);
  return response.data.data;
};

export const getSavedSearches = async () => {
  const response = await instance.get<{
    success: boolean;
    data: SavedSearchResponse[];
  }>("/search");
  return response.data.data;
};

export const deleteSavedSearch = async (publicId: string) => {
  await instance.delete<{ success: boolean }>(`/search/${publicId}`);
};

export const deleteAllSavedSearches = async () => {
  await instance.delete<{ success: boolean }>("/search");
};

