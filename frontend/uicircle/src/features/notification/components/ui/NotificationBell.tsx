// src/features/notification/components/ui/NotificationBell.tsx
import React from "react";
import { formatUnreadCount } from "../../utils/notificationHelpers";

interface NotificationBellProps {
  unreadCount: number;
  onClick: () => void;
  className?: string;
}

export const NotificationBell: React.FC<NotificationBellProps> = ({
  unreadCount,
  onClick,
  className = "",
}) => {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`relative flex items-center justify-center p-2 rounded-lg hover:bg-surface-light transition-colors ${className}`}
      aria-label="Notifications"
    >
      {/* Bell Icon */}
      <svg
        className="w-6 h-6 text-gray-700"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
        />
      </svg>

      {/* Unread Badge */}
      {unreadCount > 0 && (
        <span className="absolute -top-1 -right-1 flex items-center justify-center min-w-[18px] h-[18px] px-1 bg-red-600 text-white text-[10px] font-bold rounded-full">
          {formatUnreadCount(unreadCount)}
        </span>
      )}
    </button>
  );
};
