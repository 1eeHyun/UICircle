// src/features/notification/service/notificationService.ts

import apiClient from "@/api/axios";
import { NOTIFICATION_ROUTES } from "@/api/routes/notificationRoutes";
import { NotificationResponse } from "@/types/notification";

interface CommonResponse<T> {
  code: string;
  message: string;
  data: T;
}

interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  last: boolean;
  totalElements: number;
  totalPages: number;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export const getNotifications = async (page = 0, size = 10) => {
  const { method, url } = NOTIFICATION_ROUTES.GET_NOTIFICATIONS;

  const res = await apiClient.request<
    CommonResponse<PageResponse<NotificationResponse>>
  >({
    method,
    url,
    params: { page, size },
  });

  return res.data.data; // PageResponse<NotificationResponse>
};

export const getUnreadNotifications = async (page = 0, size = 10) => {
  const { method, url } = NOTIFICATION_ROUTES.GET_UNREAD_NOTIFICATIONS;

  const res = await apiClient.request<
    CommonResponse<PageResponse<NotificationResponse>>
  >({
    method,
    url,
    params: { page, size },
  });

  return res.data.data;
};

export const getUnreadCount = async () => {
  const { method, url } = NOTIFICATION_ROUTES.GET_UNREAD_COUNT;

  const res = await apiClient.request<CommonResponse<number>>({
    method,
    url,
  });

  return res.data.data; // number
};

export const markAsRead = async (notificationId: string) => {
  const { method, url } = NOTIFICATION_ROUTES.MARK_AS_READ;

  const res = await apiClient.request<CommonResponse<NotificationResponse>>({
    method,
    url: url(notificationId),
  });

  return res.data.data; // NotificationResponse
};

export const markAllAsRead = async () => {
  const { method, url } = NOTIFICATION_ROUTES.MARK_ALL_AS_READ;

  await apiClient.request<CommonResponse<void>>({
    method,
    url,
  });
};

export const deleteNotification = async (notificationId: string) => {
  const { method, url } = NOTIFICATION_ROUTES.DELETE_ONE;

  await apiClient.request<CommonResponse<void>>({
    method,
    url: url(notificationId),
  });
};

export const deleteAllNotifications = async () => {
  const { method, url } = NOTIFICATION_ROUTES.DELETE_ALL;

  await apiClient.request<CommonResponse<void>>({
    method,
    url,
  });
};
