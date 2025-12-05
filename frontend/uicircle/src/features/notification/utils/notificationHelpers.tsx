// src/features/notification/utils/notificationHelpers.ts

import {
  MessageSquare,
  BadgeDollarSign,
  CheckCircle2,
  XCircle,
  PartyPopper,
  Star,
  ArrowDown,
  Heart,
  BellRing,
  Megaphone,
} from "lucide-react";


export const getNotificationIcon = (type: string): React.ReactNode => {
  const iconMap: Record<string, React.ReactNode> = {
    NEW_MESSAGE: <MessageSquare className="w-5 h-5 text-blue-600" />,
    PRICE_OFFER: <BadgeDollarSign className="w-5 h-5 text-green-600" />,
    OFFER_ACCEPTED: <CheckCircle2 className="w-5 h-5 text-green-600" />,
    OFFER_REJECTED: <XCircle className="w-5 h-5 text-red-600" />,
    LISTING_SOLD: <PartyPopper className="w-5 h-5 text-purple-600" />,
    NEW_REVIEW: <Star className="w-5 h-5 text-yellow-500" />,
    PRICE_CHANGE: <ArrowDown className="w-5 h-5 text-orange-600" />,
    LISTING_FAVORITED: <Heart className="w-5 h-5 text-pink-600" />,
    SYSTEM: <BellRing className="w-5 h-5 text-gray-600" />,
  };

  return iconMap[type] || <Megaphone className="w-5 h-5 text-gray-500" />;
};

/**
 * Converts notification type into a human-readable format
 */
export const formatNotificationType = (type: string): string => {
  return type.replace(/_/g, " ").toLowerCase();
};

/**
 * Checks whether the notification was created recently (within 24 hours)
 */
export const isRecentNotification = (createdAt: string): boolean => {
  try {
    const created = new Date(createdAt);
    const now = new Date();
    const hoursDiff = (now.getTime() - created.getTime()) / (1000 * 60 * 60);
    return hoursDiff <= 24;
  } catch {
    return false;
  }
};

/**
 * Converts unread notification count into a display format
 */
export const formatUnreadCount = (count: number): string => {
  if (count === 0) return "0";
  if (count > 99) return "99+";
  return count.toString();
};
