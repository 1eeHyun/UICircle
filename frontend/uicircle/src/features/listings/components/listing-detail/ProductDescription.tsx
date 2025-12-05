interface ProductDescriptionProps {
  description: string;
}

const ProductDescription = ({ description }: ProductDescriptionProps) => {
  return (
    <div className="mt-3 pt-2">
      <h2 className="text-lg font-bold text-gray-900 mb-2">Description</h2>
      <p className="text-sm text-gray-700 whitespace-pre-wrap leading-relaxed">
        {description || "No description provided."}
      </p>
    </div>
  );
};

export default ProductDescription;
