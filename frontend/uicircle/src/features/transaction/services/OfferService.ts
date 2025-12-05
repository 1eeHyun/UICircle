import instance from "@/api/axios";

// Backend response types (nested structure)
interface ListingSummary {
  publicId: string;
  title: string;
  price: number;
  thumbnailUrl: string | null;
  status: string;
}

interface UserSummary {
  publicId: string;
  username: string;
  displayName?: string;
  avatarUrl?: string | null;
}

interface BackendOfferResponse {
  publicId: string;
  listing: ListingSummary;
  buyer: UserSummary;
  amount: number;
  message: string | null;
  status: "PENDING" | "ACCEPTED" | "REJECTED" | "CANCELLED";
  createdAt: string;
  updatedAt: string;
}

// Frontend-friendly flattened type
export interface PriceOfferResponse {
  publicId: string;
  listingPublicId: string;
  listingTitle: string;
  listingThumbnailUrl: string | null;
  listingPrice: number;
  buyerUsername: string;
  buyerDisplayName: string;
  buyerAvatarUrl: string | null;
  amount: number;
  message: string | null;
  status: "PENDING" | "ACCEPTED" | "REJECTED" | "CANCELLED";
  createdAt: string;
  updatedAt: string;
}

// Transform backend response to frontend format
const transformOffer = (offer: BackendOfferResponse): PriceOfferResponse => ({
  publicId: offer.publicId,
  listingPublicId: offer.listing?.publicId || "",
  listingTitle: offer.listing?.title || "Unknown Listing",
  listingThumbnailUrl: offer.listing?.thumbnailUrl || null,
  listingPrice: offer.listing?.price || 0,
  buyerUsername: offer.buyer?.username || "Unknown",
  buyerDisplayName: offer.buyer?.displayName || offer.buyer?.username || "Unknown",
  buyerAvatarUrl: offer.buyer?.avatarUrl || null,
  amount: offer.amount,
  message: offer.message,
  status: offer.status,
  createdAt: offer.createdAt,
  updatedAt: offer.updatedAt,
});

export interface CreateOfferRequest {
  amount: number;
  message?: string;
}

export interface UpdateOfferStatusRequest {
  status: "ACCEPTED" | "REJECTED";
  note?: string;
}

// Get all offers received by the current user (as a seller)
export const getReceivedOffers = async (): Promise<PriceOfferResponse[]> => {
  const res = await instance.get<{ success: boolean; data: BackendOfferResponse[] }>(
    "/offers/received"
  );
  return res.data.data.map(transformOffer);
};

// Get all offers sent by the current user (as a buyer)
export const getSentOffers = async (): Promise<PriceOfferResponse[]> => {
  const res = await instance.get<{ success: boolean; data: BackendOfferResponse[] }>(
    "/offers/sent"
  );
  return res.data.data.map(transformOffer);
};

// Get offers for a specific listing
export const getOffersForListing = async (listingPublicId: string): Promise<PriceOfferResponse[]> => {
  const res = await instance.get<{ success: boolean; data: BackendOfferResponse[] }>(
    `/listings/${listingPublicId}/offers`
  );
  return res.data.data.map(transformOffer);
};

// Create an offer for a listing
export const createOffer = async (
  listingPublicId: string,
  request: CreateOfferRequest
): Promise<PriceOfferResponse> => {
  const res = await instance.post<{ success: boolean; data: BackendOfferResponse }>(
    `/listings/${listingPublicId}/offers`,
    request
  );
  return transformOffer(res.data.data);
};

// Accept an offer (seller only)
export const acceptOffer = async (
  offerPublicId: string,
  note?: string
): Promise<PriceOfferResponse> => {
  const res = await instance.post<{ success: boolean; data: BackendOfferResponse }>(
    `/offers/${offerPublicId}/accept`,
    { status: "ACCEPTED", note }
  );
  return transformOffer(res.data.data);
};

// Reject an offer (seller only)
export const rejectOffer = async (
  offerPublicId: string,
  note?: string
): Promise<PriceOfferResponse> => {
  const res = await instance.post<{ success: boolean; data: BackendOfferResponse }>(
    `/offers/${offerPublicId}/reject`,
    { status: "REJECTED", note }
  );
  return transformOffer(res.data.data);
};

// Cancel an offer (buyer only)
export const cancelOffer = async (offerPublicId: string): Promise<void> => {
  await instance.post(`/offers/${offerPublicId}/cancel`);
};

// Check if user has a pending offer for a listing
export const hasPendingOffer = async (listingPublicId: string): Promise<boolean> => {
  const res = await instance.get<{ success: boolean; data: boolean }>(
    `/listings/${listingPublicId}/offers/pending`
  );
  return res.data.data;
};
