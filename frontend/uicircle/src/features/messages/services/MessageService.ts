import instance from "@/api/axios";

export interface ConversationResponse {
  publicId: string;
  listingPublicId: string;
  listingTitle: string;
  listingThumbnailUrl: string | null;
  otherPartyUsername: string;
  otherPartyDisplayName: string;
  otherPartyAvatarUrl: string | null;
  lastMessage: string | null;
  lastMessageAt: string | null;
  unreadCount: number;
  createdAt: string;
}

export interface MessageResponse {
  publicId: string;
  senderUsername: string;
  senderDisplayName: string;
  senderAvatarUrl: string | null;
  body: string;
  isRead: boolean;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface SendMessageRequest {
  body: string;
}

// Get all conversations for the current user
export const getConversations = async (
  page = 0,
  size = 20
): Promise<PageResponse<ConversationResponse>> => {
  const res = await instance.get<{ success: boolean; data: PageResponse<ConversationResponse> }>(
    "/conversations",
    { params: { page, size } }
  );
  return res.data.data;
};

// Get a single conversation
export const getConversation = async (
  conversationId: string
): Promise<ConversationResponse> => {
  const res = await instance.get<{ success: boolean; data: ConversationResponse }>(
    `/conversations/${conversationId}`
  );
  return res.data.data;
};

// Get messages in a conversation
export const getMessages = async (
  conversationId: string,
  page = 0,
  size = 50
): Promise<PageResponse<MessageResponse>> => {
  const res = await instance.get<{ success: boolean; data: PageResponse<MessageResponse> }>(
    `/messages/${conversationId}`,
    { params: { conversationId, page, size } }
  );
  return res.data.data;
};

// Send a message
export const sendMessage = async (
  conversationId: string,
  body: string
): Promise<MessageResponse> => {
  const res = await instance.post<{ success: boolean; data: MessageResponse }>(
    `/messages/${conversationId}`,
    { body }
  );
  return res.data.data;
};

// Mark conversation as read
export const markConversationAsRead = async (conversationId: string): Promise<void> => {
  await instance.post(`/messages/${conversationId}/read`);
};

// Get unread conversation count
export const getUnreadConversationCount = async (): Promise<number> => {
  const res = await instance.get<{ success: boolean; data: number }>(
    "/conversations/unread-count"
  );
  return res.data.data;
};

// Leave a conversation
export const leaveConversation = async (conversationId: string): Promise<void> => {
  await instance.post(`/conversations/${conversationId}/leave`);
};

