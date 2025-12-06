// src/components/Navbar.tsx
import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import NotificationDropdown from "@/features/notification/components/NotificationDropdown";
import { ChatBubbleLeftRightIcon } from "@heroicons/react/24/outline";
import MessageDrawer from "@/features/messages/components/MessageDrawer";

const Navbar: React.FC = () => {
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [showMessages, setShowMessages] = useState(false);
  const navigate = useNavigate();

  const username = localStorage.getItem("username");
  const token = localStorage.getItem("token");
  const isLoggedIn = !!token;

  const userMenuRef = useRef<HTMLDivElement | null>(null);

  // Handle logout
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    setShowUserMenu(false);
    navigate("/");
  };

  // Close user dropdown on outside click or Escape key
  useEffect(() => {
    if (!showUserMenu) return;

    const handleClickOutside = (event: MouseEvent) => {
      if (
        userMenuRef.current &&
        !userMenuRef.current.contains(event.target as Node)
      ) {
        setShowUserMenu(false);
      }
    };

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        setShowUserMenu(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    document.addEventListener("keydown", handleKeyDown);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [showUserMenu]);

  return (
    <>
      <nav className="bg-background-light border-b border-border-light sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex h-16 items-center justify-between gap-4">
            {/* Logo */}
            <button
              type="button"
              onClick={() => navigate("/home")}
              className="flex items-center gap-2 group"
            >
              <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center transition-all">
                <span className="text-background-light font-extrabold text-xl">
                  U
                </span>
              </div>
              <span className="text-xl font-bold text-gray-900 group-hover:text-primary transition-colors">
                UICircle
              </span>
            </button>

            {/* Search bar */}
            <div className="flex-1 max-w-xl mx-4 hidden sm:block">
              <div className="relative">
                <input
                  type="text"
                  placeholder="Search for anything"
                  onClick={() => navigate("/search")}
                  className="w-full px-4 py-2 pl-10 rounded-lg border border-border-light bg-surface-light text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary cursor-pointer"
                  readOnly
                />
                <svg
                  className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                  />
                </svg>
              </div>
            </div>

            {/* Right section */}
            <div className="flex items-center gap-3">
              {/* Message icon (opens slide drawer) */}
              {isLoggedIn && (
                <button
                  type="button"
                  onClick={() => setShowMessages(true)}
                  className="relative p-2 rounded-full hover:bg-surface-light transition-colors"
                  aria-label="Messages"
                >
                  <ChatBubbleLeftRightIcon className="w-6 h-6 text-gray-700" />
                </button>
              )}

              {/* Notification dropdown */}
              {isLoggedIn && <NotificationDropdown />}

              {/* User menu */}
              {isLoggedIn && (
                <div className="relative" ref={userMenuRef}>
                  <button
                    type="button"
                    onClick={() => setShowUserMenu((prev) => !prev)}
                    className="flex items-center gap-2 px-2 py-1.5 rounded-lg hover:bg-surface-light transition-colors"
                  >
                    <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                      <span className="text-xs font-semibold text-primary">
                        {username?.charAt(0).toUpperCase() || "U"}
                      </span>
                    </div>
                    <span className="hidden sm:inline text-sm font-medium text-gray-800">
                      {username}
                    </span>
                  </button>

                  {showUserMenu && (
                    <div className="absolute right-0 mt-2 w-48 rounded-lg bg-background-light border border-border-light py-1 text-sm shadow-lg z-40">
                      <hr className="my-1 border-border-light" />
                      <button
                        type="button"
                        onClick={handleLogout}
                        className="block w-full text-left px-4 py-2 text-primary hover:bg-red-50 transition-colors"
                      >
                        Logout
                      </button>
                    </div>
                  )}
                </div>
              )}

              {/* Sell button */}
              <button
                type="button"
                onClick={() =>
                  isLoggedIn ? navigate("/listing/create") : navigate("/login")
                }
                className="hidden sm:inline-flex items-center gap-1.5 px-4 py-2 rounded-lg bg-primary text-background-light text-sm font-semibold hover:bg-primary-dark transition-colors"
              >
                <span>List an item</span>
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* Message drawer (slide-in panel from right) */}
      {isLoggedIn && (
        <MessageDrawer
          isOpen={showMessages}
          onClose={() => setShowMessages(false)}
          onSelectConversation={(id) => {
            setShowMessages(false);
            navigate(`/messages/${id}`);
          }}
        />
      )}
    </>
  );
};

export default Navbar;
