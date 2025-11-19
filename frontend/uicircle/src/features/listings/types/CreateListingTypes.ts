// src/features/listings/types/CreateListingTypes.ts
import type { CreateListingRequest, CategoryResponse } from "../services/ListingService";

export interface CreateListingErrors {
  title: string;
  description: string;
  price: string;
  categorySlug: string;
  condition: string;
  submit: string;
}

export interface CreateListingTouched {
  title: boolean;
  description: boolean;
  price: boolean;
  condition: boolean;
  categorySlug: boolean;
  longitude: boolean;
  latitude: boolean;
}

export interface CreateListingFormProps {
  form: CreateListingRequest;
  errors: CreateListingErrors;
  touched: CreateListingTouched;
  loading: boolean;
  isFormValid: boolean;
  categories: CategoryResponse[];
  subcategories: CategoryResponse[];
  selectedParent: string;
  selectedImages: File[];
  previewUrls: string[];
  successMessage: string;

  onChange: (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => void;
  onBlur: (
    e: React.FocusEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => void;
  onSubmit: (e: React.FormEvent) => void;
  onParentChange: (e: React.ChangeEvent<HTMLSelectElement>) => void;
  onImageChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onRemoveImage: (index: number) => void;
  onCancel: () => void;
}
