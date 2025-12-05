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

      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {items.map((item) => (
          <button
            key={item.publicId}
            onClick={() => navigate(`/listing/${item.publicId}`)}
            className="text-left border rounded-lg overflow-hidden hover:shadow-sm transition-shadow"
          >
            <div className="aspect-square bg-gray-100 overflow-hidden">
              {item.thumbnailUrl && (
                <img
                  src={item.thumbnailUrl}
                  alt={item.title}
                  className="w-full h-full object-cover"
                />
              )}
            </div>
            <div className="p-2">
              <p className="text-sm font-medium line-clamp-2">{item.title}</p>
              <p className="mt-1 text-sm font-bold">
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
