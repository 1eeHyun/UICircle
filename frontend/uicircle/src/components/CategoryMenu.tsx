import { useState } from "react";
import { useNavigate } from "react-router-dom";
import type { CategoryResponse } from "../features/listings/services/ListingService";

interface CategoryMenuProps {
  categories: CategoryResponse[];
}

// Only used for URL path
const normalizeSlug = (slug: string) => slug.trim().replace(/\s+/g, "-");

const CategoryMenu = ({ categories }: CategoryMenuProps) => {
  // store raw slugs from backend, do NOT normalize here
  const [activeParentSlug, setActiveParentSlug] = useState<string | null>(null);
  const [activeChildSlug, setActiveChildSlug] = useState<string | null>(null);
  const navigate = useNavigate();

  const activeParent =
    categories.find((c) => c.categorySlug === activeParentSlug) ?? null;

  const activeChild =
    activeParent?.children?.find((c) => c.categorySlug === activeChildSlug) ??
    null;

  const goToCategory = (slug: string) => {
    navigate(`/category/${normalizeSlug(slug)}`);
  };

  return (
    // whole menu is relative so submenu can be absolute on top of content
    <div
      className="relative border-b bg-white z-30"
      onMouseLeave={() => {
        setActiveParentSlug(null);
        setActiveChildSlug(null);
      }}
    >
      {/* top-level categories row */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="flex justify-center gap-14 pt-4 pb-2 overflow-x-auto scrollbar-hide">
          {categories.map((category) => {
            const hasChildren =
              Array.isArray(category.children) &&
              category.children.length > 0;

            const isActive = activeParentSlug === category.categorySlug;
            const displayName = category.name.split("&")[0].trim();

            return (
              <button
                key={category.categorySlug}
                type="button"
                onMouseEnter={() => {
                  if (hasChildren) {
                    setActiveParentSlug(category.categorySlug);
                    setActiveChildSlug(null);
                  }
                }}
                onClick={() => {
                  if (hasChildren) {
                    setActiveParentSlug((prev) =>
                      prev === category.categorySlug ? null : category.categorySlug
                    );
                    setActiveChildSlug(null);
                  } else {
                    // no children → go directly to category page
                    goToCategory(category.categorySlug);
                  }
                }}
                className={`whitespace-nowrap border-b-2 pb-2 transition text-sm
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

      {/* mega menu: children of active parent on top of content */}
      {activeParent &&
        activeParent.children &&
        activeParent.children.length > 0 && (
          <div className="absolute inset-x-0 top-full bg-white shadow-md border-t">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
                {/* left: second-level categories */}
                <div>
                  <p className="text-sm font-semibold text-gray-900 mb-3">
                    {activeParent.name}
                  </p>
                  <ul className="space-y-2">
                    {activeParent.children.map((sub) => {
                      const isChildActive = activeChildSlug === sub.categorySlug;

                      return (
                        <li key={sub.categorySlug}>
                          <button
                            type="button"
                            onMouseEnter={() => {
                              if (sub.children && sub.children.length > 0) {
                                setActiveChildSlug(sub.categorySlug);
                              } else {
                                setActiveChildSlug(null);
                              }
                            }}
                            onClick={() => goToCategory(sub.categorySlug)}
                            className={`flex w-full justify-between text-sm transition
                              ${
                                isChildActive
                                  ? "text-primary font-semibold"
                                  : "text-gray-700 hover:text-primary"
                              }`}
                          >
                            <span>{sub.name}</span>
                            {sub.children && sub.children.length > 0 && (
                              <span className="text-xs text-gray-400">›</span>
                            )}
                          </button>
                        </li>
                      );
                    })}                    
                  </ul>
                </div>

                {/* right: third-level categories */}
                <div className="hidden md:block">
                  {activeChild &&
                  activeChild.children &&
                  activeChild.children.length > 0 ? (
                    <>
                      <p className="text-base font-semibold text-gray-900 mb-3">
                        {activeChild.name}
                      </p>
                      <ul className="space-y-2">
                        {activeChild.children.map((grand) => (
                          <li key={grand.categorySlug}>
                            <button
                              type="button"
                              onClick={() => goToCategory(grand.categorySlug)}
                              className="text-sm text-gray-700 hover:text-primary transition"
                            >
                              {grand.name}
                            </button>
                          </li>
                        ))}
                      </ul>
                    </>
                  ) : (
                    <p className="text-sm text-gray-400 mt-2">
                      
                    </p>
                  )}
                </div>
              </div>
            </div>
          </div>
        )}
    </div>
  );
};

export default CategoryMenu;
