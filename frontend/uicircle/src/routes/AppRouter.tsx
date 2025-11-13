import { createBrowserRouter } from "react-router-dom";
import { MainLayout } from "../layout/MainLayout";
import { LoginPage } from "../features/auth/pages/LoginPage";
import { SignUpPage } from "../features/auth/pages/SignUpPage";


const AppRouter = createBrowserRouter([
    {
        path: "/",
        element: <MainLayout/>,
        
    },
    {
        path: "/login",
        element: <LoginPage/>,
        
    },
    {
        path: "/sign-up",
        element: <SignUpPage/>,
        
    },
])

export {AppRouter}