import { createBrowserRouter } from "react-router-dom";
import { LoginPage } from "../features/auth/pages/LoginPage";
import { SignUpPage } from "../features/auth/pages/SignUpPage";
import { HomePage } from "../features/home/pages/HomePage";
import { CategoryPage } from "../features/listings/pages/CategoryPage";
import ProtectedRoute from "../components/ProtectedRoute";
import { CreateListingPage } from "@/features/listings/pages/CreateListingPage";
import { ListingDetailPage } from "@/features/listings/pages/ListingDetailPage";

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
<<<<<<< HEAD
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
=======
>>>>>>> c653aa9 (refactor: moved AppRouter to index.ts)
]);

export { AppRouter };