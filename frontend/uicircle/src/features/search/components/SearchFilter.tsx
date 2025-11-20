import { useState, useEffect } from "react";
import { SearchFilters } from "../services/SearchService";
import { getAllCategories, CategoryResponse } from "@/features/listings/services/ListingService";

interface SearchFilterProps {
  filters: SearchFilters;
  onFiltersChange: (filters: SearchFilters) => void;
  onSearch: () => void;
}

const ITEM_CONDITIONS = [
  { value: "NEW", label: "Brand New" },
  { value: "LIKE_NEW", label: "Like New" },
  { value: "GOOD", label: "Good" },
  { value: "FAIR", label: "Fair" },
  { value: "POOR", label: "Poor" },
] as const;

const SORT_OPTIONS = [
  { value: "createdAt", label: "Newest First", sortOrder: "desc" as const },
  { value: "price", label: "Price: Low to High", sortOrder: "asc" as const },
  { value: "price", label: "Price: High to Low", sortOrder: "desc" as const },
] as const;

const SearchFilter = ({ filters, onFiltersChange, onSearch }: SearchFilterProps) => {
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [isExpanded, setIsExpanded] = useState(false);
  const [allCategories, setAllCategories] = useState<CategoryResponse[]>([]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const cats = await getAllCategories();
        setAllCategories(cats);
        // Flatten categories and subcategories for dropdown
        const flattened: CategoryResponse[] = [];
        const flatten = (cats: CategoryResponse[]) => {
          cats.forEach((cat) => {
            flattened.push(cat);
            if (cat.children && cat.children.length > 0) {
              flatten(cat.children);
            }
          });
        };
        flatten(cats);
        setCategories(flattened);
      } catch (error) {
        console.error("Failed to load categories:", error);
      }
    };
    fetchCategories();
  }, []);

  const updateFilter = (key: keyof SearchFilters, value: any) => {
    onFiltersChange({ ...filters, [key]: value || undefined });
  };

  const handleClearFilters = () => {
    onFiltersChange({
      keyword: undefined,
      categorySlug: undefined,
      minPrice: undefined,
      maxPrice: undefined,
      condition: undefined,
      sortBy: "createdAt",
      sortOrder: "desc",
      page: 1,
      size: 20,
    });
  };

  const hasActiveFilters =
    filters.keyword ||
    filters.categorySlug ||
    filters.minPrice ||
    filters.maxPrice ||
    filters.condition;

  return (
    <div className="bg-background-light border-b border-border-light">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        {/* Main Search Bar */}
        <div className="flex gap-3 mb-4">
          <div className="flex-1 relative">
            <input
              type="text"
              placeholder="Search for items..."
              value={filters.keyword || ""}
              onChange={(e) => updateFilter("keyword", e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  onSearch();
                }
              }}
              className="w-full px-4 py-3 pl-10 border border-border-light rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
            />
            <svg
              className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
          </div>
          <button
            onClick={onSearch}
            className="px-6 py-3 bg-primary text-white rounded-lg hover:bg-primary/90 transition font-medium"
          >
            Search
          </button>
        </div>

        {/* Quick Filters Row */}
        <div className="flex items-center gap-3 flex-wrap">
          <select
            value={filters.categorySlug || ""}
            onChange={(e) => updateFilter("categorySlug", e.target.value)}
            className="px-4 py-2 border border-border-light rounded-lg focus:outline-none focus:ring-2 focus:ring-primary text-sm"
          >
            <option value="">All Categories</option>
            {categories.map((cat) => (
              <option key={cat.categorySlug} value={cat.categorySlug}>
                {cat.name}
              </option>
            ))}
          </select>

          <select
            value={filters.condition || ""}
            onChange={(e) => updateFilter("condition", e.target.value as any)}
            className="px-4 py-2 border border-border-light rounded-lg focus:outline-none focus:ring-2 focus:ring-primary text-sm"
          >
            <option value="">Any Condition</option>
            {ITEM_CONDITIONS.map((cond) => (
              <option key={cond.value} value={cond.value}>
                {cond.label}
              </option>
            ))}
          </select>

          <select
            value={`${filters.sortBy || "createdAt"}_${filters.sortOrder || "desc"}`}
            onChange={(e) => {
              const [sortBy, sortOrder] = e.target.value.split("_");
              updateFilter("sortBy", sortBy);
              updateFilter("sortOrder", sortOrder as "asc" | "desc");
            }}
            className="px-4 py-2 border border-border-light rounded-lg focus:outline-none focus:ring-2 focus:ring-primary text-sm"
          >
            {SORT_OPTIONS.map((opt, idx) => (
              <option key={`${opt.value}_${opt.sortOrder}_${idx}`} value={`${opt.value}_${opt.sortOrder}`}>
                {opt.label}
              </option>
            ))}
          </select>

          <button
            onClick={() => setIsExpanded(!isExpanded)}
            className="px-4 py-2 text-sm text-gray-700 hover:text-primary transition flex items-center gap-1"
          >
            <span>More Filters</span>
            <svg
              className={`w-4 h-4 transition-transform ${isExpanded ? "rotate-180" : ""}`}
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
            </svg>
          </button>

          {hasActiveFilters && (
            <button
              onClick={handleClearFilters}
              className="px-4 py-2 text-sm text-red-600 hover:text-red-700 transition"
            >
              Clear All
            </button>
          )}
        </div>

        {/* Expanded Filters */}
        {isExpanded && (
          <div className="mt-4 pt-4 border-t border-border-light grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Min Price</label>
              <input
                type="number"
                min="0"
                step="0.01"
                value={filters.minPrice || ""}
                onChange={(e) => updateFilter("minPrice", e.target.value ? parseFloat(e.target.value) : undefined)}
                placeholder="0.00"
                className="w-full px-4 py-2 border border-border-light rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Max Price</label>
              <input
                type="number"
                min="0"
                step="0.01"
                value={filters.maxPrice || ""}
                onChange={(e) => updateFilter("maxPrice", e.target.value ? parseFloat(e.target.value) : undefined)}
                placeholder="No limit"
                className="w-full px-4 py-2 border border-border-light rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchFilter;

