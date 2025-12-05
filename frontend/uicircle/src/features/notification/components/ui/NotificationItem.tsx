// src/features/notification/components/ui/NotificationItem.tsx
import React from "react";
import { NotificationResponse } from "@/types/notification";
import { formatDistanceToNow } from "date-fns";
import { getNotificationIcon, formatNotificationType } from "../../utils/notificationHelpers";

interface NotificationItemProps {
  notification: NotificationResponse;
  onRead: (id: string) => void;
  onDelete: (id: string) => void;
  onClick?: (notification: NotificationResponse) => void;
  compact?: boolean;
}

export const NotificationItem: React.FC<NotificationItemProps> = ({
  notification,
  onRead,
  onDelete,
  onClick,
  compact = false,
}) => {
  const handleClick = () => {
    if (!notification.isRead) {
      onRead(notification.publicId);
    }
    if (onClick) {
      onClick(notification);
    }
  };

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    onDelete(notification.publicId);
  };

  const timeAgo = React.useMemo(() => {
    try {
      return formatDistanceToNow(new Date(notification.createdAt), {
        addSuffix: true,
      });
    } catch {
      return "recently";
    }
  }, [notification.createdAt]);

  if (compact) {
    return (
      <div
        className={`flex items-start justify-between px-3 py-2.5 rounded-lg cursor-pointer transition-colors ${
          notification.isRead
            ? "bg-white hover:bg-gray-50"
            : "bg-blue-50 hover:bg-blue-100"
        }`}
        onClick={handleClick}
      >
        <div className="flex items-start gap-2 flex-1 min-w-0">
          <span className="text-lg flex-shrink-0 mt-0.5">
            {getNotificationIcon(notification.type)}
          </span>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-2">
              <p
                className={`text-sm font-medium text-gray-900 ${
                  !notification.isRead ? "font-semibold" : ""
                }`}
              >
                {notification.title}
              </p>
              {!notification.isRead && (
                <span className="w-2 h-2 bg-blue-600 rounded-full flex-shrink-0"></span>
              )}
            </div>
            <p className="text-xs text-gray-600 line-clamp-2 mt-0.5">
              {notification.message}
            </p>
            <p className="text-[10px] text-gray-400 mt-1">{timeAgo}</p>
          </div>
        </div>

        <button
          onClick={handleDelete}
          className="flex-shrink-0 ml-2 p-1 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded transition-colors"
          aria-label="Delete notification"
        >
          <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path
              fillRule="evenodd"
              d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
              clipRule="evenodd"
            />
          </svg>
        </button>
      </div>
    );
  }

  // Full size version for NotificationPage
  return (
    <div
      className={`flex items-start justify-between p-4 rounded-xl border cursor-pointer transition-all hover:shadow-md ${
        notification.isRead
          ? "bg-white border-gray-200"
          : "bg-blue-50 border-blue-200 shadow-sm"
      }`}
      onClick={handleClick}
    >
      <div className="flex items-start gap-3 flex-1 min-w-0">
        <span className="text-2xl flex-shrink-0">{getNotificationIcon(notification.type)}</span>
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2">
            <h3
              className={`text-base font-medium text-gray-900 ${
                !notification.isRead ? "font-semibold" : ""
              }`}
            >
              {notification.title}
            </h3>
            {!notification.isRead && (
              <span className="w-2.5 h-2.5 bg-blue-600 rounded-full flex-shrink-0"></span>
            )}
          </div>
          <p className="text-sm text-gray-600 mt-1">{notification.message}</p>
          <div className="flex items-center gap-3 mt-2">
            <span className="text-xs text-gray-400 capitalize">
              {formatNotificationType(notification.type)}
            </span>
            <span className="text-xs text-gray-400">•</span>
            <span className="text-xs text-gray-400">{timeAgo}</span>
          </div>
        </div>
      </div>

      <button
        onClick={handleDelete}
        className="flex-shrink-0 ml-3 p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
        aria-label="Delete notification"
      >
        <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
          <path
            fillRule="evenodd"
            d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
            clipRule="evenodd"
          />
        </svg>
      </button>
    </div>
  );
};
