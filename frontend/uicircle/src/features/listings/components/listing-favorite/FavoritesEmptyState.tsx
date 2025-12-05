// src/features/listings/components/listing-favorite/FavoritesEmptyState.tsx
import { useNavigate } from "react-router-dom";

export default function FavoritesEmptyState() {
  const navigate = useNavigate();

  return (
    <div className="flex h-full flex-col items-center justify-center text-center">      
      <div className="mb-6">
        <div className="h-32 w-32 rounded-full bg-yellow-100 flex items-center justify-center">
          <span className="text-4xl">🐣</span>
        </div>
      </div>

      <h2 className="mb-2 text-xl font-semibold text-zinc-900">
        Interested in an item?
      </h2>

      <p className="mb-6 text-sm text-zinc-500">
        After you like it, it’ll show up here.
      </p>

      <ul className="mb-6 space-y-3 text-sm text-zinc-500">
        <li className="flex items-center gap-2">
          <span>Tap the heart to save items you like.</span>
        </li>
        <li className="flex items-center gap-2">          
          <span>Get updates when the price drops.</span>
        </li>
        <li className="flex items-center gap-2">        
          <span>Sellers are more likely to send offers.</span>
        </li>
      </ul>

      <button
        type="button"
        onClick={() => navigate("/search")}
        className="text-sm font-semibold text-primary hover:underline"
      >
        Start browsing
      </button>
    </div>
  );
}
