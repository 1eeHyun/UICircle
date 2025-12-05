import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "@/components/Navbar";
import CategoryMenu from "@/components/CategoryMenu";
import {
  getTopLevelCategories,
  ListingResponse,
  getListing,
  CategoryResponse,
} from "../services/ListingService";

import ImageGallery from "@/features/listings/components/listing-detail/ImageGallery";
import ActionButtons from "@/features/listings/components/listing-detail/ActionButtons";
import SellerCard from "@/features/listings/components/listing-detail/SellerCard";
import ProductInfo from "@/features/listings/components/listing-detail/ProductInfo";
import ProductStats from "@/features/listings/components/listing-detail/ProductStats";
import ProductDetails from "@/features/listings/components/listing-detail/ProductDetails";
import ProductDescription from "@/features/listings/components/listing-detail/ProductDescription";
import SellerListings from "@/features/listings/components/listing-detail/SellerListings";
import Breadcrumb from "@/features/listings/components/listing-detail/Breadcrumb";
import LocationMap from "@/features/listings/components/listing-detail/LocationMap";

const ListingDetailPage = () => {
  const { listingId } = useParams<{ listingId: string }>();
  const navigate = useNavigate();

  const [listing, setListing] = useState<ListingResponse | null>(null);
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>("");

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

  // Handler functions
  const handleLike = () => {
    // TODO: Implement like functionality
    console.log("Like clicked");
  };

  const handleShare = () => {
    // TODO: Implement share functionality
    console.log("Share clicked");
  };

  const handleMore = () => {
    // TODO: Implement more options
    console.log("More clicked");
  };

  const handleViewProfile = () => {
    // TODO: Navigate to seller profile
    console.log("View profile clicked");
  };

  const handleAddToCart = () => {
    // TODO: Implement add to cart
    console.log("Add to cart clicked");
  };

  const handleMakeOffer = () => {
    // TODO: Implement make offer
    console.log("Make offer clicked");
  };

  const handleSeeAllSellerItems = () => {
    // TODO: Navigate to seller's all items
    console.log("See all clicked");
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-white">
        <Navbar />
        <CategoryMenu categories={[]} />
      </div>
    );
  }

  if (error || !listing) {
    return (
      <div className="min-h-screen bg-white">
        <Navbar />
        <div className="max-w-2xl mx-auto px-4 py-16 text-center">
          <h2 className="text-2xl font-bold mb-4">Listing Not Found</h2>
          <p className="text-gray-600 mb-6">
            {error || "This listing doesn't exist"}
          </p>
          <button
            onClick={() => navigate("/home")}
            className="px-6 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700"
          >
            Back to Home
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      <CategoryMenu categories={categories} />

      <div className="max-w-7xl mx-auto px-8 py-6">
        {/* Breadcrumb */}
        <Breadcrumb
          categoryName={listing.category.name}
          listingTitle={listing.title}
          onHomeClick={() => navigate("/home")}
          onCategoryClick={() => navigate(-1)}
        />

        <div className="flex gap-24">
          {/* Left: Image Gallery (Sticky) */}
          <div className="w-full max-w-2xl flex-shrink-0">
            <div className="sticky top-4">
              <ImageGallery images={listing.images} title={listing.title} />

              <ActionButtons
                favoriteCount={listing.favoriteCount}
                onLike={handleLike}
                onShare={handleShare}
                onMore={handleMore}
              />

              <SellerCard
                seller={listing.seller}
                salesCount={listing.viewCount} // TODO: Change to actual sales count
                onViewProfile={handleViewProfile}
              />
            </div>
          </div>

          {/* Right: Details (Scrollable) */}
          <div className="flex-1 min-w-0">
            <ProductInfo
              title={listing.title}
              price={listing.price}
              onAddToCart={handleAddToCart}
              onMakeOffer={handleMakeOffer}
            />

            <ProductStats
              viewCount={listing.viewCount}
              favoriteCount={listing.favoriteCount}
            />

            <LocationMap
              latitude={listing.latitude}
              longitude={listing.longitude}
              title={listing.title}
            />

            <ProductDetails
              condition={listing.condition}
              category={listing.category}
              createdAt={listing.createdAt}
              onCategoryClick={() => navigate(-1)}
            />

            <ProductDescription description={listing.description} />

            {/* CTA */}
            <div className="border-t mt-8 pt-6">
              <div className="w-full px-0">
                <button className="w-full px-6 py-2 border border-primary text-primary rounded-lg font-medium hover:bg-primary/10">
                  Have a similar item? Sell yours
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* More from this seller */}
        <SellerListings onSeeAll={handleSeeAllSellerItems} />
      </div>
    </div>
  );
};

export { ListingDetailPage };
