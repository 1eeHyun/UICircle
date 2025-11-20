import { useState, useEffect } from "react";
import {
  getSavedSearches,
  deleteSavedSearch,
  deleteAllSavedSearches,
  SavedSearchResponse,
} from "../services/SearchService";

interface SavedSearchesProps {
  onLoadSearch: (savedSearch: SavedSearchResponse) => void;
}

const SavedSearches = ({ onLoadSearch }: SavedSearchesProps) => {
  const [savedSearches, setSavedSearches] = useState<SavedSearchResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [isExpanded, setIsExpanded] = useState(false);

  useEffect(() => {
    loadSavedSearches();
  }, []);

  const loadSavedSearches = async () => {
    try {
      setLoading(true);
      const searches = await getSavedSearches();
      setSavedSearches(searches);
    } catch (error) {
      console.error("Failed to load saved searches:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (publicId: string, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await deleteSavedSearch(publicId);
      await loadSavedSearches();
    } catch (error) {
      console.error("Failed to delete saved search:", error);
    }
  };

  const handleDeleteAll = async () => {
    if (!window.confirm("Are you sure you want to delete all saved searches?")) {
      return;
    }
    try {
      await deleteAllSavedSearches();
      await loadSavedSearches();
    } catch (error) {
      console.error("Failed to delete all saved searches:", error);
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };

  if (loading) {
    return null;
  }

  if (savedSearches.length === 0) {
    return null;
  }

  return (
    <div className="bg-background-light border-b border-border-light">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3">
        <div className="flex items-center justify-between">
          <button
            onClick={() => setIsExpanded(!isExpanded)}
            className="flex items-center gap-2 text-sm font-medium text-gray-700 hover:text-primary transition"
          >
            <svg
              className={`w-4 h-4 transition-transform ${isExpanded ? "rotate-180" : ""}`}
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
            </svg>
            <span>Saved Searches ({savedSearches.length})</span>
          </button>
          {isExpanded && savedSearches.length > 0 && (
            <button
              onClick={handleDeleteAll}
              className="text-xs text-red-600 hover:text-red-700 transition"
            >
              Delete All
            </button>
          )}
        </div>

        {isExpanded && (
          <div className="mt-3 space-y-2">
            {savedSearches.map((search) => (
              <div
                key={search.publicId}
                onClick={() => onLoadSearch(search)}
                className="flex items-center justify-between p-3 bg-white rounded-lg border border-border-light hover:border-primary/30 hover:shadow-sm transition cursor-pointer group"
              >
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2">
                    <h4 className="font-medium text-gray-900 truncate">{search.name}</h4>
                    <span className="text-xs text-gray-500">{formatDate(search.createdAt)}</span>
                  </div>
                  {search.query && (
                    <p className="text-sm text-gray-600 truncate mt-1">"{search.query}"</p>
                  )}
                </div>
                <button
                  onClick={(e) => handleDelete(search.publicId, e)}
                  className="ml-3 p-1 text-gray-400 hover:text-red-600 transition opacity-0 group-hover:opacity-100"
                  title="Delete saved search"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                    />
                  </svg>
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default SavedSearches;

