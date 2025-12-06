// src/features/messages/components/MessageRoom.tsx
import React, { useEffect, useRef, useState } from "react";
import type {
  ConversationResponse,
  MessageResponse,
} from "../types/message";
import {
  getMessages,
  sendMessage,
  markConversationAsRead,
} from "../services/MessageService";

interface MessageRoomProps {
  conversation: ConversationResponse;
}

const MESSAGE_POLL_INTERVAL_MS = 3000;
const PAGE_SIZE = 50;

const MessageRoom: React.FC<MessageRoomProps> = ({ conversation }) => {
  const [messages, setMessages] = useState<MessageResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [inputValue, setInputValue] = useState("");
  const [isSending, setIsSending] = useState(false);

  const bottomRef = useRef<HTMLDivElement | null>(null);

  const conversationId = conversation.conversationPublicId;

  const scrollToBottom = (behavior: ScrollBehavior = "auto") => {
    if (bottomRef.current) {
      bottomRef.current.scrollIntoView({ behavior });
    }
  };

  const formatTime = (iso: string) => {
    const d = new Date(iso);
    return d.toLocaleTimeString(undefined, {
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const fetchMessages = async (silent = false) => {
  try {
    if (!silent) {
      setLoading(true);
    }
    setError(null);

    const pageData = await getMessages(conversationId, 0, PAGE_SIZE);
        
        const sorted = [...pageData.content].sort(
        (a, b) =>
            new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
        );

        setMessages(sorted);

        markConversationAsRead(conversationId).catch((e) => {
        console.warn("Failed to mark conversation as read", e);
        });

        if (!silent) {
        scrollToBottom("smooth");
        } else {
        scrollToBottom();
        }
    } catch (err: any) {
        console.error(err);
        setError(err.message || "Failed to load messages");
    } finally {
        setLoading(false);
    }
    };

  useEffect(() => {
    let isMounted = true;
    let intervalId: number | undefined;

    const init = async () => {
      if (!isMounted) return;
      await fetchMessages(false);
    };

    init();

    intervalId = window.setInterval(() => {
      if (!isMounted) return;
      fetchMessages(true);
    }, MESSAGE_POLL_INTERVAL_MS);

    return () => {
      isMounted = false;
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [conversationId]);

  useEffect(() => {
    scrollToBottom();
  }, [messages.length]);

  const handleSend = async () => {
    const trimmed = inputValue.trim();
    if (!trimmed || isSending) return;

    try {
      setIsSending(true);

      await sendMessage(conversationId, trimmed);
      setInputValue("");
      await fetchMessages(true);
    } catch (err: any) {
      console.error(err);
      setError(err.message || "Failed to send message");
    } finally {
      setIsSending(false);
    }
  };

  const handleKeyDown: React.KeyboardEventHandler<HTMLTextAreaElement> = (
    e
  ) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="h-full flex flex-col">
      {/* Message List */}
      <div className="flex-1 overflow-y-auto px-3 py-3 space-y-2">
        {loading && messages.length === 0 && (
          <p className="text-xs text-gray-500">Loading messages...</p>
        )}

        {error && (
          <p className="text-xs text-red-500 mb-1">
            {error}
          </p>
        )}

        {messages.length === 0 && !loading && !error && (
          <p className="text-xs text-gray-500">
            No messages yet. Start the conversation!
          </p>
        )}

        {messages.map((msg) => {
          const isMine = msg.senderUsername !== conversation.otherUser.username;

          return (
            <div
              key={msg.messagePublicId}
              className={`flex w-full ${
                isMine ? "justify-end" : "justify-start"
              }`}
            >
              <div className="max-w-[80%] flex flex-col">
                <div
                  className={`px-3 py-2 rounded-2xl text-sm break-words ${
                    isMine
                      ? "bg-primary text-white rounded-br-sm"
                      : "bg-gray-100 text-gray-900 rounded-bl-sm"
                  }`}
                >
                  {msg.body}
                </div>
                <span
                  className={`mt-0.5 text-[10px] ${
                    isMine ? "text-gray-300 text-right" : "text-gray-400"
                  }`}
                >
                  {formatTime(msg.createdAt)}
                </span>
              </div>
            </div>
          );
        })}

        <div ref={bottomRef} />
      </div>

      {/* Input */}
      <div className="border-t border-border-light px-3 py-2 bg-background-light">
        <div className="flex items-end gap-2">
          <textarea
            className="flex-1 resize-none text-sm rounded-lg border border-border-light px-3 py-2 focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary bg-white"
            rows={1}
            value={inputValue}
            placeholder="Type a message..."
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={handleKeyDown}
          />
          <button
            type="button"
            onClick={handleSend}
            disabled={isSending || !inputValue.trim()}
            className="text-sm font-semibold px-3 py-2 rounded-lg bg-primary text-white disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Send
          </button>
        </div>
      </div>
    </div>
  );
};

export default MessageRoom;
