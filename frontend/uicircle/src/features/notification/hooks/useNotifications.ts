// src/features/notification/hooks/useNotifications.ts

import { useEffect, useState } from "react";
import {
  getNotifications,
  getUnreadCount,
  markAsRead,
  markAllAsRead,
  deleteNotification,
  deleteAllNotifications,
} from "@/features/notification/service/NotificationService";
import { NotificationResponse } from "@/types/notification";

export function useNotifications(initialPage = 0, pageSize = 10) {
  const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
  const [page, setPage] = useState(initialPage);
  const [hasMore, setHasMore] = useState(true);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);

  /**
   * Loads notifications for the given page.
   * If targetPage === 0, the list will be replaced.
   * Otherwise, the new page will be appended (infinite scroll).
   */
  const loadPage = async (targetPage = 0) => {
    setLoading(true);
    try {
      const pageData = await getNotifications(targetPage, pageSize);
      if (targetPage === 0) {
        setNotifications(pageData.content);
      } else {
        setNotifications(prev => [...prev, ...pageData.content]);
      }
      setHasMore(!pageData.last);
      setPage(targetPage);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Fetches unread notification count from backend.
   */
  const refreshUnreadCount = async () => {
    const count = await getUnreadCount();
    setUnreadCount(count);
  };

  /**
   * Marks a single notification as read and updates local state.
   * Uses publicId as identifier.
   */
  const handleMarkAsRead = async (publicId: string) => {
    await markAsRead(publicId);
    setNotifications(prev =>
      prev.map(n =>
        n.publicId === publicId ? { ...n, isRead: true } : n
      )
    );
    refreshUnreadCount();
  };

  /**
   * Marks all notifications as read.
   * Updates local state and resets unread counter.
   */
  const handleMarkAllAsRead = async () => {
    await markAllAsRead();
    setNotifications(prev =>
      prev.map(n => ({ ...n, isRead: true }))
    );
    setUnreadCount(0);
  };

  /**
   * Deletes a single notification.
   * Optimistically updates local state to remove it immediately.
   */
  const handleDelete = async (publicId: string) => {
    // 1) Optimistic update: update UI first
    setNotifications(prev =>
      prev.filter(n => n.publicId !== publicId)
    );

    try {
      // 2) Then call backend
      await deleteNotification(publicId);
      await refreshUnreadCount();
    } catch (e) {
      console.error("[handleDelete] API error:", e);
      // Optionally: rollback or show toast
    }
  };

  /**
   * Deletes all notifications from backend and clears local state.
   */
  const handleDeleteAll = async () => {
    await deleteAllNotifications();
    setNotifications([]);
    setUnreadCount(0);
    setHasMore(false);
    setPage(0);
  };

  // Initial load
  useEffect(() => {
    loadPage(0);
    refreshUnreadCount();
  }, []);

  return {
    notifications,
    unreadCount,
    hasMore,
    loading,

    // Pagination loader
    loadMore: () =>
      !loading && hasMore && loadPage(page + 1),

    // Notification actions
    markAsRead: handleMarkAsRead,
    markAllAsRead: handleMarkAllAsRead,
    deleteNotification: handleDelete,
    deleteAllNotifications: handleDeleteAll,

    // Manual reload from page 0
    reload: () => loadPage(0),
  };
}
