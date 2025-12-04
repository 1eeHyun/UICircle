// src/pages/VerifyEmailPendingPage.tsx
import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import { resendVerificationEmail } from "@/features/auth/services/AuthService";

interface LocationState {
  email?: string;
}

const VerifyEmailPendingPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const state = (location.state || {}) as LocationState;

  const [email] = useState(state.email || "");
  const [resendLoading, setResendLoading] = useState(false);
  const [resendMessage, setResendMessage] = useState<string | null>(null);
  const [resendError, setResendError] = useState<string | null>(null);

  const handleResend = async () => {
    if (!email) {
      setResendError("Email information is missing. Please sign up again.");
      return;
    }

    try {
      setResendLoading(true);
      setResendError(null);
      setResendMessage(null);

      await resendVerificationEmail(email);

      setResendMessage(
        "We’ve sent a new verification email. Please check your inbox and also your Spam / Junk / Promotions folder."
      );
    } catch (err: any) {
      const msg =
        err?.response?.data?.message ||
        "Failed to resend verification email. Please try again.";
      setResendError(msg);
    } finally {
      setResendLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-surface-light to-gray-100 flex items-center justify-center px-4">
      <div className="w-full max-w-xl bg-background-light rounded-2xl shadow-2xl p-8 border border-border-light">
        <h1 className="text-2xl md:text-3xl font-bold text-gray-900 mb-3 text-center">
          Check your email
        </h1>

        <p className="text-gray-600 text-sm md:text-base mb-2 text-center">
          We’ve sent a verification link to:
        </p>

        <p className="text-primary font-semibold text-center mb-5 break-all">
          {email || "your email address"}
        </p>

        <p className="text-gray-600 text-sm md:text-base mb-6 text-center leading-relaxed">
          Please open the email and click the verification link to activate your account.
          If you don&apos;t see it in your inbox, make sure to check your{" "}
          <span className="font-semibold">Spam</span>,{" "}
          <span className="font-semibold">Junk</span>, or{" "}
          <span className="font-semibold">Promotions</span> folder as well.
        </p>

        <div className="flex flex-col items-center gap-3">
          <button
            type="button"
            onClick={handleResend}
            disabled={resendLoading}
            className="px-6 py-2.5 bg-primary text-background-light rounded-xl font-semibold shadow-md hover:bg-primary-dark disabled:opacity-50 disabled:cursor-not-allowed transition"
          >
            {resendLoading ? "Resending..." : "Resend verification email"}
          </button>

          {resendMessage && (
            <p className="text-sm text-green-600 text-center">{resendMessage}</p>
          )}
          {resendError && (
            <p className="text-sm text-red-600 text-center">{resendError}</p>
          )}
        </div>

        <div className="mt-8 border-t border-border-light pt-4 text-center text-sm text-gray-600">
          <p>Already verified your email?</p>
          <button
            type="button"
            onClick={() => navigate("/")}
            className="mt-2 text-primary hover:text-primary-dark font-semibold hover:underline"
          >
            Go to Login
          </button>
        </div>
      </div>
    </div>
  );
};

export default VerifyEmailPendingPage;
