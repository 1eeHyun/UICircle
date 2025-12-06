import { createBrowserRouter } from "react-router-dom";
import { LoginPage } from "../features/auth/pages/LoginPage";
import { SignUpPage } from "../features/auth/pages/SignUpPage";
import { HomePage } from "../features/home/pages/HomePage";
import { CategoryPage } from "../features/listings/pages/CategoryPage";
import { SearchPage } from "../features/search/pages/SearchPage";
import ProtectedRoute from "../components/ProtectedRoute";
import { CreateListingPage } from "@/features/listings/pages/CreateListingPage";
import { ListingDetailPage } from "@/features/listings/pages/ListingDetailPage";
import VerifyEmailPage from "@/pages/VerifyEmailPage";
import VerifyEmailPendingPage from "@/features/auth/pages/VerifyEmailPendingPage";
import OffersPage from "@/features/offers/pages/OffersPage";

const AppRouter = createBrowserRouter([
  {
    path: "/",
    element: <LoginPage />,
  },
  {
    path: "/signup",
    element: <SignUpPage />,
  },
  {
    path: "/verify-email/pending",
    element: <VerifyEmailPendingPage />,
  },
  {
    path: "/verify-email",
    element: <VerifyEmailPage />,
  },
  {
    path: "/home",
    element: (
      <ProtectedRoute>
        <HomePage />
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
      <CreateListingPage/>
    </ProtectedRoute>
    ),
  },
  {
    path: "/listings/:listingId",
    element: (
    <ProtectedRoute>
      <ListingDetailPage/>
    </ProtectedRoute>
    ),
  },
  {
    path: "/search",
    element: (
      <ProtectedRoute>
        <SearchPage />
      </ProtectedRoute>
    )
  },
  {
    path: "/offers",
    element: (
      <ProtectedRoute>
        <OffersPage />
      </ProtectedRoute>
    )
  },
]);

export { AppRouter };