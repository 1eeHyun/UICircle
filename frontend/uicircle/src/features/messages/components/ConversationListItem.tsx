// src/features/messages/components/ConversationListItem.tsx
import React from "react";
import { ConversationResponse } from "../types/message";

interface ConversationListItemProps {
  conversation: ConversationResponse;
  onClick?: (conversationId: string) => void;
}

const ConversationListItem: React.FC<ConversationListItemProps> = ({
  conversation,
  onClick,
}) => {
  const {
    conversationPublicId,
    listing,
    otherUser,
    lastMessage,
    unreadCount,
  } = conversation;

  return (
    <li>
      <button
        type="button"
        className="w-full px-4 py-3 flex items-start gap-3 hover:bg-surface-light transition-colors text-left"
        onClick={() => onClick?.(conversationPublicId)}
      >
        {/* Listing thumbnail */}
        <div className="w-12 h-12 rounded-md bg-gray-100 overflow-hidden flex-shrink-0">
          {listing.thumbnailUrl ? (
            <img
              src={listing.thumbnailUrl}
              alt={listing.title}
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-[10px] text-gray-400">
              No image
            </div>
          )}
        </div>

        {/* Text area */}
        <div className="flex-1 min-w-0">
          <div className="flex items-center justify-between gap-2">
            <p className="text-sm font-medium text-gray-900 truncate">
              {listing.title}
            </p>
            {unreadCount > 0 && (
              <span className="ml-2 inline-flex items-center justify-center min-w-[18px] h-5 px-1.5 rounded-full bg-primary text-[11px] font-semibold text-background-light">
                {unreadCount}
              </span>
            )}
          </div>

          <p className="mt-0.5 text-sm text-gray-500 truncate">
            {otherUser.username}
          </p>

          <p className="mt-1 text-sm text-gray-600 line-clamp-2">
            {lastMessage ? lastMessage.body : "No messages yet."}
          </p>
        </div>
      </button>
    </li>
  );
};

export default ConversationListItem;
