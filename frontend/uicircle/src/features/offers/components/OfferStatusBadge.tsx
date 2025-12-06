// src/features/offers/components/OfferStatusBadge.tsx
import React from "react";
import type { OfferStatus } from "../types";

interface OfferStatusBadgeProps {
  status: OfferStatus;
}

const STATUS_LABEL: Record<OfferStatus, string> = {
  PENDING: "Pending",
  ACCEPTED: "Accepted",
  REJECTED: "Rejected",
  EXPIRED: "Expired",
};

const STATUS_CLASS: Record<OfferStatus, string> = {
  PENDING: "bg-yellow-100 text-yellow-800 border-yellow-200",
  ACCEPTED: "bg-green-100 text-green-800 border-green-200",
  REJECTED: "bg-red-100 text-red-800 border-red-200",
  EXPIRED: "bg-gray-100 text-gray-700 border-gray-200",
};

export const OfferStatusBadge: React.FC<OfferStatusBadgeProps> = ({
  status,
}) => {
  return (
    <span
      className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-medium ${STATUS_CLASS[status]}`}
    >
      {STATUS_LABEL[status]}
    </span>
  );
};
