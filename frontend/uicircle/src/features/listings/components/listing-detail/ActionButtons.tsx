import { HeartIcon as HeartSolid } from "@heroicons/react/24/outline";
import { MoreHorizontal } from "lucide-react";
import { ArrowUpTrayIcon } from "@heroicons/react/24/outline";

interface ActionButtonsProps {
  favoriteCount: number;
  onLike?: () => void;
  onShare?: () => void;
  onMore?: () => void;
}

const ActionButtons = ({
  favoriteCount,
  onLike,
  onShare,
  onMore,
}: ActionButtonsProps) => {
  return (
    <div className="flex justify-between mt-6 pt-6 border-t">
      <button
        onClick={onLike}
        className="flex items-center gap-2 text-gray-700 hover:text-primary"
      >
        <HeartSolid className="w-6 h-6 text-gray-700" />
        <span className="text-sm font-medium">Like ({favoriteCount})</span>
      </button>

      <button
        onClick={onShare}
        className="flex items-center gap-2 text-gray-700 hover:text-primary"
      >
        <ArrowUpTrayIcon className="w-5 h-5 text-gray-700" />
        <span className="text-sm font-medium">Share</span>
      </button>

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
