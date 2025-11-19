import { createBrowserRouter } from "react-router-dom";
import { LoginPage } from "../features/auth/pages/LoginPage";
import { SignUpPage } from "../features/auth/pages/SignUpPage";
import { HomePage } from "../features/home/pages/HomePage";
import { CategoryPage } from "../features/listings/pages/CategoryPage";
import ProtectedRoute from "../components/ProtectedRoute";
import { CreateListingPage } from "@/features/listings/pages/CreateListingPage";

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
  {
  path: "/listing/create",
  element: (
  <ProtectedRoute>
     <CreateListingPage/>
  </ProtectedRoute>
  ),
  },
]);

export { AppRouter };