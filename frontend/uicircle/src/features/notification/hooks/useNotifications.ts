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
    } catch (error) {
      console.error("Failed to load notifications:", error);
      // Don't crash the app, just log the error
    } finally {
      setLoading(false);
    }
  };

  const refreshUnreadCount = async () => {
    try {
      const count = await getUnreadCount();
      setUnreadCount(count);
    } catch (error) {
      console.error("Failed to load unread count:", error);
      // Don't crash the app, just log the error
    }
  };

  const handleMarkAsRead = async (publicId: string) => {
    await markAsRead(publicId);
    setNotifications(prev =>
      prev.map(n =>
        n.publicId === publicId ? { ...n, isRead: true } : n
      )
    );
    refreshUnreadCount();
  };

  const handleMarkAllAsRead = async () => {
    await markAllAsRead();
    setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
    setUnreadCount(0);
  };

  const handleDelete = async (publicId: string) => {
    await deleteNotification(publicId);
    setNotifications(prev =>
      prev.filter(n => n.publicId !== publicId)
    );
    refreshUnreadCount();
  };

  const handleDeleteAll = async () => {
    await deleteAllNotifications();
    setNotifications([]);
    setUnreadCount(0);
  };

  useEffect(() => {
    loadPage(0);
    refreshUnreadCount();
  }, []);

  return {
    notifications,
    unreadCount,
    hasMore,
    loading,
    loadMore: () =>
      !loading && hasMore && loadPage(page + 1),
    markAsRead: handleMarkAsRead,
    markAllAsRead: handleMarkAllAsRead,
    deleteNotification: handleDelete,
    deleteAllNotifications: handleDeleteAll,
    reload: () => loadPage(0),
  };
}
