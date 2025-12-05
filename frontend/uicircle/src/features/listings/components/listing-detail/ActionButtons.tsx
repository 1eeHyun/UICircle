// src/features/listings/components/listing-detail/ActionButtons.tsx
import {
  HeartIcon as HeartOutline,
  ArrowUpTrayIcon,
} from "@heroicons/react/24/outline";
import { HeartIcon as HeartSolid } from "@heroicons/react/24/solid";
import { MoreHorizontal } from "lucide-react";

interface ActionButtonsProps {
  favoriteCount: number;
  isFavorited: boolean;
  onLike?: () => void;
  onShare?: () => void;
  onMore?: () => void;
}

const ActionButtons = ({
  favoriteCount,
  isFavorited,
  onLike,
  onShare,
  onMore,
}: ActionButtonsProps) => {
  return (
    <div className="flex justify-between mt-6 pt-6 border-t">
      {/* Like */}
      <button
        onClick={onLike}
        className="flex items-center gap-2 text-gray-700 hover:text-primary"
      >
        {isFavorited ? (
          <HeartSolid className="w-6 h-6 text-red-500" />
        ) : (
          <HeartOutline className="w-6 h-6 text-gray-700" />
        )}
        <span className="text-sm font-medium">Like ({favoriteCount})</span>
      </button>

      {/* Share */}
      <button
        onClick={onShare}
        className="flex items-center gap-2 text-gray-700 hover:text-primary"
      >
        <ArrowUpTrayIcon className="w-5 h-5 text-gray-700" />
        <span className="text-sm font-medium">Share</span>
      </button>

      {/* More */}
      <button
        onClick={onMore}
        className="flex items-center gap-2 text-gray-700 hover:text-primary"
      >
        <MoreHorizontal size={20} className="text-gray-700" />
        <span className="text-sm font-medium">More</span>
      </button>
    </div>
  );
};

export default ActionButtons;
