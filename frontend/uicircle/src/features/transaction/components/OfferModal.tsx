// src/features/transaction/components/OfferModal.tsx
import React, { useEffect, useState } from "react";
import { apiRequest } from "@/api/axios";

interface OfferModalProps {
  isOpen: boolean;
  onClose: () => void;
  listingPrice: number;
  listingTitle: string;
  listingPublicId: string;
  onOfferCreated?: () => void;
}

const OfferModal: React.FC<OfferModalProps> = ({
  isOpen,
  onClose,
  listingPrice,
  listingTitle,
  listingPublicId,
  onOfferCreated,
}) => {
  const [amountInput, setAmountInput] = useState<string>("");
  const [message, setMessage] = useState<string>("");
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setAmountInput("");
      setMessage("");
      setError(null);
      setIsSubmitting(false);
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const listingPriceNum = Number(listingPrice);

  const suggestedAmounts = [
    Math.round(listingPriceNum * 0.5),
    Math.round(listingPriceNum * 0.75),
    listingPriceNum,
  ].filter((v, idx, arr) => arr.indexOf(v) === idx && v > 0);

  const validateAmount = (raw: string): number | null => {
    if (!raw) {
      setError("Please enter an amount.");
      return null;
    }

    const value = Number(raw);

    if (Number.isNaN(value)) {
      setError("Please enter a valid number.");
      return null;
    }

    if (value < 0) {
      setError("Offer amount must not be negative.");
      return null;
    }

    if (value > listingPriceNum) {
      setError("Offer amount cannot exceed the listing price.");
      return null;
    }

    setError(null);
    return value;
  };

  const handleAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setAmountInput(value);
    validateAmount(value);
  };

  const handleSuggestedClick = (val: number) => {
    const str = val.toString();
    setAmountInput(str);
    validateAmount(str);
  };

  const handleSubmit = async () => {
    const validAmount = validateAmount(amountInput);
    if (validAmount === null) return;

    setIsSubmitting(true);
    try {
      const res = await apiRequest({
        method: "POST",
        url: `/listings/${listingPublicId}/offers`,
        data: {
          amount: validAmount,
          message: message.trim() || null,
        },
      });

      console.log("Offer created response:", res); // debug

      onOfferCreated?.();
      onClose();
    } catch (err: any) {
      console.error("Create offer failed:", err); // debug

      // Try to read backend error message
      const backendMessage =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        err?.message;

      setError(backendMessage || "Something went wrong while creating the offer.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40">
      <div className="w-full max-w-md rounded-lg bg-white p-6 shadow-lg">
        <h2 className="text-lg font-semibold mb-2">Make an offer</h2>
        <p className="text-sm text-gray-600 mb-1">{listingTitle}</p>
        <p className="text-sm text-gray-600 mb-4">
          Listing price:{" "}
          <span className="font-semibold">${listingPriceNum.toFixed(2)}</span>
        </p>

        {/* Suggested amounts */}
        <div className="flex flex-wrap gap-2 mb-4">
          {suggestedAmounts.map((amt) => (
            <button
              key={amt}
              type="button"
              onClick={() => handleSuggestedClick(amt)}
              className="rounded-full border px-3 py-1 text-sm hover:bg-gray-100"
            >
              ${amt.toFixed(2)}
            </button>
          ))}
        </div>

        {/* Amount input */}
        <div className="mb-4">
          <label className="block text-sm font-medium mb-1">
            Offer amount
          </label>
          <div className="flex items-center gap-2">
            <span className="text-gray-500">$</span>
            <input
              type="number"
              min={0.01}
              step={0.01}
              value={amountInput}
              onChange={handleAmountChange}
              className="flex-1 rounded-md border px-2 py-1 text-sm"
              placeholder="e.g. 25.00"
            />
          </div>
        </div>

        {/* Message (optional) */}
        <div className="mb-4">
          <label className="block text-sm font-medium mb-1">
            Message (optional)
          </label>
          <textarea
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            maxLength={500}
            rows={3}
            className="w-full rounded-md border px-2 py-1 text-sm resize-none"
            placeholder="You can leave a short note to the seller (max 500 characters)."
          />
          <div className="text-right text-xs text-gray-400 mt-1">
            {message.length}/500
          </div>
        </div>

        {error && (
          <p className="mb-3 text-sm text-red-500">
            {error}
          </p>
        )}

        <div className="flex justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            className="rounded-md px-3 py-1.5 text-sm border hover:bg-gray-100"
            disabled={isSubmitting}
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={handleSubmit}
            className="rounded-md px-4 py-1.5 text-sm font-semibold bg-primary text-white hover:bg-primary-light disabled:opacity-60"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Submitting..." : "Send offer"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default OfferModal;
