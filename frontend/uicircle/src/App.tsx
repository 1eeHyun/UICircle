import { RouterProvider } from "react-router-dom";
import { AppRouter } from "./routes/AppRouter";
import './index.css'

function App() {
  return <RouterProvider router={AppRouter} />;
}

export default App;
