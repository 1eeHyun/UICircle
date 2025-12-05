import { useState } from "react";
import { useNavigate } from "react-router-dom";
import type { CategoryResponse } from "../features/listings/services/ListingService";

interface CategoryMenuProps {
  categories: CategoryResponse[];
}

const normalizeSlug = (slug: string) =>
  slug.trim().replace(/\s+/g, "-");

const CategoryMenu = ({ categories }: CategoryMenuProps) => {
  const [activeParentSlug, setActiveParentSlug] = useState<string | null>(null);
  const navigate = useNavigate();

  const activeParent = categories.find(
    (c) => normalizeSlug(c.categorySlug) === activeParentSlug
  );

  const handleSubcategoryClick = (slug: string) => {
    navigate(`/category/${normalizeSlug(slug)}`);
  };

  return (
    <div className="border-b" onMouseLeave={() => setActiveParentSlug(null)}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="flex justify-center gap-14 pt-4 pb-2 overflow-x-auto scrollbar-hide">
          {categories.map((category) => {
            const hasChildren =
              Array.isArray(category.children) && category.children.length > 0;
            const isActive = activeParentSlug === category.categorySlug;
            
            const displayName = category.name.split("&")[0].trim();
            return (
              <button
                key={category.categorySlug}
                type="button"
                onMouseEnter={() => {
                  if (hasChildren) setActiveParentSlug(category.categorySlug);
                }}
                onClick={() => {
                  if (hasChildren) {
                    setActiveParentSlug((prev) =>
                      prev === category.categorySlug ? null : category.categorySlug
                    );
                  }
                }}
                className={`whitespace-nowrap border-b-2 pb-2 transition
                  text-sm md:text-sm
                  ${
                    isActive
                      ? "border-primary text-primary"
                      : "border-transparent text-gray-700 hover:text-primary hover:border-primary/40"
                  }`}
              >
                {displayName}
              </button>
            );
          })}
        </div>
      </div>

      {activeParent &&
        activeParent.children &&
        activeParent.children.length > 0 && (
          <div className="border-t">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
              <ul className="space-y-2">
                {activeParent.children.map((sub) => (
                  <li key={normalizeSlug(sub.categorySlug)}>
                    <button
                      type="button"
                      onClick={() => handleSubcategoryClick(sub.categorySlug)}
                      className="text-sm md:text-sm text-gray-700 hover:text-primary transition"
                    >
                      {sub.name}
                    </button>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        )}
    </div>
  );
};

export default CategoryMenu;
