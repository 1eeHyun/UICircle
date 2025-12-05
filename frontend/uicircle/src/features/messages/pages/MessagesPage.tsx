import { useEffect, useState, useRef } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "@/components/Navbar";
import {
  getConversations,
  getMessages,
  sendMessage,
  markConversationAsRead,
  leaveConversation,
  ConversationResponse,
  MessageResponse,
} from "../services/MessageService";

export default function MessagesPage() {
  const navigate = useNavigate();
  const { conversationId } = useParams<{ conversationId?: string }>();
  
  const [conversations, setConversations] = useState<ConversationResponse[]>([]);
  const [selectedConversation, setSelectedConversation] = useState<ConversationResponse | null>(null);
  const [messages, setMessages] = useState<MessageResponse[]>([]);
  const [newMessage, setNewMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [sendingMessage, setSendingMessage] = useState(false);
  
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const currentUsername = localStorage.getItem("username");

  useEffect(() => {
    fetchConversations();
  }, []);

  useEffect(() => {
    if (conversationId) {
      const conv = conversations.find((c) => c.publicId === conversationId);
      if (conv) {
        handleSelectConversation(conv);
      }
    }
  }, [conversationId, conversations]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const fetchConversations = async () => {
    try {
      setLoading(true);
      const res = await getConversations(0, 50);
      setConversations(res.content);
      
      // Auto-select first conversation if none selected and no URL param
      if (!conversationId && res.content.length > 0) {
        handleSelectConversation(res.content[0]);
      }
    } catch (err) {
      console.error("Failed to fetch conversations:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectConversation = async (conversation: ConversationResponse) => {
    setSelectedConversation(conversation);
    
    try {
      const res = await getMessages(conversation.publicId, 0, 100);
      setMessages(res.content.reverse()); // Reverse to show oldest first
      
      // Mark as read
      if (conversation.unreadCount > 0) {
        await markConversationAsRead(conversation.publicId);
        // Update local state
        setConversations((prev) =>
          prev.map((c) =>
            c.publicId === conversation.publicId ? { ...c, unreadCount: 0 } : c
          )
        );
      }
    } catch (err) {
      console.error("Failed to fetch messages:", err);
    }
  };

  const handleSendMessage = async () => {
    if (!newMessage.trim() || !selectedConversation || sendingMessage) return;
    
    setSendingMessage(true);
    try {
      const msg = await sendMessage(selectedConversation.publicId, newMessage.trim());
      setMessages((prev) => [...prev, msg]);
      setNewMessage("");
      
      // Update last message in conversation list
      setConversations((prev) =>
        prev.map((c) =>
          c.publicId === selectedConversation.publicId
            ? { ...c, lastMessage: msg.body, lastMessageAt: msg.createdAt }
            : c
        )
      );
    } catch (err: any) {
      alert(err?.response?.data?.message || "Failed to send message");
    } finally {
      setSendingMessage(false);
    }
  };

  const handleLeaveConversation = async () => {
    if (!selectedConversation) return;
    if (!confirm("Are you sure you want to leave this conversation?")) return;
    
    try {
      await leaveConversation(selectedConversation.publicId);
      setConversations((prev) =>
        prev.filter((c) => c.publicId !== selectedConversation.publicId)
      );
      setSelectedConversation(null);
      setMessages([]);
    } catch (err: any) {
      alert(err?.response?.data?.message || "Failed to leave conversation");
    }
  };

  const formatTime = (dateStr: string) => {
    const date = new Date(dateStr);
    const now = new Date();
    const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
    
    if (diffDays === 0) {
      return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
    } else if (diffDays === 1) {
      return "Yesterday";
    } else if (diffDays < 7) {
      return date.toLocaleDateString([], { weekday: "short" });
    } else {
      return date.toLocaleDateString([], { month: "short", day: "numeric" });
    }
  };

  return (
    <div className="min-h-screen bg-surface-light flex flex-col">
      <Navbar />

      <div className="flex-1 flex max-w-6xl mx-auto w-full">
        {/* Conversation List */}
        <div className="w-80 border-r border-gray-200 bg-white flex flex-col">
          <div className="p-4 border-b border-gray-200">
            <h2 className="text-lg font-semibold">Messages</h2>
          </div>
          
          <div className="flex-1 overflow-y-auto">
            {loading ? (
              <div className="p-4 text-center">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
              </div>
            ) : conversations.length === 0 ? (
              <div className="p-4 text-center text-gray-500">
                No conversations yet
              </div>
            ) : (
              <ul>
                {conversations.map((conv) => (
                  <li
                    key={conv.publicId}
                    onClick={() => handleSelectConversation(conv)}
                    className={`p-4 cursor-pointer border-b border-gray-100 hover:bg-gray-50 transition-colors ${
                      selectedConversation?.publicId === conv.publicId ? "bg-primary/5" : ""
                    }`}
                  >
                    <div className="flex gap-3">
                      {/* Avatar */}
                      <div className="w-12 h-12 rounded-full bg-gray-200 flex-shrink-0 overflow-hidden">
                        {conv.otherPartyAvatarUrl ? (
                          <img
                            src={conv.otherPartyAvatarUrl}
                            alt={conv.otherPartyDisplayName}
                            className="w-full h-full object-cover"
                          />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center text-gray-500 font-medium">
                            {conv.otherPartyDisplayName?.charAt(0).toUpperCase() || "U"}
                          </div>
                        )}
                      </div>
                      
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center justify-between">
                          <span className="font-medium text-gray-900 truncate">
                            {conv.otherPartyDisplayName || conv.otherPartyUsername}
                          </span>
                          {conv.lastMessageAt && (
                            <span className="text-xs text-gray-400">
                              {formatTime(conv.lastMessageAt)}
                            </span>
                          )}
                        </div>
                        <p className="text-sm text-gray-500 truncate">{conv.listingTitle}</p>
                        <div className="flex items-center gap-2">
                          <p className="text-sm text-gray-400 truncate flex-1">
                            {conv.lastMessage || "No messages yet"}
                          </p>
                          {conv.unreadCount > 0 && (
                            <span className="px-1.5 py-0.5 text-xs font-semibold bg-primary text-white rounded-full">
                              {conv.unreadCount}
                            </span>
                          )}
                        </div>
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>

        {/* Chat Area */}
        <div className="flex-1 flex flex-col bg-white">
          {selectedConversation ? (
            <>
              {/* Chat Header */}
              <div className="p-4 border-b border-gray-200 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-full bg-gray-200 overflow-hidden">
                    {selectedConversation.otherPartyAvatarUrl ? (
                      <img
                        src={selectedConversation.otherPartyAvatarUrl}
                        alt={selectedConversation.otherPartyDisplayName}
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center text-gray-500">
                        {selectedConversation.otherPartyDisplayName?.charAt(0).toUpperCase()}
                      </div>
                    )}
                  </div>
                  <div>
                    <h3 className="font-medium">
                      {selectedConversation.otherPartyDisplayName || selectedConversation.otherPartyUsername}
                    </h3>
                    <p
                      className="text-sm text-primary hover:underline cursor-pointer"
                      onClick={() => navigate(`/listings/${selectedConversation.listingPublicId}`)}
                    >
                      {selectedConversation.listingTitle}
                    </p>
                  </div>
                </div>
                
                <button
                  onClick={handleLeaveConversation}
                  className="p-2 text-gray-400 hover:text-red-500 transition-colors"
                  title="Leave conversation"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>

              {/* Messages */}
              <div className="flex-1 overflow-y-auto p-4 space-y-4">
                {messages.map((msg) => {
                  const isOwn = msg.senderUsername === currentUsername;
                  return (
                    <div
                      key={msg.publicId}
                      className={`flex ${isOwn ? "justify-end" : "justify-start"}`}
                    >
                      <div
                        className={`max-w-xs md:max-w-md lg:max-w-lg px-4 py-2 rounded-2xl ${
                          isOwn
                            ? "bg-primary text-white rounded-br-md"
                            : "bg-gray-100 text-gray-900 rounded-bl-md"
                        }`}
                      >
                        <p className="whitespace-pre-wrap break-words">{msg.body}</p>
                        <p className={`text-xs mt-1 ${isOwn ? "text-white/70" : "text-gray-400"}`}>
                          {formatTime(msg.createdAt)}
                        </p>
                      </div>
                    </div>
                  );
                })}
                <div ref={messagesEndRef} />
              </div>

              {/* Message Input */}
              <div className="p-4 border-t border-gray-200">
                <div className="flex gap-2">
                  <input
                    type="text"
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    onKeyPress={(e) => e.key === "Enter" && handleSendMessage()}
                    placeholder="Type a message..."
                    className="flex-1 px-4 py-2 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary"
                  />
                  <button
                    onClick={handleSendMessage}
                    disabled={!newMessage.trim() || sendingMessage}
                    className="px-6 py-2 bg-primary text-white rounded-full hover:bg-primary-dark disabled:opacity-50 transition-colors"
                  >
                    {sendingMessage ? "..." : "Send"}
                  </button>
                </div>
              </div>
            </>
          ) : (
            <div className="flex-1 flex items-center justify-center text-gray-400">
              <div className="text-center">
                <svg className="w-16 h-16 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                </svg>
                <p>Select a conversation to start chatting</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

