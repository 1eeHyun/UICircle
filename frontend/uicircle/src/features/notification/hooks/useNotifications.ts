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
    } finally {
      setLoading(false);
    }
  };

  const refreshUnreadCount = async () => {
    const count = await getUnreadCount();
    setUnreadCount(count);
  };

  const handleMarkAsRead = async (id: number) => {
    await markAsRead(id);
    setNotifications(prev =>
      prev.map(n =>
        n.notificationId === id ? { ...n, isRead: true } : n
      )
    );
    refreshUnreadCount();
  };

  const handleMarkAllAsRead = async () => {
    await markAllAsRead();
    setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
    setUnreadCount(0);
  };

  const handleDelete = async (id: number) => {
    await deleteNotification(id);
    setNotifications(prev =>
      prev.filter(n => n.notificationId !== id)
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
