import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "@/components/Navbar";
import {
  getReceivedOffers,
  getSentOffers,
  acceptOffer,
  rejectOffer,
  cancelOffer,
  PriceOfferResponse,
} from "../services/OfferService";

type TabType = "received" | "sent";

const statusColors: Record<string, string> = {
  PENDING: "bg-yellow-100 text-yellow-800",
  ACCEPTED: "bg-green-100 text-green-800",
  REJECTED: "bg-red-100 text-red-800",
  CANCELLED: "bg-gray-100 text-gray-600",
};

export default function OffersPage() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<TabType>("received");
  const [receivedOffers, setReceivedOffers] = useState<PriceOfferResponse[]>([]);
  const [sentOffers, setSentOffers] = useState<PriceOfferResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState<string | null>(null);

  useEffect(() => {
    fetchOffers();
  }, []);

  const fetchOffers = async () => {
    try {
      setLoading(true);
      const [received, sent] = await Promise.all([
        getReceivedOffers(),
        getSentOffers(),
      ]);
      setReceivedOffers(received);
      setSentOffers(sent);
    } catch (err) {
      console.error("Failed to fetch offers:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleAccept = async (offerPublicId: string) => {
    if (actionLoading) return;
    setActionLoading(offerPublicId);
    try {
      await acceptOffer(offerPublicId);
      await fetchOffers();
    } catch (err: any) {
      alert(err?.response?.data?.message || "Failed to accept offer");
    } finally {
      setActionLoading(null);
    }
  };

  const handleReject = async (offerPublicId: string) => {
    if (actionLoading) return;
    setActionLoading(offerPublicId);
    try {
      await rejectOffer(offerPublicId);
      await fetchOffers();
    } catch (err: any) {
      alert(err?.response?.data?.message || "Failed to reject offer");
    } finally {
      setActionLoading(null);
    }
  };

  const handleCancel = async (offerPublicId: string) => {
    if (actionLoading) return;
    if (!confirm("Are you sure you want to cancel this offer?")) return;
    setActionLoading(offerPublicId);
    try {
      await cancelOffer(offerPublicId);
      await fetchOffers();
    } catch (err: any) {
      alert(err?.response?.data?.message || "Failed to cancel offer");
    } finally {
      setActionLoading(null);
    }
  };

  const currentOffers = activeTab === "received" ? receivedOffers : sentOffers;
  const pendingCount = receivedOffers.filter((o) => o.status === "PENDING").length;

  return (
    <div className="min-h-screen bg-surface-light">
      <Navbar />

      <div className="max-w-4xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="flex items-center gap-3 mb-6">
          <button
            onClick={() => navigate(-1)}
            className="p-2 hover:bg-gray-200 rounded-lg transition-colors"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <h1 className="text-2xl font-bold text-gray-900">My Offers</h1>
        </div>

        {/* Tabs */}
        <div className="flex gap-1 bg-gray-200 p-1 rounded-lg w-fit mb-6">
          <button
            onClick={() => setActiveTab("received")}
            className={`px-4 py-2 text-sm font-medium rounded-md transition-colors ${
              activeTab === "received"
                ? "bg-white text-gray-900 shadow-sm"
                : "text-gray-600 hover:text-gray-900"
            }`}
          >
            Received
            {pendingCount > 0 && (
              <span className="ml-2 px-1.5 py-0.5 text-xs font-semibold bg-primary text-white rounded-full">
                {pendingCount}
              </span>
            )}
          </button>
          <button
            onClick={() => setActiveTab("sent")}
            className={`px-4 py-2 text-sm font-medium rounded-md transition-colors ${
              activeTab === "sent"
                ? "bg-white text-gray-900 shadow-sm"
                : "text-gray-600 hover:text-gray-900"
            }`}
          >
            Sent
          </button>
        </div>

        {/* Offers List */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200">
          {loading ? (
            <div className="p-8 text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
            </div>
          ) : currentOffers.length === 0 ? (
            <div className="p-8 text-center text-gray-500">
              {activeTab === "received"
                ? "No offers received yet"
                : "You haven't sent any offers yet"}
            </div>
          ) : (
            <ul className="divide-y divide-gray-100">
              {currentOffers.map((offer) => (
                <li key={offer.publicId} className="p-4 hover:bg-gray-50 transition-colors">
                  <div className="flex gap-4">
                    {/* Thumbnail */}
                    <div
                      className="w-20 h-20 rounded-lg bg-gray-200 flex-shrink-0 cursor-pointer overflow-hidden"
                      onClick={() => navigate(`/listings/${offer.listingPublicId}`)}
                    >
                      {offer.listingThumbnailUrl ? (
                        <img
                          src={offer.listingThumbnailUrl}
                          alt={offer.listingTitle}
                          className="w-full h-full object-cover"
                        />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-gray-400">
                          <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                          </svg>
                        </div>
                      )}
                    </div>

                    {/* Content */}
                    <div className="flex-1 min-w-0">
                      <div className="flex items-start justify-between gap-2">
                        <div>
                          <h3
                            className="font-medium text-gray-900 hover:text-primary cursor-pointer truncate"
                            onClick={() => navigate(`/listings/${offer.listingPublicId}`)}
                          >
                            {offer.listingTitle}
                          </h3>
                          <p className="text-sm text-gray-500 mt-0.5">
                            {activeTab === "received" ? (
                              <>From: <span className="font-medium">{offer.buyerDisplayName || offer.buyerUsername}</span></>
                            ) : (
                              <>To: <span className="font-medium">{offer.sellerUsername}</span></>
                            )}
                          </p>
                        </div>
                        <span className={`px-2 py-1 text-xs font-medium rounded-full ${statusColors[offer.status]}`}>
                          {offer.status}
                        </span>
                      </div>

                      <div className="mt-2 flex items-baseline gap-2">
                        <span className="text-lg font-bold text-primary">${offer.amount.toFixed(2)}</span>
                        <span className="text-sm text-gray-400">
                          (Listed: ${offer.listingPrice.toFixed(2)})
                        </span>
                      </div>

                      {offer.message && (
                        <p className="mt-2 text-sm text-gray-600 line-clamp-2">"{offer.message}"</p>
                      )}

                      <div className="mt-3 flex items-center gap-2">
                        {/* Actions for received pending offers (seller) */}
                        {activeTab === "received" && offer.status === "PENDING" && (
                          <>
                            <button
                              onClick={() => handleAccept(offer.publicId)}
                              disabled={actionLoading === offer.publicId}
                              className="px-3 py-1.5 text-sm font-medium bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50 transition-colors"
                            >
                              {actionLoading === offer.publicId ? "..." : "Accept"}
                            </button>
                            <button
                              onClick={() => handleReject(offer.publicId)}
                              disabled={actionLoading === offer.publicId}
                              className="px-3 py-1.5 text-sm font-medium bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:opacity-50 transition-colors"
                            >
                              Reject
                            </button>
                          </>
                        )}

                        {/* Actions for sent pending offers (buyer) */}
                        {activeTab === "sent" && offer.status === "PENDING" && (
                          <button
                            onClick={() => handleCancel(offer.publicId)}
                            disabled={actionLoading === offer.publicId}
                            className="px-3 py-1.5 text-sm font-medium border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 disabled:opacity-50 transition-colors"
                          >
                            Cancel Offer
                          </button>
                        )}

                        {/* Message button for accepted offers */}
                        {offer.status === "ACCEPTED" && (
                          <button
                            onClick={() => navigate("/messages")}
                            className="px-3 py-1.5 text-sm font-medium bg-primary text-white rounded-lg hover:bg-primary-dark transition-colors"
                          >
                            Message
                          </button>
                        )}

                        <span className="ml-auto text-xs text-gray-400">
                          {new Date(offer.createdAt).toLocaleDateString()}
                        </span>
                      </div>
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}

