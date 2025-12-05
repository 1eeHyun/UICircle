export interface NotificationResponse {
  publicId: string; 
  type: string;
  title: string;
  message: string;
  linkUrl: string | null;
  entityType: string | null;
  entityId: string | null;
  isRead: boolean;
  readAt: string | null;
  createdAt: string;  
}
