// src/features/notification/components/ui/NotificationList.tsx
import React from "react";
import { NotificationResponse } from "@/types/notification";
import { NotificationItem } from "./NotificationItem";

interface NotificationListProps {
  notifications: NotificationResponse[];
  loading: boolean;
  hasMore: boolean;
  compact?: boolean;
  onRead: (id: number) => void;
  onDelete: (id: number) => void;
  onLoadMore?: () => void;
  onItemClick?: (notification: NotificationResponse) => void;
  emptyMessage?: string;
}

export const NotificationList: React.FC<NotificationListProps> = ({
  notifications,
  loading,
  hasMore,
  compact = false,
  onRead,
  onDelete,
  onLoadMore,
  onItemClick,
  emptyMessage = "No notifications",
}) => {
  if (notifications.length === 0 && !loading) {
    return (
      <div className="flex flex-col items-center justify-center py-12 px-4">
        <svg
          className="w-16 h-16 text-gray-300 mb-3"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={1.5}
            d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
          />
        </svg>
        <p className="text-sm text-gray-500">{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className={`space-y-${compact ? "1" : "2"}`}>
      {notifications.map((notification) => (
        <NotificationItem
          key={notification.notificationId}
          notification={notification}
          onRead={onRead}
          onDelete={onDelete}
          onClick={onItemClick}
          compact={compact}
        />
      ))}

      {hasMore && onLoadMore && (
        <button
          disabled={loading}
          onClick={onLoadMore}
          className="w-full py-2.5 text-sm text-blue-600 hover:text-blue-700 hover:bg-blue-50 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed font-medium"
        >
          {loading ? "Loading..." : "Load more"}
        </button>
      )}

      {loading && notifications.length === 0 && (
        <div className="flex items-center justify-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      )}
    </div>
  );
};
