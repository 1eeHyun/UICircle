// src/features/notification/pages/NotificationPage.tsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useNotifications } from "@/features/notification/hooks/useNotifications";
import { NotificationList } from "../components/ui/NotificationList";
import { NotificationResponse } from "@/types/notification";

export default function NotificationPage() {
  const navigate = useNavigate();
  const [filterTab, setFilterTab] = useState<"all" | "unread">("all");

  const {
    notifications,
    unreadCount,
    hasMore,
    loading,
    loadMore,
    markAsRead,
    markAllAsRead,
    deleteNotification,
    deleteAllNotifications,
  } = useNotifications(0, 20);

  const handleItemClick = (notification: NotificationResponse) => {
    if (notification.linkUrl) {
      navigate(notification.linkUrl);
    }
  };

  const filteredNotifications =
    filterTab === "unread"
      ? notifications.filter((n) => !n.isRead)
      : notifications;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-6">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-3">
              <button
                onClick={() => navigate(-1)}
                className="p-2 hover:bg-gray-200 rounded-lg transition-colors"
                aria-label="Go back"
              >
                <svg
                  className="w-5 h-5 text-gray-600"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M15 19l-7-7 7-7"
                  />
                </svg>
              </button>
              <h1 className="text-3xl font-bold text-gray-900">Notifications</h1>
            </div>

            <div className="flex gap-2">
              {unreadCount > 0 && (
                <button
                  onClick={markAllAsRead}
                  className="px-4 py-2 text-sm text-blue-600 hover:text-blue-700 hover:bg-blue-50 rounded-lg font-medium transition-colors"
                >
                  Mark all as read
                </button>
              )}
              {notifications.length > 0 && (
                <button
                  onClick={() => {
                    if (
                      window.confirm(
                        "Are you sure you want to delete all notifications?"
                      )
                    ) {
                      deleteAllNotifications();
                    }
                  }}
                  className="px-4 py-2 text-sm text-red-600 hover:text-red-700 hover:bg-red-50 rounded-lg font-medium transition-colors"
                >
                  Clear all
                </button>
              )}
            </div>
          </div>

          {/* Filter Tabs */}
          <div className="flex gap-1 bg-gray-200 p-1 rounded-lg w-fit">
            <button
              onClick={() => setFilterTab("all")}
              className={`px-4 py-2 text-sm font-medium rounded-md transition-colors ${
                filterTab === "all"
                  ? "bg-white text-gray-900 shadow-sm"
                  : "text-gray-600 hover:text-gray-900"
              }`}
            >
              All
              {notifications.length > 0 && (
                <span className="ml-2 text-xs text-gray-500">
                  ({notifications.length})
                </span>
              )}
            </button>
            <button
              onClick={() => setFilterTab("unread")}
              className={`px-4 py-2 text-sm font-medium rounded-md transition-colors ${
                filterTab === "unread"
                  ? "bg-white text-gray-900 shadow-sm"
                  : "text-gray-600 hover:text-gray-900"
              }`}
            >
              Unread
              {unreadCount > 0 && (
                <span className="ml-2 px-1.5 py-0.5 text-xs font-semibold bg-blue-600 text-white rounded-full">
                  {unreadCount}
                </span>
              )}
            </button>
          </div>
        </div>

        {/* Notifications List */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
          <NotificationList
            notifications={filteredNotifications}
            loading={loading}
            hasMore={hasMore && filterTab === "all"}
            compact={false}
            onRead={markAsRead}
            onDelete={deleteNotification}
            onLoadMore={filterTab === "all" ? loadMore : undefined}
            onItemClick={handleItemClick}
            emptyMessage={
              filterTab === "unread"
                ? "No unread notifications"
                : "You're all caught up!"
            }
          />
        </div>
      </div>
    </div>
  );
}
