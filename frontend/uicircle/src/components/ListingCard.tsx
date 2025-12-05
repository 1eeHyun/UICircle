import { useNavigate } from "react-router-dom";
import { ListingSummaryResponse } from "@/features/listings/services/ListingService";

interface ListingCardProps {
  listing: ListingSummaryResponse;
}

const ListingCard = ({ listing }: ListingCardProps) => {
  const navigate = useNavigate();

  return (
    <div
      onClick={() => navigate(`/listings/${listing.publicId}`)}
      className="
        rounded-xl overflow-hidden 
        cursor-pointer transition-shadow duration-400        
      "
    >
      <div className="relative overflow-hidden aspect-square group">
        {listing.thumbnailUrl ? (
          <img
            src={listing.thumbnailUrl}
            alt={listing.title}
            className="
              w-full h-full object-cover
              transition-transform duration-300 ease-out
              group-hover:scale-110
            "
          />
        ) : (
          <div className="flex flex-col items-center justify-center h-full text-gray-400">
            <svg
              className="w-16 h-16 mb-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={1.5}
                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
              />
            </svg>
            <span>No Image</span>
          </div>
        )}        
      </div>

      <div className="p-3">
        <h3 className="text-gray-500 text-sm line-clamp-1">
          {listing.title}
        </h3>

        <div className="flex justify-between items-center mt-1">
          <span className="text-sm font-bold text-gray-900">
            ${listing.price.toFixed(2)}
          </span>
        </div>
      </div>
    </div>
  );
};

export default ListingCard;
