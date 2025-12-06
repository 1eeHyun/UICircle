// src/services/ConversationService.ts
import { CommonResponse, PageResponse } from "@/types/common";
import { ConversationResponse } from "../types/message";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

export async function getConversations(
  page = 0,
  size = 20
): Promise<PageResponse<ConversationResponse>> {
  const token = localStorage.getItem("token");

  const res = await fetch(
    `${API_BASE_URL}/api/conversations?page=${page}&size=${size}`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      credentials: "include",
    }
  );

  if (!res.ok) {
    const text = await res.text();
    console.error("Error fetching conversations:", text);
    throw new Error(`Failed to fetch conversations (${res.status})`);
  }

  const data = (await res.json()) as CommonResponse<
    PageResponse<ConversationResponse>
  >;

  if (!data.success) {
    throw new Error(data.message || "Failed to fetch conversations");
  }

  return data.data;
}
