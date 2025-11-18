import { useEffect, useState } from "react";
import Navbar from "@/components/Navbar";
import CategoryMenu from "@/components/CategoryMenu";
import ListingCard from "@/components/ListingCard";
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
    return (
      <div className="min-h-screen bg-surface-light">
        <Navbar />
        <div className="flex justify-center items-center h-96">
          <div className="text-center">
            <p className="text-gray-600">Loading...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-surface-light">
        <Navbar />
        <div className="flex justify-center items-center h-96">
          <div className="text-center">            
            <p className="text-red-600 font-medium">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-surface-light">
      <Navbar />
      <CategoryMenu categories={categories} />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">        

        {/* Listings Section */}
        <div className="mb-6 flex justify-between items-center">
          <h2 className="text-2xl font-bold text-gray-900">Recent Listings</h2>
          <div className="flex items-center gap-2 text-sm text-gray-600">
            <span>Updated just now</span>
          </div>
        </div>
        
        {listings.length === 0 ? (
          <div className="text-center py-20 bg-background-light rounded-2xl border-2 border-dashed border-border-light">
            <svg className="w-20 h-20 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
            <p className="text-gray-500 text-lg">No listings available yet.</p>
            <p className="text-gray-400 text-sm mt-2">Be the first to post something!</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {listings.map((listing) => (
              <ListingCard key={listing.publicId} listing={listing} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export { HomePage };
