interface SellerListingsProps {
  onSeeAll?: () => void;
}

const SellerListings = ({ onSeeAll }: SellerListingsProps) => {
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

      <div className="grid grid-cols-3 gap-4">
        {/* TODO: get seller's listing items */}
        {/* Placeholder items will be added here */}
      </div>
    </div>
  );
};

export default SellerListings;
