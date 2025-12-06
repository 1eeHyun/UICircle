// src/features/offers/services/OfferService.ts
import instance from "@/api/axios";
import type { PriceOfferResponse } from "../types";

export interface UpdateOfferStatusRequest {
  status: "ACCEPTED" | "REJECTED";
  note?: string;
}

export const getSentOffers = async (): Promise<PriceOfferResponse[]> => {
  const res = await instance.get<{
    success: boolean;
    data: PriceOfferResponse[];
  }>("/offers/sent");
  return res.data.data;
};

export const getReceivedOffers = async (): Promise<PriceOfferResponse[]> => {
  const res = await instance.get<{
    success: boolean;
    data: PriceOfferResponse[];
  }>("/offers/received");
  return res.data.data;
};

export const acceptOffer = async (
  offerPublicId: string,
  note?: string
): Promise<PriceOfferResponse> => {
  const payload: UpdateOfferStatusRequest = {
    status: "ACCEPTED",
    note,
  };

  const res = await instance.post<{
    success: boolean;
    data: PriceOfferResponse;
  }>(`/offers/${offerPublicId}/accept`, payload);

  return res.data.data;
};

export const rejectOffer = async (
  offerPublicId: string,
  note?: string
): Promise<PriceOfferResponse> => {
  const payload: UpdateOfferStatusRequest = {
    status: "REJECTED",
    note,
  };

  const res = await instance.post<{
    success: boolean;
    data: PriceOfferResponse;
  }>(`/offers/${offerPublicId}/reject`, payload);

  return res.data.data;
};

export const cancelOffer = async (
  offerPublicId: string
): Promise<void> => {
  await instance.post<{ success: boolean }>(
    `/offers/${offerPublicId}/cancel`
  );
};
