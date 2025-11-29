// src/pages/VerifyEmailPage.tsx
import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import instance from "@/api/axios";

const VerifyEmailPage = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");
  const navigate = useNavigate();
  const [status, setStatus] = useState<"loading" | "success" | "error">("loading");

  useEffect(() => {
    if (!token) {
      setStatus("error");
      return;
    }

    const verifyEmail = async () => {
      try {
        // POST /api/auth/verify-email?token=...
        await instance.post("auth/verify-email", null, {
          params: { token },
        });

        setStatus("success");

        // Redirect to login page after 2 seconds
        setTimeout(() => {
          navigate("/");
        }, 2000);
      } catch (error) {
        console.error("Email verification failed:", error);
        setStatus("error");
      }
    };

    verifyEmail();
  }, [token, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="max-w-md w-full bg-white rounded-xl shadow p-8 text-center">
        {status === "loading" && (
          <>
            <h1 className="text-xl font-semibold mb-2">Verifying your email...</h1>
            <p className="text-gray-600 text-sm">
              Please wait a moment while we confirm your account.
            </p>
          </>
        )}

        {status === "success" && (
          <>
            <h1 className="text-xl font-semibold mb-2 text-green-600">
              Email verified
            </h1>
            <p className="text-gray-600 text-sm mb-4">
              Your email has been verified. Redirecting to login page...
            </p>
            <button
              type="button"
              className="mt-2 px-4 py-2 rounded-md bg-red-600 text-white text-sm font-medium"
              onClick={() => navigate("/")}
            >
              Go to Login
            </button>
          </>
        )}

        {status === "error" && (
          <>
            <h1 className="text-xl font-semibold mb-2 text-red-600">
              Verification failed
            </h1>
            <p className="text-gray-600 text-sm mb-4">
              The verification link is invalid or has expired.
            </p>
            <button
              type="button"
              className="mt-2 px-4 py-2 rounded-md bg-red-600 text-white text-sm font-medium"
              onClick={() => navigate("/")}
            >
              Back to Login
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default VerifyEmailPage;
