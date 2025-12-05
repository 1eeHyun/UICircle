// src/features/notification/components/NotificationDropdown.tsx
import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useNotifications } from "@/features/notification/hooks/useNotifications";
import { NotificationBell } from "./ui/NotificationBell";
import { NotificationList } from "./ui/NotificationList";
import { NotificationResponse } from "@/types/notification";

export default function NotificationDropdown() {
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

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
  } = useNotifications(0, 10);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setOpen(false);
      }
    };

    if (open) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [open]);

  const handleItemClick = (notification: NotificationResponse) => {
    if (notification.linkUrl) {
      navigate(notification.linkUrl);
      setOpen(false);
    }
  };

  const handleViewAll = () => {
    navigate("/notifications");
    setOpen(false);
  };

  return (
    <div className="relative" ref={dropdownRef}>
      <NotificationBell
        unreadCount={unreadCount}
        onClick={() => setOpen((prev) => !prev)}
      />

      {open && (
        <div className="absolute right-0 mt-2 w-96 bg-white border border-gray-200 rounded-xl shadow-xl z-50 overflow-hidden">
          {/* Header */}
          <div className="flex items-center justify-between px-4 py-3 border-b border-gray-200 bg-gray-50">
            <h3 className="text-base font-semibold text-gray-900">Notifications</h3>
            <div className="flex gap-2">
              {unreadCount > 0 && (
                <button
                  onClick={markAllAsRead}
                  className="text-xs text-blue-600 hover:text-blue-700 font-medium transition-colors"
                >
                  Mark all read
                </button>
              )}
              {notifications.length > 0 && (
                <>
                  <span className="text-gray-300">•</span>
                  <button
                    onClick={deleteAllNotifications}
                    className="text-xs text-red-600 hover:text-red-700 font-medium transition-colors"
                  >
                    Clear all
                  </button>
                </>
              )}
            </div>
          </div>

          {/* Notifications List */}
          <div className="max-h-[32rem] overflow-y-auto p-2">
            <NotificationList
              notifications={notifications}
              loading={loading}
              hasMore={hasMore}
              compact
              onRead={markAsRead}
              onDelete={deleteNotification}
              onLoadMore={loadMore}
              onItemClick={handleItemClick}
              emptyMessage="You're all caught up!"
            />
          </div>

          {/* Footer - View All */}
          {notifications.length > 0 && (
            <div className="border-t border-gray-200 bg-gray-50">
              <button
                onClick={handleViewAll}
                className="w-full py-3 text-sm text-blue-600 hover:text-blue-700 hover:bg-blue-50 font-medium transition-colors"
              >
                View all notifications
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

