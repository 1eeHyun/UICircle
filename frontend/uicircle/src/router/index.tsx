import { createBrowserRouter } from "react-router-dom";
import { LoginPage } from "../features/auth/pages/LoginPage";
import { SignUpPage } from "../features/auth/pages/SignUpPage";
import { HomePage } from "../features/home/pages/HomePage";
import { CategoryPage } from "../features/listings/pages/CategoryPage";
import { SearchPage } from "../features/search/pages/SearchPage";
import ProtectedRoute from "../components/ProtectedRoute";

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
    path: "/search",
    element: (
      <ProtectedRoute>
        <SearchPage />
      </ProtectedRoute>
    ),
  },
]);

export { AppRouter };