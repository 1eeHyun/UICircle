// src/features/listings/components/listing-detail/ProductInfo.tsx
import React from "react";

interface ProductInfoProps {
  title: string;
  price: number;
  onAddToCart?: () => void;
  onMakeOffer?: () => void;
}

const ProductInfo: React.FC<ProductInfoProps> = ({
  title,
  price,
  onMakeOffer,
}) => {
  return (
    <div>
      {/* Title & Price */}
      <h1 className="text-2xl font-bold text-gray-900 mb-4 break-words">
        {title}
      </h1>

      <div className="flex items-baseline gap-3 mb-6">
        <span className="text-3xl font-bold text-gray-900">
          {price === 0 ? "Free" : `$${price.toFixed(2)}`}
        </span>
      </div>

      {/* Action Buttons */}
      <div className="flex gap-3 mb-6">
        <button
          onClick={onMakeOffer}
          className="flex-1 py-3 bg-primary text-white rounded-lg font-bold hover:bg-primary-light"
        >
          Make an offer
        </button>
      </div>
    </div>
  );
};

export default ProductInfo;
