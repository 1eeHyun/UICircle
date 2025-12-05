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
  onAddToCart,
  onMakeOffer,
}) => {
  return (
    <div>
      {/* Title & Price */}
      <h1 className="text-2xl font-bold text-gray-900 mb-4">{title}</h1>
      <div className="flex items-baseline gap-3 mb-6">
        <span className="text-3xl font-bold text-gray-900">
          {price === 0 ? "Free" : `$${price.toFixed(2)}`}
        </span>
      </div>

      {/* Action Buttons */}
      <div className="flex gap-3 mb-6">
        <button
          onClick={onAddToCart}
          className="flex-1 py-3 bg-gray-100 text-gray-900 rounded-lg font-bold hover:bg-gray-200"
        >
          Add to cart
        </button>
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
