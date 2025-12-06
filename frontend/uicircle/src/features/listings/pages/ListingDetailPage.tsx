// src/pages/ListingDetailPage.tsx
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "@/components/Navbar";
import CategoryMenu from "@/components/CategoryMenu";
import {
  ListingResponse,
  getListing,
  toggleFavorite,
  deleteListing,
} from "../services/ListingService";
import { getMyProfile } from "@/features/profile/services/ProfileService";

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

import OfferModal from "@/features/transaction/components/OfferModal";
import { useCategories } from "@/features/listings/context/CategoryContext";

const ListingDetailPage = () => {
  const { listingId } = useParams<{ listingId: string }>();
  const navigate = useNavigate();

  const [listing, setListing] = useState<ListingResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>("");
  const [isOwner, setIsOwner] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const { categories } = useCategories();

  // Offer modal state
  const [isOfferModalOpen, setIsOfferModalOpen] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const listingData = await getListing(listingId!);
        setListing(listingData);

        // Check if current user is the owner
        try {
          const myProfile = await getMyProfile();
          setIsOwner(myProfile.publicId === listingData.sellerProfile.publicId);
        } catch {
          // User not logged in or failed to get profile
          setIsOwner(false);
        }
      } catch (err: any) {
        setError(err?.response?.data?.message || "Failed to load listing");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [listingId]);

  const handleLike = async () => {
    if (!listing) return;

    const prevIsFavorited = listing.isFavorited;
    const prevFavoriteCount = listing.favoriteCount;

    const nextIsFavorited = !prevIsFavorited;
    const nextFavoriteCount = prevFavoriteCount + (nextIsFavorited ? 1 : -1);

    setListing({
      ...listing,
      isFavorited: nextIsFavorited,
      favoriteCount: nextFavoriteCount,
    });

    try {
      await toggleFavorite(listing.publicId);
    } catch (error) {
      console.error("Failed to toggle favorite:", error);

      setListing({
        ...listing,
        isFavorited: prevIsFavorited,
        favoriteCount: prevFavoriteCount,
      });
    }
  };

  const handleShare = () => {
    console.log("Share clicked");
  };

  const handleMore = () => {
    console.log("More clicked");
  };

  const handleViewProfile = () => {
    navigate(`/profile/${listing?.sellerProfile.publicId}`);
  };

  const handleMakeOffer = () => {
    setIsOfferModalOpen(true);
  };

  const handleSeeAllSellerItems = () => {
    if (!listing) return;
    navigate(`/profile/${listing.sellerProfile.publicId}?tab=listings`);
  };

  const handleOfferCreated = () => {
    console.log("Offer created successfully");
    // TODO: refetch / toast
  };

  const handleSellSimilar = () => {
    navigate("/listing/create");
  };

  const handleEdit = () => {
    navigate(`/listings/${listingId}/edit`);
  };

  const handleDelete = async () => {
    if (!listing) return;

    const confirmed = window.confirm(
      "Are you sure you want to delete this listing? This action cannot be undone."
    );

    if (!confirmed) return;

    try {
      setIsDeleting(true);
      await deleteListing(listing.publicId);
      // Navigate to home after successful deletion
      navigate("/home");
    } catch (err: any) {
      const errorMessage =
        err?.response?.data?.message || "Failed to delete listing. Please try again.";
      alert(errorMessage);
    } finally {
      setIsDeleting(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-white">
        <Navbar />
        <CategoryMenu categories={categories} />
      </div>
    );
  }

  if (error || !listing) {
    return (
      <div className="min-h-screen bg-white">
        <Navbar />
        <CategoryMenu categories={categories} />
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

        <div className="mt-4 flex flex-col lg:flex-row gap-8 lg:gap-16">
          {/* Left: Image / Seller  */}
          <div className="w-full lg:basis-[55%] lg:max-w-3xl lg:flex-shrink-0">
            <div className="sticky top-4">
              <ImageGallery images={listing.images} title={listing.title} />

              <ActionButtons
                favoriteCount={listing.favoriteCount}
                isFavorited={listing.isFavorited}
                onLike={handleLike}
                onShare={handleShare}
                onMore={handleMore}
              />

              <SellerCard
                seller={{
                  username:
                    listing.sellerProfile?.displayName ??
                    listing.sellerProfile?.publicId ??
                    "Unknown seller",
                  avatarUrl: listing.sellerProfile?.avatarUrl,
                }}
                salesCount={listing.sellerProfile?.soldCount ?? 0}
                onViewProfile={handleViewProfile}
              />
            </div>
          </div>

          {/* Right: Details (Scrollable) */}
          <div className="flex-1 min-w-0">
            <ProductInfo
              title={listing.title}
              price={listing.price}
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

            {/* Owner Actions */}
            {isOwner && (
              <div className="border-t mt-8 pt-6">
                <div className="flex gap-4">
                  <button
                    onClick={handleEdit}
                    className="flex-1 px-6 py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition"
                  >
                    Edit Listing
                  </button>
                  <button
                    onClick={handleDelete}
                    disabled={isDeleting}
                    className="flex-1 px-6 py-2 bg-red-600 text-white rounded-lg font-medium hover:bg-red-700 transition disabled:bg-red-400 disabled:cursor-not-allowed"
                  >
                    {isDeleting ? "Deleting..." : "Delete Listing"}
                  </button>
                </div>
              </div>
            )}

            {/* CTA */}
            <div className="border-t mt-8 pt-6">
              <div className="w-full px-0">
                <button
                  onClick={handleSellSimilar}
                  className="w-full px-6 py-2 border border-primary text-primary rounded-lg font-medium hover:bg-primary/10"
                >
                  Have a similar item? Sell yours
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* More from this seller */}
        <SellerListings
          sellerPublicId={listing.sellerProfile?.publicId}
          currentListingPublicId={listing.publicId}
          onSeeAll={handleSeeAllSellerItems}
        />
      </div>

      {/* Offer Modal at the bottom */}
      <OfferModal
        isOpen={isOfferModalOpen}
        onClose={() => setIsOfferModalOpen(false)}
        listingPrice={listing.price}
        listingTitle={listing.title}
        listingPublicId={listing.publicId}
        onOfferCreated={handleOfferCreated}
      />
    </div>
  );
};

export { ListingDetailPage };
