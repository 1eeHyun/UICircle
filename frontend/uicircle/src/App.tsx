// src/App.tsx
import { RouterProvider } from "react-router-dom";
import { AppRouter } from "./router/index";
import "./index.css";
import { CategoryProvider } from "@/features/listings/context/CategoryContext";

function App() {
  return (
    <CategoryProvider>
      <RouterProvider router={AppRouter} />
    </CategoryProvider>
  );
}

export default App;
