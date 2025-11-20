import { useState, useEffect } from "react";
import Navbar from "@/components/Navbar";
import CategoryMenu from "@/components/CategoryMenu";
import ListingCard from "@/components/ListingCard";
import SearchFilter from "../components/SearchFilter";
import SavedSearches from "../components/SavedSearches";
import {
  searchListings,
  saveSearch,
  SearchFilters,
  SavedSearchResponse,
} from "../services/SearchService";
import {
  ListingSummaryResponse,
  PageResponse,
  getTopLevelCategories,
  CategoryResponse,
} from "@/features/listings/services/ListingService";

const SearchPage = () => {
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [listings, setListings] = useState<ListingSummaryResponse[]>([]);
  const [pageInfo, setPageInfo] = useState<PageResponse<ListingSummaryResponse> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [filters, setFilters] = useState<SearchFilters>({
    sortBy: "createdAt",
    sortOrder: "desc",
    page: 1,
    size: 20,
  });
  const [showSaveDialog, setShowSaveDialog] = useState(false);
  const [saveSearchName, setSaveSearchName] = useState("");

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const cats = await getTopLevelCategories();
        setCategories(cats);
      } catch (err) {
        console.error("Failed to load categories:", err);
      }
    };
    fetchCategories();
  }, []);

  const performSearch = async (searchFilters: SearchFilters = filters) => {
    try {
      setLoading(true);
      setError("");
      const result = await searchListings({
        ...searchFilters,
        status: "ACTIVE", // Always search for active listings
      });
      setListings(result.content);
      setPageInfo(result);
    } catch (err: any) {
      setError(err?.response?.data?.message || "Failed to search listings");
      setListings([]);
      setPageInfo(null);
    } finally {
      setLoading(false);
    }
  };

  const handleFiltersChange = (newFilters: SearchFilters) => {
    setFilters(newFilters);
  };

  const handleSearch = () => {
    performSearch();
  };

  const handleLoadSavedSearch = async (savedSearch: SavedSearchResponse) => {
    try {
      // Parse the filters JSON from saved search
      const parsedFilters = savedSearch.filters ? JSON.parse(savedSearch.filters) : {};
      const newFilters: SearchFilters = {
        keyword: savedSearch.query || parsedFilters.keyword,
        categorySlug: parsedFilters.categorySlug,
        minPrice: parsedFilters.minPrice,
        maxPrice: parsedFilters.maxPrice,
        condition: parsedFilters.condition,
        sortBy: parsedFilters.sortBy || "createdAt",
        sortOrder: parsedFilters.sortOrder || "desc",
        page: 1,
        size: 20,
      };
      setFilters(newFilters);
      await performSearch(newFilters);
    } catch (err) {
      console.error("Failed to load saved search:", err);
      setError("Failed to load saved search");
    }
  };

  const handleSaveSearch = async () => {
    if (!saveSearchName.trim()) {
      alert("Please enter a name for your saved search");
      return;
    }

    try {
      // Create filters JSON (exclude pagination and status)
      const filtersToSave = {
        keyword: filters.keyword,
        categorySlug: filters.categorySlug,
        minPrice: filters.minPrice,
        maxPrice: filters.maxPrice,
        condition: filters.condition,
        sortBy: filters.sortBy,
        sortOrder: filters.sortOrder,
      };

      await saveSearch({
        name: saveSearchName.trim(),
        query: filters.keyword,
        filters: JSON.stringify(filtersToSave),
      });

      setShowSaveDialog(false);
      setSaveSearchName("");
      alert("Search saved successfully!");
    } catch (err: any) {
      console.error("Failed to save search:", err);
      alert(err?.response?.data?.message || "Failed to save search");
    }
  };

  const handlePageChange = (newPage: number) => {
    // Convert from 0-indexed (from pageInfo) to 1-indexed (for API)
    const newFilters = { ...filters, page: newPage };
    setFilters(newFilters);
    performSearch(newFilters);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  return (
    <div className="min-h-screen bg-surface-light">
      <Navbar />
      <CategoryMenu categories={categories} />
      <SavedSearches onLoadSearch={handleLoadSavedSearch} />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <SearchFilter
          filters={filters}
          onFiltersChange={handleFiltersChange}
          onSearch={handleSearch}
        />

        {/* Save Search Button */}
        <div className="mt-4 flex justify-end">
          <button
            onClick={() => setShowSaveDialog(true)}
            className="px-4 py-2 text-sm text-primary hover:text-primary/80 transition flex items-center gap-2"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"
              />
            </svg>
            Save Search
          </button>
        </div>

        {/* Save Search Dialog */}
        {showSaveDialog && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
              <h3 className="text-lg font-semibold mb-4">Save Search</h3>
              <input
                type="text"
                placeholder="Enter a name for this search..."
                value={saveSearchName}
                onChange={(e) => setSaveSearchName(e.target.value)}
                className="w-full px-4 py-2 border border-border-light rounded-lg focus:outline-none focus:ring-2 focus:ring-primary mb-4"
                autoFocus
              />
              <div className="flex gap-3 justify-end">
                <button
                  onClick={() => {
                    setShowSaveDialog(false);
                    setSaveSearchName("");
                  }}
                  className="px-4 py-2 text-gray-700 hover:text-gray-900 transition"
                >
                  Cancel
                </button>
                <button
                  onClick={handleSaveSearch}
                  className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition"
                >
                  Save
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Results Section */}
        {loading && (
          <div className="flex justify-center items-center py-20">
            <div className="text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
              <p className="text-gray-600">Searching...</p>
            </div>
          </div>
        )}

        {error && (
          <div className="text-center py-20">
            <p className="text-red-600 font-medium">{error}</p>
          </div>
        )}

        {!loading && !error && (
          <>
            {pageInfo && (
              <div className="mb-6 text-sm text-gray-600">
                Found {pageInfo.totalElements} result{pageInfo.totalElements !== 1 ? "s" : ""}
              </div>
            )}

            {listings.length === 0 && !loading ? (
              <div className="text-center py-20 bg-background-light rounded-2xl border-2 border-dashed border-border-light">
                <svg
                  className="w-20 h-20 text-gray-300 mx-auto mb-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={1.5}
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                  />
                </svg>
                <p className="text-gray-500 text-lg">No listings found.</p>
                <p className="text-gray-400 text-sm mt-2">Try adjusting your search filters.</p>
              </div>
            ) : (
              <>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                  {listings.map((listing) => (
                    <ListingCard key={listing.publicId} listing={listing} />
                  ))}
                </div>

                {/* Pagination */}
                {pageInfo && pageInfo.totalPages > 1 && (
                  <div className="mt-8 flex justify-center items-center gap-2">
                    <button
                      onClick={() => handlePageChange(pageInfo.number)}
                      disabled={pageInfo.number === 0}
                      className="px-4 py-2 border border-border-light rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-background-light transition"
                    >
                      Previous
                    </button>
                    <span className="text-sm text-gray-600">
                      Page {pageInfo.number + 1} of {pageInfo.totalPages}
                    </span>
                    <button
                      onClick={() => handlePageChange(pageInfo.number + 2)}
                      disabled={pageInfo.number + 1 >= pageInfo.totalPages}
                      className="px-4 py-2 border border-border-light rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-background-light transition"
                    >
                      Next
                    </button>
                  </div>
                )}
              </>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export { SearchPage };

