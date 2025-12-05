// src/constants/api/notificationRoutes.ts

export const NOTIFICATION_ROUTES = {
  GET_NOTIFICATIONS: {
    method: "GET",
    url: "/notifications",
  },
  GET_UNREAD_NOTIFICATIONS: {
    method: "GET",
    url: "/notifications/unread",
  },
  GET_UNREAD_COUNT: {
    method: "GET",
    url: "/notifications/unread/count",
  },
  MARK_AS_READ: {
    method: "PATCH",
    url: (notificationId: number | string) => `/notifications/${notificationId}/read`,
  },
  MARK_ALL_AS_READ: {
    method: "PATCH",
    url: "/notifications/read-all",
  },
  DELETE_ONE: {
    method: "DELETE",
    url: (notificationId: number | string) => `/notifications/${notificationId}`,
  },
  DELETE_ALL: {
    method: "DELETE",
    url: "/notifications",
  },
} as const;
