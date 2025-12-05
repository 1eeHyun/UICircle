// src/features/listings/components/listing-category/CategoryFilterSidebar.tsx
import React, { useState } from "react";

interface CategoryFilterSidebarProps {
  categoryName: string;
  resultCount: number;
  priceFilter:
    | "ANY"
    | "UNDER_25"
    | "RANGE_25_50"
    | "RANGE_50_100"
    | "OVER_200"
    | "CUSTOM";
  customMin: string;
  customMax: string;
  onPriceFilterChange: (value: CategoryFilterSidebarProps["priceFilter"]) => void;
  onCustomMinChange: (value: string) => void;
  onCustomMaxChange: (value: string) => void;

  // location filter
  nearMe: boolean;
  radiusMiles: number;
  onNearMeChange: (checked: boolean) => void;
  onRadiusChange: (value: number) => void;
}

export const CategoryFilterSidebar: React.FC<CategoryFilterSidebarProps> = ({
  categoryName,
  resultCount,
  priceFilter,
  customMin,
  customMax,
  onPriceFilterChange,
  onCustomMinChange,
  onCustomMaxChange,
  nearMe,
  radiusMiles,
  onNearMeChange,
  onRadiusChange,
}) => {
  const [isPriceOpen, setIsPriceOpen] = useState(true);
  const [isLocationOpen, setIsLocationOpen] = useState(true);

  const radiusOptions = [1, 3, 5, 10];

  return (
    <aside className="hidden lg:block
        lg:basis-1/3 
        xl:basis-1/4 
        2xl:basis-1/5
        min-w-[260px] 
        shrink-0">

      <div className="p-4">
        {/* name + description */}
        <div className="mb-6">
          <h1 className="text-lg font-bold text-gray-900">{categoryName}</h1>
          <p className="text-xs text-gray-500">({resultCount} results)</p>
        </div>

        <h2 className="text-base font-semibold text-gray-900 mb-6">
          Filter by
        </h2>

        {/* === Price section === */}
        <div className="border-t border-gray-100 pt-3 mt-3">
          <button
            type="button"
            className="flex w-full items-center justify-between text-left"
            onClick={() => setIsPriceOpen((prev) => !prev)}
          >
            <p className="text-base font-medium text-gray-700">Price</p>
            <svg
              className={`w-4 h-4 text-gray-600 transition-transform ${
                isPriceOpen ? "rotate-180" : ""
              }`}
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M19 9l-7 7-7-7"
              />
            </svg>
          </button>

          {isPriceOpen && (
            <>
              <div className="mt-4 space-y-3 text-sm text-gray-700">
                <label className="flex items-center gap-2">
                  <input
                    type="radio"
                    name="price"
                    className="h-4 w-4"
                    checked={priceFilter === "ANY"}
                    onChange={() => onPriceFilterChange("ANY")}
                  />
                  <span>Any</span>
                </label>

                <label className="flex items-center gap-2">
                  <input
                    type="radio"
                    name="price"
                    className="h-4 w-4"
                    checked={priceFilter === "UNDER_25"}
                    onChange={() => onPriceFilterChange("UNDER_25")}
                  />
                  <span>Under $25</span>
                </label>

                <label className="flex items-center gap-2">
                  <input
                    type="radio"
                    name="price"
                    className="h-4 w-4"
                    checked={priceFilter === "RANGE_25_50"}
                    onChange={() => onPriceFilterChange("RANGE_25_50")}
                  />
                  <span>$25 to $50</span>
                </label>

                <label className="flex items-center gap-2">
                  <input
                    type="radio"
                    name="price"
                    className="h-4 w-4"
                    checked={priceFilter === "RANGE_50_100"}
                    onChange={() => onPriceFilterChange("RANGE_50_100")}
                  />
                  <span>$50 to $100</span>
                </label>

                <label className="flex items-center gap-2">
                  <input
                    type="radio"
                    name="price"
                    className="h-4 w-4"
                    checked={priceFilter === "OVER_200"}
                    onChange={() => onPriceFilterChange("OVER_200")}
                  />
                  <span>$200 and up</span>
                </label>

                <label className="flex items-center gap-2 mt-2">
                  <input
                    type="radio"
                    name="price"
                    className="h-4 w-4"
                    checked={priceFilter === "CUSTOM"}
                    onChange={() => onPriceFilterChange("CUSTOM")}
                  />
                  <span>Custom</span>
                </label>
              </div>

              {/* custom min/max */}
              <div className="mt-3 grid grid-cols-2 gap-2">
                <input
                  type="number"
                  placeholder="Min"
                  className="w-full rounded-md border border-gray-300 px-3 py-2 text-xs"
                  value={customMin}
                  onChange={(e) => {
                    onPriceFilterChange("CUSTOM");
                    onCustomMinChange(e.target.value);
                  }}
                />
                <input
                  type="number"
                  placeholder="Max"
                  className="w-full rounded-md border border-gray-300 px-3 py-2 text-xs"
                  value={customMax}
                  onChange={(e) => {
                    onPriceFilterChange("CUSTOM");
                    onCustomMaxChange(e.target.value);
                  }}
                />
              </div>
            </>
          )}
        </div>

        {/* === Location section === */}
        <div className="border-t border-gray-100 pt-3 mt-6">
          <button
            type="button"
            className="flex w-full items-center justify-between text-left"
            onClick={() => setIsLocationOpen((prev) => !prev)}
          >
            <p className="text-base font-medium text-gray-700">Location</p>
            <svg
              className={`w-4 h-4 text-gray-600 transition-transform ${
                isLocationOpen ? "rotate-180" : ""
              }`}
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M19 9l-7 7-7-7"
              />
            </svg>
          </button>

          {isLocationOpen && (
            <div className="mt-4 space-y-3 text-sm text-gray-700">
              {/* Near me checkbox */}
              <label className="flex items-center gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  className="h-4 w-4 rounded border border-gray-400"
                  checked={nearMe}
                  onChange={(e) => onNearMeChange(e.target.checked)}
                />
                <span>Near me</span>
              </label>

              {/* Radius options */}
              <div className="mt-3">

                <div className="space-y-1 text-sm">
                  {radiusOptions.map((value) => (
                    <label
                      key={value}
                      className={`flex items-center gap-2 ${
                        nearMe ? "cursor-pointer" : "cursor-not-allowed text-gray-300"
                      }`}
                    >
                      <input
                        type="radio"
                        name="radius"
                        className="h-4 w-4"
                        disabled={!nearMe}
                        checked={radiusMiles === value}
                        onChange={() => onRadiusChange(value)}
                      />
                      <span>
                        Within {value} mile{value > 1 ? "s" : ""}
                      </span>
                    </label>
                  ))}
                </div>
              </div>
            </div>
          )}

          <div className="border-t border-gray-100 pt-3 mt-6">
            
          </div>
        </div>
      </div>
    </aside>
  );
};
