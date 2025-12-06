// src/features/offers/components/OfferList.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import type { PriceOfferResponse } from "../types";
import { OfferStatusBadge } from "./OfferStatusBadge";
import {
  acceptOffer,
  rejectOffer,
  cancelOffer,
} from "@/features/offers/services/OfferService";

interface OfferListProps {
  offers: PriceOfferResponse[];
  type: "sent" | "received";
  loading: boolean;
  error: string | null;

  onActionCompleted?: () => void;
  onOpenChat?: (offer: PriceOfferResponse) => void;
}

const formatDate = (iso: string) =>
  new Date(iso).toLocaleString(undefined, {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

export const OfferList: React.FC<OfferListProps> = ({
  offers,
  type,
  loading,
  error,
  onActionCompleted,
  onOpenChat,
}) => {
  const navigate = useNavigate();
  const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);

  if (loading) {
    return (
      <div className="space-y-3">
        {Array.from({ length: 3 }).map((_, idx) => (
          <div
            key={idx}
            className="animate-pulse rounded-xl border bg-white p-4 shadow-sm"
          >
            <div className="flex gap-3">
              <div className="h-16 w-16 rounded-lg bg-gray-200" />
              <div className="flex-1 space-y-2">
                <div className="h-4 w-3/5 rounded bg-gray-200" />
                <div className="h-3 w-2/5 rounded bg-gray-200" />
                <div className="h-3 w-1/3 rounded bg-gray-200" />
              </div>
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (error) {
    return <div className="text-sm text-red-600">{error}</div>;
  }

  if (offers.length === 0) {
    return (
      <div className="rounded-xl border bg-white p-6 text-center text-sm text-gray-500">
        {type === "sent"
          ? "You haven't sent any offers yet."
          : "You haven't received any offers yet."}
      </div>
    );
  }

  const handleAccept = async (
    e: React.MouseEvent,
    offer: PriceOfferResponse
  ) => {
    e.stopPropagation();
    if (offer.status !== "PENDING") return;

    try {
      setActionLoadingId(offer.publicId);
      await acceptOffer(offer.publicId);
      onActionCompleted?.();
    } catch (err) {
      console.error("Failed to accept offer", err);
      alert("Failed to accept offer.");
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleReject = async (
    e: React.MouseEvent,
    offer: PriceOfferResponse
  ) => {
    e.stopPropagation();
    if (offer.status !== "PENDING") return;

    try {
      setActionLoadingId(offer.publicId);
      await rejectOffer(offer.publicId);
      onActionCompleted?.();
    } catch (err) {
      console.error("Failed to reject offer", err);
      alert("Failed to reject offer.");
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleCancel = async (
    e: React.MouseEvent,
    offer: PriceOfferResponse
  ) => {
    e.stopPropagation();
    if (offer.status !== "PENDING") return;

    try {
      setActionLoadingId(offer.publicId);
      await cancelOffer(offer.publicId);
      onActionCompleted?.();
    } catch (err) {
      console.error("Failed to cancel offer", err);
      alert("Failed to cancel offer.");
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleOpenChat = (
    e: React.MouseEvent,
    offer: PriceOfferResponse
  ) => {
    e.stopPropagation();
    if (!onOpenChat) return;
    onOpenChat(offer);
  };

  return (
    <div className="space-y-3">
      {offers.map((offer) => {
        const isActionLoading = actionLoadingId === offer.publicId;

        return (
          <button
            key={offer.publicId}
            type="button"
            onClick={() => navigate(`/listings/${offer.listing.publicId}`)}
            className="flex w-full max-w-full overflow-hidden gap-3 rounded-xl border bg-white p-4 text-left shadow-sm transition hover:border-primary/40 hover:shadow-md"
          >
            {/* Thumbnail */}
            <div className="h-32 w-32 flex-shrink-0 overflow-hidden rounded-lg bg-gray-100">
              {offer.listing.thumbnailUrl ? (
                <img
                  src={offer.listing.thumbnailUrl}
                  alt={offer.listing.title}
                  className="h-full w-full object-cover"
                />
              ) : (
                <div className="flex h-full w-full items-center justify-center text-xs text-gray-400">
                  No Image
                </div>
              )}
            </div>

            {/* Right side content */}
            <div className="flex flex-1 min-w-0 flex-col gap-1">
              {/* Title + status */}
              <div className="flex items-start justify-between gap-2">
                <div className="flex-1 min-w-0">
                  <div className="text-sm font-medium text-gray-900 truncate">
                    {offer.listing.title}
                  </div>
                  <div className="mt-0.5 text-xs text-gray-500">
                    Listed price: $
                    {offer.listing.price.toLocaleString()}
                  </div>
                </div>
                <div className="flex-shrink-0">
                  <OfferStatusBadge status={offer.status} />
                </div>
              </div>

              {/* Offer + message (message = 1 line max) */}
              <div className="mt-1 flex items-center gap-2 text-xs">
                <span className="shrink-0 font-semibold text-gray-900">
                  Offer: ${offer.amount.toLocaleString()}
                </span>
                {offer.message && (
                  <span className="flex-1 min-w-0 text-gray-500 truncate">
                    “{offer.message}”
                  </span>
                )}
              </div>

              {/* Meta */}
              <div className="mt-1 flex items-center justify-between text-xs text-gray-500">
                <span className="flex-1 min-w-0 truncate">
                  {type === "sent"
                    ? "To seller"
                    : `From ${offer.buyer.username}`}
                </span>
                <span className="ml-2 shrink-0">
                  {formatDate(offer.createdAt)}
                </span>
              </div>

              {/* Actions */}
              <div className="mt-2 flex flex-wrap justify-end gap-2">
                {offer.status === "PENDING" && (
                  <>
                    {type === "received" && (
                      <>
                        <button
                          type="button"
                          onClick={(e) => handleAccept(e, offer)}
                          disabled={isActionLoading}
                          className="rounded-md bg-emerald-500 px-3 py-1 text-xs font-medium text-white hover:bg-emerald-600 disabled:opacity-50"
                        >
                          {isActionLoading ? "Processing..." : "Accept"}
                        </button>
                        <button
                          type="button"
                          onClick={(e) => handleReject(e, offer)}
                          disabled={isActionLoading}
                          className="rounded-md bg-red-500 px-3 py-1 text-xs font-medium text-white hover:bg-red-600 disabled:opacity-50"
                        >
                          {isActionLoading ? "Processing..." : "Reject"}
                        </button>
                      </>
                    )}

                    {type === "sent" && (
                      <button
                        type="button"
                        onClick={(e) => handleCancel(e, offer)}
                        disabled={isActionLoading}
                        className="rounded-md border border-gray-300 px-3 py-1 text-xs font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50"
                      >
                        {isActionLoading ? "Processing..." : "Cancel"}
                      </button>
                    )}
                  </>
                )}

                {offer.status === "ACCEPTED" && onOpenChat && (
                  <button
                    type="button"
                    onClick={(e) => handleOpenChat(e, offer)}
                    className="rounded-md border px-3 py-2 text-sm font-medium hover:bg-gray-100"
                  >
                    {type === "sent" ? "Message seller" : "Message buyer"}
                  </button>
                )}
              </div>
            </div>
          </button>
        );
      })}
    </div>
  );
};
