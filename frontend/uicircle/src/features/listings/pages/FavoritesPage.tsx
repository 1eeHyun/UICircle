import { useNavigate } from "react-router-dom";
import Navbar from "@/components/Navbar";
import MyFavoritesSection from "../components/listing-favorite/MyFavoritesSection";

export default function FavoritesPage() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-surface-light">
      <Navbar />

      <div className="max-w-6xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="flex items-center gap-3 mb-6">
          <button
            onClick={() => navigate(-1)}
            className="p-2 hover:bg-gray-200 rounded-lg transition-colors"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <h1 className="text-2xl font-bold text-gray-900">My Favorites</h1>
        </div>

        {/* Favorites Content */}
        <MyFavoritesSection />
      </div>
    </div>
  );
}

