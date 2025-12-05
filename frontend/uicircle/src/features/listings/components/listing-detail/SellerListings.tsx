// src/features/listings/components/listing-detail/SellerListings.tsx
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  ListingSummaryResponse,
  getSellerListingsBySeller,
} from "@/features/listings/services/ListingService";

interface SellerListingsProps {
  sellerPublicId: string;
  currentListingPublicId: string;
  onSeeAll?: () => void;
}

const SellerListings = ({
  sellerPublicId,
  currentListingPublicId,
  onSeeAll,
}: SellerListingsProps) => {
  const [items, setItems] = useState<ListingSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchSellerListings = async () => {
      try {
        setLoading(true);
        const page = await getSellerListingsBySeller(sellerPublicId, 0, 6);
        const filtered = page.content.filter(
          (item) => item.publicId !== currentListingPublicId
        );
        setItems(filtered);
      } catch (e) {
        console.error("Failed to load seller listings", e);
      } finally {
        setLoading(false);
      }
    };

    if (sellerPublicId) {
      fetchSellerListings();
    }
  }, [sellerPublicId, currentListingPublicId]);

  if (loading) {
    return (
      <div className="border-t mt-8 pt-8">
        <h2 className="text-lg font-bold text-gray-900 mb-4">
          More from this seller
        </h2>
        <p className="text-sm text-gray-500">Loading other items…</p>
      </div>
    );
  }

  if (!items.length) {
    return null;
  }

  return (
    <div className="border-t mt-8 pt-8">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-lg font-bold text-gray-900">
          More from this seller
        </h2>
        <button
          onClick={onSeeAll}
          className="text-sm text-primary hover:underline font-medium"
        >
          See all
        </button>
      </div>

      <div className="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 lg:grid-cols-6 gap-3">
        {items.map((item) => (
            <button
            key={item.publicId}
            onClick={() => navigate(`/listings/${item.publicId}`)}
            className="rounded-lg overflow-hidden hover:shadow-md transition-shadow"
            >
            <div className="w-full aspect-square bg-gray-100 overflow-hidden">
                {item.thumbnailUrl && (
                <img
                    src={item.thumbnailUrl}
                    alt={item.title}
                    className="w-full h-full object-cover transition-transform duration-200 ease-out hover:scale-105"
                />
                )}
            </div>
            <div className="p-1">
                <p className="text-xs font-medium line-clamp-2">{item.title}</p>
                <p className="mt-1 text-xs font-bold">
                ${item.price.toLocaleString()}
                </p>
            </div>
            </button>
        ))}
        </div>
    </div>
  );
};

export default SellerListings;
