// src/pages/HomePage.tsx
import { useEffect, useState, useCallback } from "react";
import Navbar from "@/components/Navbar";
import CategoryMenu from "@/components/CategoryMenu";
import ListingCard from "@/components/ListingCard";
import HomePageTabs from "@/features/listings/components/home/HomePageTabs";
import MyFavoritesSection from "@/features/listings/components/listing-favorite/MyFavoritesSection";

import {
  getAllActiveListings,
  type ListingSummaryResponse,
} from "@/features/listings/services/ListingService";
import { useCategories } from "@/features/listings/context/CategoryContext";

import type { PriceOfferResponse } from "@/features/offers/types";
import {
  getSentOffers,
  getReceivedOffers,
} from "@/features/offers/services/OfferService";
import { OffersSection } from "@/features/offers/components/OffersSection";

type HomeTab = "RECENT" | "LIKES" | "OFFERS";
type OffersTab = "sent" | "received";

const HomePage = () => {
  // Category data from context
  const {
    categories,
    loading: categoriesLoading,
    error: categoriesError,
  } = useCategories();

  // Listings for "RECENT" tab
  const [listings, setListings] = useState<ListingSummaryResponse[]>([]);
  const [listingsLoading, setListingsLoading] = useState(true);
  const [listingsError, setListingsError] = useState("");

  // Home top-level tab (Recent / Likes / Offers)
  const [activeTab, setActiveTab] = useState<HomeTab>("RECENT");

  // Sub tab for Offers section (sent / received)
  const [offersTab, setOffersTab] = useState<OffersTab>("sent");

  // Offers data
  const [sentOffers, setSentOffers] = useState<PriceOfferResponse[]>([]);
  const [receivedOffers, setReceivedOffers] = useState<PriceOfferResponse[]>([]);

  // Offers loading / error state
  const [loadingSent, setLoadingSent] = useState(false);
  const [loadingReceived, setLoadingReceived] = useState(false);
  const [errorSent, setErrorSent] = useState<string | null>(null);
  const [errorReceived, setErrorReceived] = useState<string | null>(null);
  const [offersLoaded, setOffersLoaded] = useState(false);

  // Fetch recent listings for RECENT tab
  useEffect(() => {
    const fetchListings = async () => {
      try {
        setListingsLoading(true);
        const listingsData = await getAllActiveListings(
          0,
          20,
          "createdAt",
          "DESC"
        );
        setListings(listingsData.content);
      } catch (err: any) {
        setListingsError(
          err?.response?.data?.message || "Failed to load listings"
        );
      } finally {
        setListingsLoading(false);
      }
    };

    fetchListings();
  }, []);

  // Reusable fetch function for offers (sent + received)
  const fetchOffers = useCallback(async () => {
    try {
      setLoadingSent(true);
      setLoadingReceived(true);
      setErrorSent(null);
      setErrorReceived(null);

      const [sent, received] = await Promise.all([
        getSentOffers(),
        getReceivedOffers(),
      ]);

      setSentOffers(sent);
      setReceivedOffers(received);
      setOffersLoaded(true);
    } catch (e) {
      console.error(e);
      setErrorSent("Failed to load sent offers.");
      setErrorReceived("Failed to load received offers.");
    } finally {
      setLoadingSent(false);
      setLoadingReceived(false);
    }
  }, []);

  // Only load offers when OFFERS tab is opened for the first time
  useEffect(() => {
    if (activeTab === "OFFERS" && !offersLoaded) {
      fetchOffers();
    }
  }, [activeTab, offersLoaded, fetchOffers]);

  // Callback passed down to OffersSection -> OfferList
  // This will be called after accept / reject / cancel
  const handleOfferActionCompleted = () => {
    // Refresh offers data
    fetchOffers();
  };

  // Global loading / error combined from categories + listings
  const isLoading = categoriesLoading || listingsLoading;
  const errorMessage = categoriesError || listingsError;

  if (isLoading) {
    return (
      <div className="min-h-screen">
        <Navbar />
        <CategoryMenu categories={categories} />
        <div className="flex h-96 items-center justify-center">
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  if (errorMessage) {
    return (
      <div className="min-h-screen">
        <Navbar />
        <CategoryMenu categories={categories} />
        <div className="flex h-96 items-center justify-center">
          <p className="text-red-600">{errorMessage}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen">
      <Navbar />
      <CategoryMenu categories={categories} />

      <div className="mx-auto max-w-7xl px-4 pt-6 pb-8 sm:px-6 lg:px-8">
        {/* Top-level home tabs */}
        <HomePageTabs activeTab={activeTab} onChange={setActiveTab} />

        {/* RECENT tab */}
        {activeTab === "RECENT" && (
          <div>
            <div
              className="
                grid 
                grid-cols-2 
                gap-4
                sm:grid-cols-3 
                md:grid-cols-4 
                lg:grid-cols-5 
                xl:grid-cols-6
              "
            >
              {listings.map((listing) => (
                <ListingCard key={listing.publicId} listing={listing} />
              ))}
            </div>
          </div>
        )}

        {/* LIKES tab */}
        {activeTab === "LIKES" && <MyFavoritesSection />}

        {/* OFFERS tab */}
        {activeTab === "OFFERS" && (
          <OffersSection
            activeTab={offersTab}
            onTabChange={setOffersTab}
            sentOffers={sentOffers}
            receivedOffers={receivedOffers}
            loadingSent={loadingSent}
            loadingReceived={loadingReceived}
            errorSent={errorSent}
            errorReceived={errorReceived}
            // Important: refresh callback after accept/reject/cancel
            onRefresh={handleOfferActionCompleted}
          />
        )}
      </div>
    </div>
  );
};

export { HomePage };
