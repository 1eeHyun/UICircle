import { useEffect, useState } from "react";
import Navbar from "@/components/Navbar";
import CategoryMenu from "@/components/CategoryMenu";
import ListingCard from "@/components/ListingCard";
import HomePageTabs from "@/features/listings/components/home/HomePageTabs";
import MyFavoritesSection from "@/features/listings/components/listing-favorite/MyFavoritesSection";

import {
  getTopLevelCategories,
  getAllActiveListings,
  CategoryResponse,
  ListingSummaryResponse,
} from "@/features/listings/services/ListingService";

const HomePage = () => {
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [listings, setListings] = useState<ListingSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [activeTab, setActiveTab] = useState<"RECENT" | "LIKES">("RECENT");

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [categoriesData, listingsData] = await Promise.all([
          getTopLevelCategories(),
          getAllActiveListings(0, 20, "createdAt", "DESC"),
        ]);

        setCategories(categoriesData);
        setListings(listingsData.content);
      } catch (err: any) {
        setError(err?.response?.data?.message || "Failed to load data");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }
  if (error) {
    return <div>{error}</div>;
  }

  return (
    <div className="min-h-screen bg-surface-light">
      <Navbar />
      <CategoryMenu categories={categories} />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        
        {/* Home tab */}
        <HomePageTabs activeTab={activeTab} onChange={setActiveTab} />

        {/* Section */}
        {activeTab === "RECENT" && (
          <div>
            <div className="
              grid 
              grid-cols-2 
              sm:grid-cols-3 
              md:grid-cols-4 
              lg:grid-cols-5 
              xl:grid-cols-6 
              gap-4
            ">
              {listings.map((listing) => (
                <ListingCard key={listing.publicId} listing={listing} />
              ))}
            </div>
          </div>
        )}

        {activeTab === "LIKES" && (
          <MyFavoritesSection />
        )}

      </div>
    </div>
  );
};

export { HomePage };
