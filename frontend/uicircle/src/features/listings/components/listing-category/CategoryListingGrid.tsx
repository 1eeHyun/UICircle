// src/features/listings/components/listing-category/CategoryListingGrid.tsx
import React from "react";
import FavoriteGridCard from "../listing-favorite/FavoriteGridCard";
import type { ListingSummaryResponse } from "@/features/listings/services/ListingService";

interface CategoryListingGridProps {
  listings: ListingSummaryResponse[];
}

export const CategoryListingGrid: React.FC<CategoryListingGridProps> = ({
  listings,
}) => {
  return (
    <section className="flex-1">
      {/* mobile filter button + result count */}
      <div className="mb-3 lg:hidden flex justify-between items-center">
        <button className="inline-flex items-center gap-2 rounded-full border border-gray-300 px-3 py-1.5 text-xs font-medium text-gray-700">
          Filters
        </button>
        <span className="text-xs text-gray-500">
          {listings.length} results
        </span>
      </div>

      {/* sort dropdown (top-right of list area) */}
      <div className="mb-3 flex justify-end">
        <select className="rounded-md border border-gray-300 bg-white px-3 py-2 text-xs font-medium text-gray-700 focus:outline-none focus:ring-2 focus:ring-primary/40">
          <option value="best">Sort by best match</option>
          <option value="newest">Newest first</option>
          <option value="price-low">Price: low to high</option>
          <option value="price-high">Price: high to low</option>
        </select>
      </div>

      {listings.length === 0 ? (
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
              d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"
            />
          </svg>
          <p className="text-gray-500 text-lg">
            No listings in this category yet.
          </p>
          <p className="text-gray-400 text-sm mt-2">
            Check back later for new items!
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 md:gap-4">
          {listings.map((listing) => (
            <FavoriteGridCard key={listing.publicId} item={listing} />
          ))}
        </div>
      )}
    </section>
  );
};
