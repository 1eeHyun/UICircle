import { useNavigate } from "react-router-dom";
import { ListingSummaryResponse } from "@/features/listings/services/ListingService";

export default function FavoriteGridCard({ item }: { item: ListingSummaryResponse }) {
  const navigate = useNavigate();

  return (
    <button
      onClick={() => navigate(`/listings/${item.publicId}`)}
      className="rounded-lg transition-shadow dark:bg-zinc-900 w-full"
    >
      <div className="w-full aspect-square bg-gray-100 dark:bg-zinc-800 overflow-hidden">
        {item.thumbnailUrl ? (
          <img
            src={item.thumbnailUrl}
            alt={item.title}
            className="w-full h-full object-cover transition-transform duration-300 ease-out hover:scale-110"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center text-xs text-zinc-500 dark:text-zinc-400">
            No Image
          </div>
        )}
      </div>

      <div className="px-2 py-2 flex flex-col items-start w-full">
        <p className="text-sm font-medium text-left line-clamp-1 truncate break-words w-full">
          {item.title}
        </p>

        <p className="mt-1 text-sm font-bold text-left text-zinc-900 dark:text-zinc-100 w-full">
          ${item.price.toLocaleString()}
        </p>
      </div>
    </button>
  );
}
