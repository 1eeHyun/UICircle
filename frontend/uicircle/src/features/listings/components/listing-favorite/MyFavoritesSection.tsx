// src/features/listings/components/listing-favorite/MyFavoritesSection.tsx
import { useEffect, useState } from "react";
import {
  getMyFavoriteListings,
  ListingSummaryResponse,
} from "@/features/listings/services/ListingService";
import FavoritesEmptyState from "@/features/listings/components/listing-favorite/FavoritesEmptyState";
import FavoriteGridCard from "@/features/listings/components/listing-favorite/FavoriteGridCard";

type TabKey = "ALL" | "ON_SALE" | "SOLD_OUT" | "PRICE_DROP";

const TABS: { key: TabKey; label: string }[] = [
  { key: "ALL", label: "All" },
  { key: "ON_SALE", label: "On sale" },
  { key: "SOLD_OUT", label: "Sold out" },
  { key: "PRICE_DROP", label: "Price drop" },
];

export default function MyFavoritesSection() {
  const [activeTab, setActiveTab] = useState<TabKey>("ALL");
  const [favorites, setFavorites] = useState<ListingSummaryResponse[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchFavorites = async () => {
      try {
        setLoading(true);
        const data = await getMyFavoriteListings();
        setFavorites(data);
      } finally {
        setLoading(false);
      }
    };
    fetchFavorites();
  }, []);

  const filteredFavorites = favorites.filter((listing) => {
    if (activeTab === "ALL") return true;
    if (activeTab === "ON_SALE") return listing.status === "ACTIVE";
    if (activeTab === "SOLD_OUT") return listing.status === "SOLD";
    if (activeTab === "PRICE_DROP") {
      // TODO: price drop
      return false;
    }
    return true;
  });

  return (
    <div className="flex items-start gap-7"> 
      {/* Left menu */}
      <aside className="w-64 shrink-0 mt-6">
        <nav className="space-y-1">
          {TABS.map((tab) => {
            const isActive = tab.key === activeTab;
            return (
              <button
                key={tab.key}
                type="button"
                onClick={() => setActiveTab(tab.key)}
                className={`block w-full text-left text-[15px] tracking-tight
                  px-5 py-4 
                  rounded-none
                  ${
                    isActive
                      ? "bg-zinc-100 font-semibold text-zinc-900"
                      : "text-zinc-500 hover:bg-zinc-100 hover:text-zinc-900"
                  }
                `}
              >
                {tab.label}
              </button>
            );
          })}
        </nav>
      </aside>

      {/* RIght card */}
      <main className="flex-1 pt-6">
        {loading && favorites.length === 0 && (
          <p className="text-sm text-zinc-500">Loading favorites…</p>
        )}

        {!loading && filteredFavorites.length === 0 && <FavoritesEmptyState />}

        {!loading && filteredFavorites.length > 0 && (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
            {filteredFavorites.map((item) => (
              <FavoriteGridCard key={item.publicId} item={item} />
            ))}
          </div>          
        )}
      </main>
    </div>
  );
}
