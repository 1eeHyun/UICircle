import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "@/components/Navbar";
import CategoryMenu from "@/components/CategoryMenu";
import { getTopLevelCategories, ListingResponse, getListing, CategoryResponse } from "../services/ListingService";

const ListingDetailPage = () => {
  const { listingId } = useParams<{ listingId: string }>();
  const navigate = useNavigate();

  const [listing, setListing] = useState<ListingResponse | null>(null);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>("");
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const listingData = await getListing(listingId!);
        setListing(listingData);

        const categoriesData = await getTopLevelCategories();
        setCategories(categoriesData);
      } catch (err: any) {
        setError(err?.response?.data?.message || "Failed to load listing");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [listingId]);

  const handleClick = () => {
    navigate(`/listings/${listing?.publicId}`);  // Match the route
  };

  if (loading) {
    return ( 
      <div className="min-h-screen bg-surface-light">
        <Navbar />
        <div className="flex justify-center items-center h-96">
          <div className="text-gray-500">Loading listing...</div>
        </div>
      </div>
    );
  }

  if (error || !listing) {
    return (
      <div className="min-h-screen bg-surface-light">
        <Navbar />
        <div className="max-w-6xl mx-auto px-4 py-8">
          <div className="bg-red-50 p-4 rounded-md">
            <p className="text-red-800">{error || "Listing not found"}</p>
          </div>
          <button
            onClick={() => navigate("/home")}
            className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
          >
            Back to Home
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-surface-light">
      <Navbar />
      <CategoryMenu categories={categories} />
      
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {/* Images Section */}
          <div>
            <div className="bg-gray-200 rounded-lg overflow-hidden mb-4">
              {listing.images && listing.images.length > 0 ? (
                <img
                  src={listing.images[selectedImageIndex].imageUrl}
                  alt={listing.title}
                  className="w-full h-96 object-cover"
                />
              ) : (
                <div className="w-full h-96 flex items-center justify-center bg-gray-300">
                  <span className="text-gray-600">No image available</span>
                </div>
              )}
            </div>

            {/* Image Thumbnails */}
            {listing.images && listing.images.length > 0 && (
              <div className="flex gap-2 overflow-x-auto">
                {listing.images.map((image, index) => (
                  <button
                    key={index}
                    onClick={() => setSelectedImageIndex(index)}
                    className={`flex-shrink-0 w-20 h-20 rounded-md overflow-hidden border-2 ${
                      selectedImageIndex === index
                        ? "border-blue-600"
                        : "border-gray-300"
                    }`}
                  >
                    <img
                      src={image.imageUrl}
                      alt={`Thumbnail ${index + 1}`}
                      className="w-full h-full object-cover"
                    />
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Details Section */}
          <div>
            <div className="mb-6">
              <h1 className="text-4xl font-bold text-gray-900 mb-2">
                {listing.title}
              </h1>
              <div className="flex items-center gap-4 text-gray-600">
                <span>{listing.category.name}</span>
                <span>•</span>
                <span className="text-sm">{new Date(listing.createdAt).toLocaleDateString()}</span>
              </div>
            </div>

            {/* Price */}
            <div className="mb-6">
              <p className="text-gray-600 text-sm mb-1">Price</p>
              <p className="text-4xl font-bold text-blue-600">
                {listing.price === 0 ? "Free" : `$${listing.price.toFixed(2)}`}
              </p>
            </div>

            {/* Condition */}
            <div className="mb-6">
              <p className="text-gray-600 text-sm mb-2">Condition</p>
              <span className="inline-block px-4 py-2 bg-gray-200 rounded-full text-sm font-medium">
                {listing.condition}
              </span>
            </div>

            {/* Status */}
            <div className="mb-6">
              <p className="text-gray-600 text-sm mb-2">Status</p>
              <span
                className={`inline-block px-4 py-2 rounded-full text-sm font-medium ${
                  listing.status === "ACTIVE"
                    ? "bg-green-100 text-green-800"
                    : "bg-red-100 text-red-800"
                }`}
              >
                {listing.status}
              </span>
            </div>

            {/* Seller Info */}
            <div className="mb-6 p-4 bg-white rounded-lg border border-gray-200">
              <p className="text-gray-600 text-sm mb-2">Seller</p>
              <p className="font-semibold text-lg">{listing.seller.username}</p>
            </div>

            {/* Stats */}
            <div className="grid grid-cols-2 gap-4 mb-6">
              <div className="p-4 bg-white rounded-lg border border-gray-200">
                <p className="text-gray-600 text-sm">Views</p>
                <p className="text-2xl font-bold">{listing.viewCount}</p>
              </div>
              <div className="p-4 bg-white rounded-lg border border-gray-200">
                <p className="text-gray-600 text-sm">Favorites</p>
                <p className="text-2xl font-bold">{listing.favoriteCount}</p>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex gap-4">
              <button className="flex-1 px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium">
                Message Seller
              </button>
              <button className="px-6 py-3 bg-gray-200 text-gray-900 rounded-md hover:bg-gray-300 font-medium">
                ❤️ Save
              </button>
            </div>
          </div>
        </div>

        {/* Description */}
        <div className="mt-12 bg-white rounded-lg shadow-md p-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Description</h2>
          <p className="text-gray-700 whitespace-pre-wrap leading-relaxed">
            {listing.description}
          </p>
        </div>

        {/* Back Button */}
        <button
          onClick={() => navigate(-1)}
          className="mt-8 px-4 py-2 bg-gray-200 text-gray-900 rounded-md hover:bg-gray-300"
        >
          ← Back
        </button>
      </div>
    </div>
  );
};

export { ListingDetailPage };