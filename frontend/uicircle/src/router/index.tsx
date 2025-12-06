import { createBrowserRouter } from "react-router-dom"; 
import { LoginPage } from "../features/auth/pages/LoginPage";
import { SignUpPage } from "../features/auth/pages/SignUpPage";
import { HomePage } from "../features/home/pages/HomePage";
import { CategoryPage } from "../features/listings/pages/CategoryPage";
import { SearchPage } from "../features/search/pages/SearchPage";
import ProtectedRoute from "../components/ProtectedRoute";
import { CreateListingPage } from "@/features/listings/pages/CreateListingPage";
import { ListingDetailPage } from "@/features/listings/pages/ListingDetailPage";
import { EditListingPage } from "@/features/listings/pages/EditListingPage";
import VerifyEmailPage from "@/pages/VerifyEmailPage";
import VerifyEmailPendingPage from "@/features/auth/pages/VerifyEmailPendingPage";
import ProfilePage from "@/features/profile/pages/ProfilePage";
import OffersPage from "@/features/offers/pages/OffersPage";
import MessagesPage from "@/features/messages/pages/MessagesPage";

const AppRouter = createBrowserRouter([
  { path: "/", element: <LoginPage /> },
  { path: "/signup", element: <SignUpPage /> },
  { path: "/verify-email/pending", element: <VerifyEmailPendingPage /> },
  { path: "/verify-email", element: <VerifyEmailPage /> },

  {
    path: "/home",
    element: (
      <ProtectedRoute>
        <HomePage />
      </ProtectedRoute>
    ),
  },
  {
    path: "/profile",
    element: (
      <ProtectedRoute>
        <ProfilePage />
      </ProtectedRoute>
    ),
  },
  {
    path: "/profile/:publicId",
    element: (
      <ProtectedRoute>
        <ProfilePage />
      </ProtectedRoute>
    ),
  },
  {
    path: "/category/:categorySlug",
    element: (
      <ProtectedRoute>
        <CategoryPage />
      </ProtectedRoute>
    ),
  },

  {
    path: "/listing/create",
    element: (
      <ProtectedRoute>
        <CreateListingPage />
      </ProtectedRoute>
    ),
  },

  {
    path: "/listings/:listingId",
    element: (
      <ProtectedRoute>
        <ListingDetailPage />
      </ProtectedRoute>
    ),
  },
  {
    path: "/listings/:listingId/edit",
    element: (
      <ProtectedRoute>
        <EditListingPage />
      </ProtectedRoute>
    ),
  },

  {
    path: "/search",
    element: (
      <ProtectedRoute>
        <SearchPage />
      </ProtectedRoute>
    ),
  },
  {
    path: "/offers",
    element: (
      <ProtectedRoute>
        <OffersPage />
      </ProtectedRoute>
    )
  },
  {
    path: "/messages",
    element: (
      <ProtectedRoute>
        <MessagesPage />
      </ProtectedRoute>
    )
  },
  {
    path: "/chat/:conversationId",
    element: (
      <ProtectedRoute>
        <MessagesPage />
      </ProtectedRoute>
    )
  },
  {
    path: "/chat",
    element: (
      <ProtectedRoute>
        <MessagesPage />
      </ProtectedRoute>
    )
  },
]);

export { AppRouter };
