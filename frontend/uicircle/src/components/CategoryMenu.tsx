import { useState } from "react";
import { useNavigate } from "react-router-dom";
import type { CategoryResponse } from "../features/listings/services/ListingService";

interface CategoryMenuProps {
  categories: CategoryResponse[];
}

const CategoryMenu = ({ categories }: CategoryMenuProps) => {
  // Currently active (hovered / clicked) parent category
  const [activeParentSlug, setActiveParentSlug] = useState<string | null>(null);
  const navigate = useNavigate();

  // Find active parent category object
  const activeParent = categories.find(
    (c) => c.categorySlug === activeParentSlug
  );

  // Only subcategories navigate
  const handleSubcategoryClick = (slug: string) => {
    navigate(`/category/${slug}`);
  };

  return (
    // Whole block: top tabs + bottom panel
    <div
      className="bg-background-light border-b border-border-light"
      onMouseLeave={() => setActiveParentSlug(null)}
    >
      {/* Top-level category tabs (like Mercari nav) */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-center gap-4 py-3 overflow-x-auto scrollbar-hide">
          {categories.map((category) => {
            const hasChildren =
              Array.isArray(category.children) && category.children.length > 0;
            const isActive = activeParentSlug === category.categorySlug;

            return (
              <button
                key={category.categorySlug}
                type="button"
                onMouseEnter={() => {
                  if (hasChildren) setActiveParentSlug(category.categorySlug);
                }}
                onClick={() => {
                  // Parent just toggles active state, never navigates
                  if (hasChildren) {
                    setActiveParentSlug((prev) =>
                      prev === category.categorySlug ? null : category.categorySlug
                    );
                  }
                }}
                className={`whitespace-nowrap border-b-2 pb-2 transition
                  text-sm md:text-sm font-medium
                  ${
                    isActive
                      ? "border-primary text-primary"
                      : "border-transparent text-gray-700 hover:text-primary hover:border-primary/40"
                  }`}
              >
                {category.name}
              </button>
            );
          })}
        </div>
      </div>

      {/* Bottom mega menu panel (full width under tabs) */}
      {activeParent &&
        activeParent.children &&
        activeParent.children.length > 0 && (
          <div className="border-t border-border-light bg-background-light shadow-[0_8px_24px_rgba(15,23,42,0.08)]">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
              {/* Title like "Video Games & Consoles" */}
              <h3 className="text-base md:text-lg font-semibold text-gray-900 mb-4">
                {activeParent.name}
              </h3>

              {/* Left column: vertical list of subcategories */}
              <ul className="space-y-2">
                {activeParent.children.map((sub) => (
                  <li key={sub.categorySlug}>
                    <button
                      type="button"
                      onClick={() => handleSubcategoryClick(sub.categorySlug)}
                      className="text-sm md:text-base text-gray-700 hover:text-primary transition"
                    >
                      {sub.name}
                    </button>
                  </li>
                ))}

                {/* Optional: "View all" like Mercari */}
                {/* <li className="pt-2">
                  <button
                    type="button"
                    onClick={() => handleSubcategoryClick(activeParent.categorySlug)}
                    className="text-sm font-semibold text-gray-900 hover:text-primary transition"
                  >
                    View all
                  </button>
                </li> */}
              </ul>
            </div>
          </div>
        )}
    </div>
  );
};

export default CategoryMenu;
