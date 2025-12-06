// src/types/message.ts
import { ListingSummaryResponse } from "@/features/listings/services/ListingService";
import { UserResponse } from "@/features/auth/services/AuthService";

export interface MessageResponse {
  messagePublicId: string;
  senderUsername: string;
  body: string;
  messageType: string;
  readAt: string | null;
  createdAt: string;
}

export interface ConversationResponse {
  conversationPublicId: string;
  listing: ListingSummaryResponse;
  otherUser: UserResponse;
  lastMessage: MessageResponse | null;
  unreadCount: number;
  lastMessageAt: string;
  createdAt: string;
}
