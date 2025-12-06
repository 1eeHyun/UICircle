// src/features/offers/types.ts
import type { ListingSummaryResponse } from "@/features/listings/services/ListingService";
import type { UserResponse } from "@/features/auth/services/AuthService";

export type OfferStatus = "PENDING" | "ACCEPTED" | "REJECTED" | "EXPIRED";

export interface PriceOfferResponse {
  publicId: string;
  listing: ListingSummaryResponse;
  buyer: UserResponse;
  amount: number;
  message: string | null;
  status: OfferStatus;
  createdAt: string;
  updatedAt: string;
}
