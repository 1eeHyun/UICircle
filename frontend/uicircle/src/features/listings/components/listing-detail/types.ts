// Shared types for listing detail components

export interface Image {
  imageUrl: string;
}

export interface Seller {
  username: string;
}

export interface Category {
  name: string;
}

export interface ListingDetailData {
  id: string;
  title: string;
  description: string;
  price: number;
  condition: string;
  viewCount: number;
  favoriteCount: number;
  createdAt: string;
  images: Image[];
  seller: Seller;
  category: Category;
}
