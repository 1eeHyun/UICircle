// src/features/listings/pages/CategoryPage.tsx
import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import Navbar from "../../../components/Navbar";
import CategoryMenu from "../../../components/CategoryMenu";
import {
  getTopLevelCategories,
  getListingsByCategory,
  getNearbyListings,
  CategoryResponse,
  ListingSummaryResponse,
} from "../services/ListingService";
import { CategoryFilterSidebar } from "@/features/listings/components/listing-category/CategoryFilterSidebar";
import { CategoryListingGrid } from "@/features/listings/components/listing-category/CategoryListingGrid";

type PriceFilter =
  | "ANY"
  | "UNDER_25"
  | "RANGE_25_50"
  | "RANGE_50_100"
  | "OVER_200"
  | "CUSTOM";

const CategoryPage = () => {
  const { categorySlug } = useParams<{ categorySlug: string }>();
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [listings, setListings] = useState<ListingSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // price filter
  const [priceFilter, setPriceFilter] = useState<PriceFilter>("ANY");
  const [customMin, setCustomMin] = useState<string>("");
  const [customMax, setCustomMax] = useState<string>("");

  // location filter
  const [nearMe, setNearMe] = useState(false);
  const [radiusMiles, setRadiusMiles] = useState<number>(5); // default 5 miles
  const [lastCoords, setLastCoords] = useState<{ lat: number; lng: number } | null>(
    null
  );

  // load category listings
  useEffect(() => {
    const fetchData = async () => {
      if (!categorySlug) return;

      try {
        setLoading(true);
        setError("");

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

    // reset filters when category changes
    setPriceFilter("ANY");
    setCustomMin("");
    setCustomMax("");
    setNearMe(false);
    setRadiusMiles(5);
  }, [categorySlug]);

  const readableCategory =
    categorySlug?.replace(/-/g, " ").replace(/\b\w/g, (c) => c.toUpperCase()) ??
    "Category";

  // helper to fetch nearby listings (reusable)
  const fetchNearbyWithCoords = async (lat: number, lng: number, radius: number) => {
    if (!categorySlug) return;

    try {
      setLoading(true);
      const nearby = await getNearbyListings({
        latitude: lat,
        longitude: lng,
        radiusMiles: radius,
        categorySlug,
      });
      setListings(nearby);
    } catch (err: any) {
      console.error("nearby error", err);
      setError(err?.response?.data?.message || "Failed to load nearby listings");
    } finally {
      setLoading(false);
    }
  };

  const filteredListings = useMemo(() => {
    if (!listings || listings.length === 0) return [];

    let min = 0;
    let max = Number.POSITIVE_INFINITY;

    switch (priceFilter) {
      case "UNDER_25":
        max = 25;
        break;
      case "RANGE_25_50":
        min = 25;
        max = 50;
        break;
      case "RANGE_50_100":
        min = 50;
        max = 100;
        break;
      case "OVER_200":
        min = 200;
        max = Number.POSITIVE_INFINITY;
        break;
      case "CUSTOM": {
        const parsedMin = Number(customMin);
        const parsedMax = Number(customMax);
        if (!Number.isNaN(parsedMin)) min = parsedMin;
        if (!Number.isNaN(parsedMax)) max = parsedMax;
        break;
      }
      case "ANY":
      default:
        break;
    }

    return listings.filter((item) => {
      const price = Number(item.price);
      if (Number.isNaN(price)) return false;
      if (price < min) return false;
      if (price > max) return false;
      return true;
    });
  }, [listings, priceFilter, customMin, customMax]);

  const handleNearMeChange = (checked: boolean) => {
    if (!categorySlug) return;

    if (checked) {
      if (!("geolocation" in navigator)) {
        alert("This browser does not support geolocation.");
        return;
      }

      setError("");
      setNearMe(true);

      // get current position
      setLoading(true);
      navigator.geolocation.getCurrentPosition(
        async (pos) => {
          const { latitude, longitude } = pos.coords;
          setLastCoords({ lat: latitude, lng: longitude });
          await fetchNearbyWithCoords(latitude, longitude, radiusMiles);
        },
        (geoErr) => {
          console.error("geo error", geoErr);

          let message = "Could not get your location.";
          switch (geoErr.code) {
            case geoErr.PERMISSION_DENIED:
              message =
                "Location permission was denied. Please allow location access in your browser settings.";
              break;
            case geoErr.POSITION_UNAVAILABLE:
              message = "Location information is unavailable.";
              break;
            case geoErr.TIMEOUT:
              message = "Timed out while getting your location.";
              break;
          }

          alert(message);
          setNearMe(false);
          setLoading(false);
        }
      );
    } else {
      // disable near-me → restore normal category list
      const reload = async () => {
        if (!categorySlug) return;
        try {
          setLoading(true);
          setError("");
          const listingsData = await getListingsByCategory(
            categorySlug,
            0,
            20,
            "createdAt",
            "DESC"
          );
          setListings(listingsData.content);
        } catch (err: any) {
          console.error(err);
          setError(err?.response?.data?.message || "Failed to load category");
        } finally {
          setLoading(false);
        }
      };

      setNearMe(false);
      reload();
    }
  };

  const handleRadiusChange = (value: number) => {
    setRadiusMiles(value);

    if (nearMe && lastCoords) {
      fetchNearbyWithCoords(lastCoords.lat, lastCoords.lng, value);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-surface-light">
        <Navbar />
        <div className="flex justify-center items-center h-96">
          <div className="text-center">
            <svg
              className="animate-spin h-12 w-12 text-primary mx-auto mb-4"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                className="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                strokeWidth="4"
              ></circle>
              <path
                className="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              ></path>
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
            <svg
              className="w-16 h-16 text-red-500 mx-auto mb-4"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
            <p className="text-red-600 font-medium">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen">
      <Navbar />
      <CategoryMenu categories={categories} />

      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {/* breadcrumb */}
        <p className="text-sm text-gray-500 mb-4 pl-4">
          Home &gt; {readableCategory}
        </p>

        {/* left: filters / right: listing grid */}
        <div className="flex gap-6">
          <CategoryFilterSidebar
            categoryName={readableCategory}
            resultCount={filteredListings.length}
            priceFilter={priceFilter}
            customMin={customMin}
            customMax={customMax}
            onPriceFilterChange={setPriceFilter}
            onCustomMinChange={setCustomMin}
            onCustomMaxChange={setCustomMax}
            nearMe={nearMe}
            radiusMiles={radiusMiles}
            onNearMeChange={handleNearMeChange}
            onRadiusChange={handleRadiusChange}
          />
          <CategoryListingGrid listings={filteredListings} />
        </div>
      </div>
    </div>
  );
};

export { CategoryPage };
