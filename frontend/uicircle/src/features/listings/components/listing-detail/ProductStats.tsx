import { Eye } from "lucide-react";
import { HeartIcon as HeartSolid } from "@heroicons/react/24/outline";

interface ProductStatsProps {
  viewCount: number;
  favoriteCount: number;
}

const ProductStats = ({ viewCount, favoriteCount }: ProductStatsProps) => {
  return (
    <div className="flex items-center gap-2 text-sm text-gray-600 mb-6">
      <span className="flex items-center gap-1">
        <Eye size={16} className="text-gray-600" />
        {viewCount} views
      </span>

      <span className="flex items-center gap-1">
        <svg
          className="w-4 h-4 text-red-500 fill-current"
          viewBox="0 0 20 20"
        >
          <HeartSolid className="w-6 h-6 text-gray-700" />
        </svg>
        Likes ({favoriteCount})
      </span>
    </div>
  );
};

export default ProductStats;
