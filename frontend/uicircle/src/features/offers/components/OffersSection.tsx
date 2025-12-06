// src/features/offers/components/OffersSection.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import type { PriceOfferResponse } from "../types";
import { OfferList } from "./OfferList";

type OffersTab = "sent" | "received";
type ReceivedFilter = "pending" | "accepted" | "rejected" | "cancelled";
type SentFilter = "all" | "pending" | "accepted" | "rejected" | "cancelled";

interface OffersSectionProps {
  activeTab: OffersTab;
  onTabChange: (tab: OffersTab) => void;

  sentOffers: PriceOfferResponse[];
  receivedOffers: PriceOfferResponse[];

  loadingSent: boolean;
  loadingReceived: boolean;

  errorSent: string | null;
  errorReceived: string | null;

  onRefresh?: () => void;
}

// Sub tabs for the "received" tab (filter by offer status)
const RECEIVED_TABS: { key: ReceivedFilter; label: string; statuses: string[] }[] = [
  { key: "pending", label: "Pending", statuses: ["PENDING"] },
  { key: "accepted", label: "Accepted", statuses: ["ACCEPTED"] },
  { key: "rejected", label: "Rejected", statuses: ["REJECTED"] },
  { key: "cancelled", label: "Cancelled", statuses: ["EXPIRED"] }, // add "CANCELLED" if backend has it
];

// Sub tabs for the "sent" tab
const SENT_TABS: { key: SentFilter; label: string; statuses: string[] | "ALL" }[] = [
  { key: "all", label: "All", statuses: "ALL" },
  { key: "pending", label: "Pending", statuses: ["PENDING"] },
  { key: "accepted", label: "Accepted", statuses: ["ACCEPTED"] },
  { key: "rejected", label: "Rejected", statuses: ["REJECTED"] },
  { key: "cancelled", label: "Cancelled", statuses: ["EXPIRED"] }, // add "CANCELLED" if needed
];

export const OffersSection: React.FC<OffersSectionProps> = ({
  activeTab,
  onTabChange,
  sentOffers,
  receivedOffers,
  loadingSent,
  loadingReceived,
  errorSent,
  errorReceived,
  onRefresh,              
}) => {
  const navigate = useNavigate();

  const [receivedFilter, setReceivedFilter] = useState<ReceivedFilter>("pending");
  const [sentFilter, setSentFilter] = useState<SentFilter>("all");

  const currentLoading = activeTab === "sent" ? loadingSent : loadingReceived;
  const currentError = activeTab === "sent" ? errorSent : errorReceived;

  const filteredReceived =
    activeTab === "received"
      ? receivedOffers.filter((offer) =>
          RECEIVED_TABS.find((t) => t.key === receivedFilter)?.statuses.includes(
            offer.status
          )
        )
      : receivedOffers;

  const filteredSent =
    activeTab === "sent"
      ? sentOffers.filter((offer) => {
          const tab = SENT_TABS.find((t) => t.key === sentFilter);
          if (!tab) return true;
          if (tab.statuses === "ALL") return true;
          return tab.statuses.includes(offer.status);
        })
      : sentOffers;

  const handleOpenChat = (offer: PriceOfferResponse) => {
    navigate(`/chat?listing=${offer.listing.publicId}`);
  };

  return (
    <div className="flex w-full max-w-full flex-col md:flex-row items-start gap-7">
      {/* Left side: main tabs (Sent / Received) */}
      <aside className="w-full md:w-64 shrink-0 mt-6">
        <nav className="space-y-1">
          {[
            { key: "sent", label: "Sent offers" },
            { key: "received", label: "Received offers" },
          ].map((tab) => {
            const isActive = tab.key === activeTab;
            return (
              <button
                key={tab.key}
                type="button"
                onClick={() => onTabChange(tab.key as OffersTab)}
                className={`block w-full text-left text-[15px] tracking-tight px-5 py-4 rounded-none ${
                  isActive
                    ? "bg-zinc-100 font-semibold text-zinc-900"
                    : "text-zinc-500 hover:bg-zinc-100 hover:text-zinc-900"
                }`}
              >
                {tab.label}
              </button>
            );
          })}
        </nav>
      </aside>

      {/* Right side: list + filters */}
      <main className="flex-1 w-full min-w-0 pt-6">
        {/* Sent subtabs */}
        {activeTab === "sent" && (
          <div className="mb-4 flex flex-wrap gap-3 border-b pb-2 text-sm">
            {SENT_TABS.map((filter) => (
              <button
                key={filter.key}
                type="button"
                onClick={() => setSentFilter(filter.key)}
                className={`px-2 py-1 ${
                  sentFilter === filter.key
                    ? "font-semibold text-primary"
                    : "text-gray-500 hover:text-gray-700"
                }`}
              >
                {filter.label}
              </button>
            ))}
          </div>
        )}

        {/* Received subtabs */}
        {activeTab === "received" && (
          <div className="mb-4 flex flex-wrap gap-3 border-b pb-2 text-sm">
            {RECEIVED_TABS.map((filter) => (
              <button
                key={filter.key}
                type="button"
                onClick={() => setReceivedFilter(filter.key)}
                className={`px-2 py-1 ${
                  receivedFilter === filter.key
                    ? "font-semibold text-primary"
                    : "text-gray-500 hover:text-gray-700"
                }`}
              >
                {filter.label}
              </button>
            ))}
          </div>
        )}

        {/* Loading / error states */}
        {currentLoading && (
          <p className="mb-3 text-sm text-zinc-500">Loading…</p>
        )}
        {currentError && (
          <p className="mb-3 text-sm text-red-600">{currentError}</p>
        )}

        {/* Main list */}
        {!currentLoading && !currentError && (
          <div className="w-full max-w-full">
            <OfferList
              offers={activeTab === "sent" ? filteredSent : filteredReceived}
              type={activeTab}
              loading={false}
              error={null}
              onActionCompleted={onRefresh}
              onOpenChat={handleOpenChat}
            />
          </div>
        )}
      </main>
    </div>
  );
};
