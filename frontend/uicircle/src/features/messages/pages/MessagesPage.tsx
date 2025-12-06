// src/features/messages/pages/MessagesPage.tsx
import React, { useEffect, useState } from "react";
import Navbar from "@/components/Navbar";
import CategoryMenu from "@/components/CategoryMenu";
import { useCategories } from "@/features/listings/context/CategoryContext";
import { ConversationResponse } from "../types/message";
import { getConversations } from "../services/ConversationService";
import ConversationListItem from "../components/ConversationListItem";
import MessageRoom from "../components/MessageRoom";

const POLLING_INTERVAL_MS = 5000;

const MessagesPage: React.FC = () => {
  // Conversation list state
  const [conversations, setConversations] = useState<ConversationResponse[]>([]);
  const [selectedConversationId, setSelectedConversationId] = useState<string | null>(null);

  // Loading / error state for conversations
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Category data for top category bar (same as HomePage)
  const { categories } = useCategories();

  // Currently selected conversation
  const selectedConversation = conversations.find(
    (c) => c.conversationPublicId === selectedConversationId
  );

  // Fetch conversations from backend
  const fetchConversations = async () => {
    try {
      setError(null);
      if (conversations.length === 0) setLoading(true);

      const pageData = await getConversations(0, 50);
      setConversations(pageData.content);

      // Auto-select first conversation when nothing is selected
      if (!selectedConversationId && pageData.content.length > 0) {
        setSelectedConversationId(pageData.content[0].conversationPublicId);
      }    
    } catch (err: any) {
      console.error(err);
      setError(err.message || "Failed to load conversations");
    } finally {
      setLoading(false);
    }
  };

  // Initial load + polling for conversations
  useEffect(() => {
    let isMounted = true;
    let intervalId: number | undefined;

    const init = async () => {
      if (!isMounted) return;
      await fetchConversations();
    };

    init();

    // Poll conversations every POLLING_INTERVAL_MS ms
    intervalId = window.setInterval(() => {
      if (!isMounted) return;
      fetchConversations();
    }, POLLING_INTERVAL_MS);

    return () => {
      isMounted = false;
      if (intervalId) clearInterval(intervalId);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
      <Navbar />
      {/* Main area below navbar (full viewport height minus navbar) */}
      <main className="h-[calc(100vh-4rem)] flex flex-col">
        {/* Category bar under navbar (same pattern as HomePage) */}
        <CategoryMenu categories={categories} />

        {/* Chat layout container (fills remaining height) */}
        <div className="flex-1">
          <div className="mx-auto max-w-7xl px-2 md:px-4 py-4 sm:px-6 lg:px-8 h-full">
            {/* Chat shell */}
            <div className="flex h-full">
              {/* Left conversation list column */}
              <div className="w-72 border-r border-border-light flex flex-col">
                {/* Filter tabs: All / Selling / Buying */}
                <div className="px-4 pt-3 pb-2 border-b border-border-light">
                  <div className="flex gap-4 text-sm font-medium">
                    <button className="pb-3 border-b-3 border-primary text-primary">
                      All
                    </button>
                    {/* <button className="pb-3 text-gray-500">Selling</button>
                    <button className="pb-3 text-gray-500">Buying</button> */}
                  </div>
                </div>

                {/* Conversation list */}
                <div className="flex-1 overflow-y-auto">
                  {loading && conversations.length === 0 && (
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
                        <li
                          key={conv.conversationPublicId}
                          className={
                            conv.conversationPublicId === selectedConversationId
                              ? "bg-surface-light"
                              : ""
                          }
                        >
                          <ConversationListItem
                            conversation={conv}
                            onClick={(id) => setSelectedConversationId(id)}
                          />
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              </div>

              {/* Right message room column */}
              <div className="flex-1 flex flex-col">
                {selectedConversation ? (
                  <>
                    {/* Conversation header (other user + listing title) */}
                    <div className="h-14 px-4 flex items-center justify-between border-b border-border-light">
                      <div className="min-w-0">
                        <p className="text-sm font-semibold text-gray-900 truncate">
                          {selectedConversation.otherUser.username}
                        </p>
                        <p className="text-xs text-gray-500 truncate">
                          {selectedConversation.listing.title}
                        </p>
                      </div>
                    </div>

                    {/* Message room body */}
                    <div className="flex-1">
                      <MessageRoom conversation={selectedConversation} />
                    </div>
                  </>
                ) : (
                  // Empty state when no conversation is selected
                  <div className="flex-1 flex items-center justify-center text-sm text-gray-500">
                    Select a conversation from the left.
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </main>
    </>
  );
};

export default MessagesPage;