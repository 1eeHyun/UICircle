interface Seller {
  username: string;
  avatarUrl?: string | null;
}

interface SellerCardProps {
  seller: Seller;
  salesCount: number;
  onViewProfile?: () => void;
}

const SellerCard = ({ seller, salesCount, onViewProfile }: SellerCardProps) => {
  return (
    <div className="mt-8 pt-2">
      {/* Title */}
      <p className="text-lg font-semibold text-gray-900 mb-4">
        Meet the seller
      </p>

      {/* Seller row */}
      <div className="w-full flex items-center justify-between">
        {/* Left side (avatar + info) */}
        <div className="flex items-center gap-4">
          {/* Avatar */}
          <div className="w-12 h-12 bg-primary/10 rounded-full flex items-center justify-center">
            <span className="text-lg font-bold text-primary">
              {seller.username.charAt(0).toUpperCase()}
            </span>
          </div>

          {/* Seller text */}
          <div className="flex flex-col">
            <span className="font-semibold text-gray-900">
              {seller.username}
            </span>

            {/* Sub info */}
            <span className="text-sm text-gray-500">
              {salesCount} sales
            </span>
          </div>
        </div>

        {/* Right side button */}
        <button
          onClick={onViewProfile}
          className="px-4 py-2 rounded-lg bg-gray-100 text-sm font-medium hover:bg-gray-200"
        >
          View profile
        </button>
      </div>
    </div>
  );
};

export default SellerCard;
