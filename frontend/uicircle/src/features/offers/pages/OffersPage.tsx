// src/features/offers/pages/OffersPage.tsx
import React, { useEffect, useState } from "react";
import type { PriceOfferResponse } from "../types";
import {
  getSentOffers,
  getReceivedOffers,
} from "../services/OfferService";

import Navbar from "@/components/Navbar";
import CategoryMenu from "@/components/CategoryMenu";
import { useCategories } from "@/features/listings/context/CategoryContext";
import { OffersSection } from "../components/OffersSection";

type Tab = "sent" | "received";

const OffersPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<Tab>("sent");

  const [sentOffers, setSentOffers] = useState<PriceOfferResponse[]>([]);
  const [receivedOffers, setReceivedOffers] = useState<PriceOfferResponse[]>([]);

  const [loadingSent, setLoadingSent] = useState(false);
  const [loadingReceived, setLoadingReceived] = useState(false);

  const [errorSent, setErrorSent] = useState<string | null>(null);
  const [errorReceived, setErrorReceived] = useState<string | null>(null);

  const { categories } = useCategories();

  useEffect(() => {
    const fetchSent = async () => {
      try {
        setLoadingSent(true);
        setErrorSent(null);
        const data = await getSentOffers();
        setSentOffers(data);
      } catch (e) {
        setErrorSent("Failed to load sent offers.");
      } finally {
        setLoadingSent(false);
      }
    };

    const fetchReceived = async () => {
      try {
        setLoadingReceived(true);
        setErrorReceived(null);
        const data = await getReceivedOffers();
        setReceivedOffers(data);
      } catch (e) {
        setErrorReceived("Failed to load received offers.");
      } finally {
        setLoadingReceived(false);
      }
    };

    fetchSent();
    fetchReceived();
  }, []);

  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      <CategoryMenu categories={categories} />

      <OffersSection
        activeTab={activeTab}
        onTabChange={setActiveTab}
        sentOffers={sentOffers}
        receivedOffers={receivedOffers}
        loadingSent={loadingSent}
        loadingReceived={loadingReceived}
        errorSent={errorSent}
        errorReceived={errorReceived}
      />
    </div>
  );
};

export default OffersPage;
