interface BreadcrumbProps {
  categoryName: string;
  listingTitle: string;
  onHomeClick: () => void;
  onCategoryClick: () => void;
}

const Breadcrumb = ({
  categoryName,
  listingTitle,
  onHomeClick,
  onCategoryClick,
}: BreadcrumbProps) => {
  return (
    <nav className="flex items-center gap-2 text-sm text-gray-600 mb-6">
      <button onClick={onHomeClick} className="hover:underline">
        UICircle
      </button>
      <span>&gt;</span>
      <button onClick={onCategoryClick} className="hover:underline">
        {categoryName}
      </button>
      <span>&gt;</span>
      <span className="text-gray-900 truncate">{listingTitle}</span>
    </nav>
  );
};

export default Breadcrumb;
