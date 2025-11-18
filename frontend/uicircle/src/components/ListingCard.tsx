import { useNavigate } from "react-router-dom";
import { ListingSummaryResponse } from "../features/listings/services/ListingService";

interface ListingCardProps {
  listing: ListingSummaryResponse;
}

const ListingCard = ({ listing }: ListingCardProps) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/listing/${listing.publicId}`);
  };

  return (
    <div
      onClick={handleClick}
      className="bg-background-light border border-border-light rounded-xl overflow-hidden hover:shadow-2xl hover:border-primary/30 transition-all duration-300 cursor-pointer transform hover:-translate-y-1"
    >
      <div className="aspect-video bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center relative overflow-hidden group">
        {listing.thumbnailUrl ? (
          <img
            src={listing.thumbnailUrl}
            alt={listing.title}
            className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
          />
        ) : (
          <div className="text-center">
            <svg className="w-16 h-16 text-gray-300 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <span className="text-gray-400 text-sm">No Image</span>
          </div>
        )}
        <div className="absolute top-2 right-2 bg-background-light/90 backdrop-blur-sm px-2 py-1 rounded-lg text-xs font-medium text-gray-700">
          {listing.categoryName}
        </div>
      </div>
      
      <div className="p-4">
        <h3 className="font-semibold text-gray-900 truncate mb-2 text-lg">
          {listing.title}
        </h3>
        <p className="text-gray-600 text-sm line-clamp-2 mb-3">
          {listing.description}
        </p>
        <div className="flex justify-between items-center pt-3 border-t border-border-light">
          <span className="text-primary font-bold text-xl">
            ${listing.price.toFixed(2)}
          </span>
          <div className="flex items-center gap-1.5 text-gray-500 text-sm">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
            <span className="truncate max-w-[120px]">{listing.sellerUsername}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ListingCard;
