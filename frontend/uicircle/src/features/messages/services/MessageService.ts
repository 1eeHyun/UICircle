// src/features/messages/services/MessageService.ts
import { CommonResponse, PageResponse } from "@/types/common";
import type { MessageResponse } from "../types/message";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

const withAuthHeader = () => {
  const token = localStorage.getItem("token");
  return {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
};

export async function getMessages(
  conversationId: string,
  page = 0,
  size = 30
): Promise<PageResponse<MessageResponse>> {
  const url = `${API_BASE_URL}/api/messages/${conversationId}?conversationId=${conversationId}&page=${page}&size=${size}`;

  const res = await fetch(url, {
    method: "GET",
    headers: withAuthHeader(),
    credentials: "include",
  });

  if (!res.ok) {
    const text = await res.text();
    console.error("Error fetching messages:", text);
    throw new Error(`Failed to fetch messages (${res.status})`);
  }

  const data = (await res.json()) as CommonResponse<
    PageResponse<MessageResponse>
  >;

  if (!data.success) {
    throw new Error(data.message || "Failed to fetch messages");
  }

  return data.data;
}

export async function sendMessage(
  conversationId: string,
  body: string
): Promise<MessageResponse> {
  const url = `${API_BASE_URL}/api/messages/${conversationId}`;

  const res = await fetch(url, {
    method: "POST",
    headers: withAuthHeader(),
    credentials: "include",
    body: JSON.stringify({ body }),
  });

  if (!res.ok) {
    const text = await res.text();
    console.error("Error sending message:", text);
    throw new Error(`Failed to send message (${res.status})`);
  }

  const data = (await res.json()) as CommonResponse<MessageResponse>;

  if (!data.success) {
    throw new Error(data.message || "Failed to send message");
  }

  return data.data;
}

export async function getUnreadCountInConversation(
  conversationId: string
): Promise<number> {
  const url = `${API_BASE_URL}/api/messages/${conversationId}/unread-count?conversationId=${conversationId}`;

  const res = await fetch(url, {
    method: "GET",
    headers: withAuthHeader(),
    credentials: "include",
  });

  if (!res.ok) {
    const text = await res.text();
    console.error("Error fetching unread count:", text);
    throw new Error(`Failed to fetch unread count (${res.status})`);
  }

  const data = (await res.json()) as CommonResponse<number>;

  if (!data.success) {
    throw new Error(data.message || "Failed to fetch unread count");
  }

  return data.data;
}

export async function markConversationAsRead(
  conversationId: string
): Promise<void> {
  const url = `${API_BASE_URL}/api/messages/${conversationId}/read?conversationId=${conversationId}`;

  const res = await fetch(url, {
    method: "POST",
    headers: withAuthHeader(),
    credentials: "include",
  });

  if (!res.ok) {
    const text = await res.text();
    console.error("Error marking conversation as read:", text);
    throw new Error(`Failed to mark conversation as read (${res.status})`);
  }
}

export async function markMessageAsRead(
  conversationId: string,
  messageId: string
): Promise<void> {
  const url = `${API_BASE_URL}/api/messages/${conversationId}/read-all?conversationId=${conversationId}&messageId=${messageId}`;

  const res = await fetch(url, {
    method: "POST",
    headers: withAuthHeader(),
    credentials: "include",
  });

  if (!res.ok) {
    const text = await res.text();
    console.error("Error marking message as read:", text);
    throw new Error(`Failed to mark message as read (${res.status})`);
  }
}

export async function deleteMessage(
  conversationId: string,
  messageId: string
): Promise<void> {
  const url = `${API_BASE_URL}/api/messages/${conversationId}/${messageId}?conversationId=${conversationId}&messageId=${messageId}`;

  const res = await fetch(url, {
    method: "DELETE",
    headers: withAuthHeader(),
    credentials: "include",
  });

  if (!res.ok) {
    const text = await res.text();
    console.error("Error deleting message:", text);
    throw new Error(`Failed to delete message (${res.status})`);
  }
}
