// src/features/messages/components/MessageDrawer.tsx
import React, { useEffect, useState } from "react";
import { XMarkIcon } from "@heroicons/react/24/outline";
import { ConversationResponse } from "../types/message";
import { getConversations } from "../services/ConversationService";
import ConversationListItem from "./ConversationListItem";

interface MessageDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  onSelectConversation?: (conversationId: string) => void;
}

const POLLING_INTERVAL_MS = 5000;

const MessageDrawer: React.FC<MessageDrawerProps> = ({
  isOpen,
  onClose,
  onSelectConversation,
}) => {
  const [conversations, setConversations] = useState<ConversationResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Close drawer on ESC key
  useEffect(() => {
    if (!isOpen) return;

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, onClose]);

  // Poll conversations while drawer is open
  useEffect(() => {
    if (!isOpen) return;

    let isMounted = true;
    let intervalId: number | undefined;

    const fetchData = async () => {
      try {
        if (!isMounted) return;
        setError(null);

        // Show loading only for first load
        if (conversations.length === 0) {
          setLoading(true);
        }

        const pageData = await getConversations(0, 20);
        if (!isMounted) return;

        setConversations(pageData.content);
      } catch (err: any) {
        if (!isMounted) return;
        setError(err.message || "Failed to load conversations");
      } finally {
        if (!isMounted) return;
        setLoading(false);
      }
    };

    // initial fetch
    fetchData();

    // polling interval
    intervalId = window.setInterval(fetchData, POLLING_INTERVAL_MS);

    return () => {
      isMounted = false;
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isOpen]);

  return (
    <>
      {/* Backdrop under navbar */}
      <div
        className={`fixed left-0 right-0 top-16 bottom-0 z-40 ${
          isOpen
            ? "opacity-100 pointer-events-auto"
            : "opacity-0 pointer-events-none"
        }`}
        onClick={onClose}
      />

      {/* Sliding panel */}
      <div
        className={`fixed right-0 top-16 bottom-0 z-50 w-full max-w-sm bg-background-light border-l border-border-light shadow-xl transform transition-transform duration-300 ease-out ${
          isOpen ? "translate-x-0" : "translate-x-full"
        }`}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-4 py-3 border-b border-border-light bg-background-light">
          <h2 className="text-base font-semibold text-gray-900">Messages</h2>
          <button
            type="button"
            onClick={onClose}
            className="p-1 rounded-full hover:bg-surface-light transition-colors"
          >
            <XMarkIcon className="w-5 h-5 text-gray-500" />
          </button>
        </div>

        {/* Scrollable content */}
        <div className="h-[calc(100%-56px)] overflow-y-auto">
          {loading && (
            <div className="p-4 text-sm text-gray-500">
              Loading conversations...
            </div>
          )}

          {!loading && error && (
            <div className="p-4 text-sm text-red-500">
              Failed to load conversations: {error}
            </div>
          )}

          {!loading && !error && conversations.length === 0 && (
            <div className="p-4 text-sm text-gray-500">
              No conversations yet.
            </div>
          )}

          {!loading && !error && conversations.length > 0 && (
            <ul className="divide-y divide-border-light">
              {conversations.map((conv) => (
                <ConversationListItem
                  key={conv.conversationPublicId}
                  conversation={conv}
                  onClick={(id) => onSelectConversation?.(id)}
                />
              ))}
            </ul>
          )}
        </div>
      </div>
    </>
  );
};

export default MessageDrawer;
