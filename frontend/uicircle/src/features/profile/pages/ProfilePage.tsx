// src/features/profile/pages/ProfilePage.tsx

import { useEffect, useState, useRef } from "react";
import { useParams, useNavigate, useSearchParams } from "react-router-dom";
import Navbar from "@/components/Navbar";
import CategoryMenu from "@/components/CategoryMenu";
import ListingCard from "@/components/ListingCard";
import {
  getTopLevelCategories,
  CategoryResponse,
  ListingSummaryResponse,
  getSellerListingsBySeller,
} from "@/features/listings/services/ListingService";
import {
  getPublicProfile,
  getMyProfile,
  uploadAvatar,
  uploadBanner,
  updateProfile,
  ProfileResponse,
  UpdateProfileRequest,
} from "../services/ProfileService";

type TabType = "listings" | "sold" | "settings";

/**
 * ProfilePage
 * - /profile           -> show my profile (getMyProfile)
 * - /profile/:publicId -> show other's profile (getPublicProfile)
 * - Shows listings / sold items / settings (for own profile)
 */
const ProfilePage = () => {
  // publicId route param for viewing other user's profile
  const { publicId } = useParams<{ publicId: string }>();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  // Profile data state
  const [profile, setProfile] = useState<ProfileResponse | null>(null);
  const [myProfile, setMyProfile] = useState<ProfileResponse | null>(null);

  // Category / listing state
  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [listings, setListings] = useState<ListingSummaryResponse[]>([]);

  // UI state
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>("");

  // Active tab (listings / sold / settings)
  const [activeTab, setActiveTab] = useState<TabType>(
    (searchParams.get("tab") as TabType) || "listings"
  );

  // Edit form state for settings tab
  const [editForm, setEditForm] = useState<UpdateProfileRequest>({});

  // File input refs for avatar and banner
  const avatarInputRef = useRef<HTMLInputElement>(null);
  const bannerInputRef = useRef<HTMLInputElement>(null);

  // Check if currently viewing my own profile
  const isOwnProfile =
    !!myProfile && !!profile && myProfile.publicId === profile.publicId;

  /**
   * Fetch categories, my profile, target profile, and listing data
   * - Runs whenever publicId or activeTab changes
   */
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError("");

        // 1) Fetch categories (for CategoryMenu)
        const categoriesData = await getTopLevelCategories();
        setCategories(categoriesData);

        // 2) Fetch my profile to know if this is my own profile
        try {
          const myProfileData = await getMyProfile();
          setMyProfile(myProfileData);
        } catch {
          // If not logged in or request fails, simply treat as no myProfile
          setMyProfile(null);
        }

        // 3) Fetch target profile (self or other)
        let profileData: ProfileResponse;
        if (publicId) {
          // Viewing other user's profile
          profileData = await getPublicProfile(publicId);
        } else {
          // Viewing own profile (no publicId in route)
          profileData = await getMyProfile();
        }
        setProfile(profileData);

        // 4) Fetch listings for this profile (ACTIVE or SOLD)
        if (profileData.publicId) {
          try {
            const listingsData = await getSellerListingsBySeller(
              profileData.publicId,
              0,
              20,
              activeTab === "sold" ? "SOLD" : "ACTIVE"
            );
            setListings(listingsData.content);
          } catch (e) {
            console.error("Failed to fetch listings:", e);
          }
        }
      } catch (err: any) {
        setError(err?.response?.data?.message || "Failed to load profile");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [publicId, activeTab]);

  /**
   * Handle avatar image upload
   */
  const handleAvatarUpload = async (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const file = e.target.files?.[0];
    if (!file || !isOwnProfile) return;

    try {
      const updated = await uploadAvatar(file);
      setProfile(updated);
    } catch (err: any) {
      console.error("Avatar upload failed:", err);
      alert(err?.response?.data?.message || "Failed to upload avatar");
    }
  };

  /**
   * Handle banner image upload
   */
  const handleBannerUpload = async (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const file = e.target.files?.[0];
    if (!file || !isOwnProfile) return;

    try {
      const updated = await uploadBanner(file);
      setProfile(updated);
    } catch (err: any) {
      console.error("Banner upload failed:", err);
      alert(err?.response?.data?.message || "Failed to upload banner");
    }
  };

  /**
   * Save profile changes from settings tab
   */
  const handleSaveProfile = async () => {
    if (!isOwnProfile) return;

    try {
      const updated = await updateProfile(editForm);
      setProfile(updated);
      setActiveTab("listings");
    } catch (err: any) {
      console.error("Profile update failed:", err);
      alert(err?.response?.data?.message || "Failed to update profile");
    }
  };

  /**
   * Loading state (initial fetch)
   */
  if (loading) {
    return (
      <div className="min-h-screen bg-surface-light">
        <Navbar />
        <div className="flex justify-center items-center py-20">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary" />
        </div>
      </div>
    );
  }

  /**
   * Error state or missing profile
   */
  if (error || !profile) {
    return (
      <div className="min-h-screen bg-surface-light">
        <Navbar />
        <div className="max-w-2xl mx-auto px-4 py-16 text-center">
          <h2 className="text-2xl font-bold mb-4">Profile Not Found</h2>
          <p className="text-gray-600 mb-6">
            {error || "This profile doesn't exist"}
          </p>
          <button
            onClick={() => navigate("/home")}
            className="px-6 py-3 bg-primary text-white rounded-lg hover:bg-primary-dark"
          >
            Back to Home
          </button>
        </div>
      </div>
    );
  }

  /**
   * Normal render
   */
  return (
    <div className="min-h-screen bg-surface-light">
      {/* Top navbar */}
      <Navbar />

      {/* Category bar */}
      <CategoryMenu categories={categories} />

      {/* Banner area */}
      <div className="relative h-48 md:h-64 bg-gradient-to-r from-primary/20 to-primary/40 overflow-hidden">
        {profile.bannerUrl ? (
          <img
            src={profile.bannerUrl}
            alt="Profile banner"
            className="w-full h-full object-cover"
          />
        ) : (
          <div className="w-full h-full bg-gradient-to-r from-primary/30 via-primary/20 to-primary/40" />
        )}

        {/* Banner upload button (only for own profile) */}
        {isOwnProfile && (
          <>
            <button
              onClick={() => bannerInputRef.current?.click()}
              className="absolute bottom-4 right-4 px-4 py-2 bg-black/50 text-white rounded-lg text-sm hover:bg-black/70 transition"
            >
              Change Banner
            </button>
            <input
              ref={bannerInputRef}
              type="file"
              accept="image/*"
              className="hidden"
              onChange={handleBannerUpload}
            />
          </>
        )}
      </div>

      {/* Main profile content */}
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Profile header section */}
        <div className="relative -mt-16 mb-6 flex flex-col sm:flex-row sm:items-end gap-4">
          {/* Avatar section */}
          <div className="relative">
            <div className="w-32 h-32 rounded-full border-4 border-white bg-surface-light overflow-hidden shadow-lg">
              {profile.avatarUrl ? (
                <img
                  src={profile.avatarUrl}
                  alt={profile.displayName}
                  className="w-full h-full object-cover"
                />
              ) : (
                <div className="w-full h-full bg-primary/10 flex items-center justify-center">
                  <span className="text-4xl font-bold text-primary">
                    {profile.displayName?.charAt(0).toUpperCase() || "U"}
                  </span>
                </div>
              )}
            </div>

            {/* Avatar upload button (only for own profile) */}
            {isOwnProfile && (
              <>
                <button
                  onClick={() => avatarInputRef.current?.click()}
                  className="absolute bottom-0 right-0 w-8 h-8 bg-primary text-white rounded-full flex items-center justify-center hover:bg-primary-dark transition"
                >
                  <svg
                    className="w-4 h-4"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"
                    />
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"
                    />
                  </svg>
                </button>
                <input
                  ref={avatarInputRef}
                  type="file"
                  accept="image/*"
                  className="hidden"
                  onChange={handleAvatarUpload}
                />
              </>
            )}
          </div>

          {/* Profile basic info */}
          <div className="flex-1 pt-4 sm:pt-0 sm:pb-2">
            <h1 className="text-2xl font-bold text-gray-900">
              {profile.displayName}
            </h1>
            <p className="text-gray-500">@{profile.username}</p>
            {profile.major && (
              <p className="text-sm text-gray-600 mt-1">{profile.major}</p>
            )}
            {profile.bio && (
              <p className="text-gray-700 mt-2 max-w-xl">{profile.bio}</p>
            )}
          </div>

          {/* Action buttons (edit profile) */}
          <div className="flex gap-2 sm:self-center">
            {isOwnProfile && (
              <button
                onClick={() => {
                  setEditForm({
                    displayName: profile.displayName,
                    bio: profile.bio || "",
                    major: profile.major || "",
                  });
                  setActiveTab("settings");
                }}
                className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-100 transition"
              >
                Edit Profile
              </button>
            )}
          </div>
        </div>

        {/* Stats section */}
        <div className="flex gap-6 mb-6 border-b border-border-light pb-4">
          <div className="text-center">
            <p className="text-xl font-bold text-gray-900">
              {profile.soldCount}
            </p>
            <p className="text-sm text-gray-500">Sold</p>
          </div>
          <div className="text-center">
            <p className="text-xl font-bold text-gray-900">
              {profile.buyCount}
            </p>
            <p className="text-sm text-gray-500">Bought</p>
          </div>
        </div>

        {/* Tabs (Listings / Sold / Settings) */}
        <div className="flex gap-4 mb-6 border-b border-border-light">
          <button
            onClick={() => setActiveTab("listings")}
            className={`pb-3 px-1 border-b-2 transition ${
              activeTab === "listings"
                ? "border-primary text-primary font-medium"
                : "border-transparent text-gray-500 hover:text-gray-700"
            }`}
          >
            Listings
          </button>
          <button
            onClick={() => setActiveTab("sold")}
            className={`pb-3 px-1 border-b-2 transition ${
              activeTab === "sold"
                ? "border-primary text-primary font-medium"
                : "border-transparent text-gray-500 hover:text-gray-700"
            }`}
          >
            Sold
          </button>
          {isOwnProfile && (
            <button
              onClick={() => setActiveTab("settings")}
              className={`pb-3 px-1 border-b-2 transition ${
                activeTab === "settings"
                  ? "border-primary text-primary font-medium"
                  : "border-transparent text-gray-500 hover:text-gray-700"
              }`}
            >
              Settings
            </button>
          )}
        </div>

        {/* Tab content */}
        {activeTab === "settings" && isOwnProfile ? (
          // Settings tab (edit profile)
          <div className="bg-white rounded-lg p-6 shadow-sm mb-8">
            <h2 className="text-lg font-semibold mb-4">Edit Profile</h2>

            <div className="space-y-4">
              {/* Display name field */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Display Name
                </label>
                <input
                  type="text"
                  value={editForm.displayName || ""}
                  onChange={(e) =>
                    setEditForm({
                      ...editForm,
                      displayName: e.target.value,
                    })
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary/30 focus:border-primary"
                />
              </div>

              {/* Major field */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Major
                </label>
                <input
                  type="text"
                  value={editForm.major || ""}
                  onChange={(e) =>
                    setEditForm({
                      ...editForm,
                      major: e.target.value,
                    })
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary/30 focus:border-primary"
                  placeholder="e.g. Computer Science"
                />
              </div>

              {/* Bio field */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Bio
                </label>
                <textarea
                  value={editForm.bio || ""}
                  onChange={(e) =>
                    setEditForm({
                      ...editForm,
                      bio: e.target.value,
                    })
                  }
                  rows={3}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary/30 focus:border-primary"
                  placeholder="Tell us about yourself..."
                />
              </div>

              {/* Action buttons */}
              <div className="flex gap-2">
                <button
                  onClick={handleSaveProfile}
                  className="px-6 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition"
                >
                  Save Changes
                </button>
                <button
                  onClick={() => {
                    setActiveTab("listings");
                  }}
                  className="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-100 transition"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        ) : (
          // Listings / Sold tab content
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4 mb-8">
            {listings.length === 0 ? (
              <div className="col-span-full text-center py-16 bg-white rounded-lg shadow-sm">
                <p className="text-gray-500">
                  {activeTab === "sold"
                    ? "No sold items yet"
                    : "No listings yet"}
                </p>
              </div>
            ) : (
              listings.map((listing) => (
                <ListingCard key={listing.publicId} listing={listing} />
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfilePage;
