interface Category {
  name: string;
}

interface ProductDetailsProps {
  condition: string;
  category: Category;
  createdAt: string;
  onCategoryClick?: () => void;
}

const ProductDetails = ({
  condition,
  category,
  createdAt,
  onCategoryClick,
}: ProductDetailsProps) => {
  return (
    <div className="border-t pt-6 mt-6">
      <h2 className="text-lg font-bold text-gray-900 mb-2">Details</h2>
      <div className="space-y-3">
        <div className="flex">
          <dt className="w-32 text-sm text-gray-600 flex items-center gap-1">
            Condition
          </dt>
          <dd className="flex-1 text-sm text-gray-900">{condition}</dd>
        </div>

        <div className="flex">
          <dt className="w-32 text-sm text-gray-600">Category</dt>
          <dd className="flex-1 text-sm">
            <span
              onClick={onCategoryClick}
              className="text-sm text-gray-900 hover:underline cursor-pointer"
            >
              {category.name}
            </span>
          </dd>
        </div>

        <div className="flex">
          <dt className="w-32 text-sm text-gray-600">Posted</dt>
          <dd className="flex-1 text-sm text-gray-900">
            {new Date(createdAt).toLocaleDateString()}
          </dd>
        </div>
      </div>
    </div>
  );
};

export default ProductDetails;
