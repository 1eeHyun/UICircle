import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import Navbar from "../../../components/Navbar";
import CategoryMenu from "../../../components/CategoryMenu";
import ListingCard from "../../../components/ListingCard";
import {
  getTopLevelCategories,
  getListingsByCategory,
  CategoryResponse,
  ListingSummaryResponse,
} from "../services/ListingService";

const CategoryPage = () => {
  const { categorySlug } = useParams<{ categorySlug: string }>();
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [listings, setListings] = useState<ListingSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      if (!categorySlug) return;
      
      try {
        setLoading(true);
        const [categoriesData, listingsData] = await Promise.all([
          getTopLevelCategories(),
          getListingsByCategory(categorySlug, 0, 20, "createdAt", "DESC"),
        ]);
        
        setCategories(categoriesData);
        setListings(listingsData.content);
      } catch (err: any) {
        setError(err?.response?.data?.message || "Failed to load category");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [categorySlug]);

  if (loading) {
    return (
      <div className="min-h-screen bg-surface-light">
        <Navbar />
        <div className="flex justify-center items-center h-96">
          <div className="text-center">
            <svg className="animate-spin h-12 w-12 text-primary mx-auto mb-4" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
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
            <svg className="w-16 h-16 text-red-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
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
        {/* Category Header */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center">
              <svg className="w-6 h-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
              </svg>
            </div>
            <div>
              <h2 className="text-3xl font-bold text-gray-900 capitalize">
                {categorySlug?.replace(/-/g, ' ')}
              </h2>
              <p className="text-gray-600">{listings.length} items available</p>
            </div>
          </div>
        </div>
        
        {listings.length === 0 ? (
          <div className="text-center py-20 bg-background-light rounded-2xl border-2 border-dashed border-border-light">
            <svg className="w-20 h-20 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
            <p className="text-gray-500 text-lg">No listings in this category yet.</p>
            <p className="text-gray-400 text-sm mt-2">Check back later for new items!</p>
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

export { CategoryPage };
