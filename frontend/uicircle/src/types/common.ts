// src/types/common.ts
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface CommonResponse<T> {
  success: boolean;
  message?: string | null;
  data: T;
}
