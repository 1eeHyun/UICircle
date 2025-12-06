// src/features/listings/pages/EditListingPage.tsx

import CategoryMenu from "@/components/CategoryMenu";
import Navbar from "@/components/Navbar";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  getTopLevelCategories,
  getSubcategories,
  getListing,
  updateListing,
  CategoryResponse,
  ListingResponse,
  UpdateListingRequest,
} from "../services/ListingService";
import { CreateListingForm } from "../components/CreateListingForm";
import type {
  CreateListingErrors,
  CreateListingTouched,
} from "../types/CreateListingTypes";
import type { LatLngLiteral } from "leaflet";

const EditListingPage = () => {
  const navigate = useNavigate();
  const { listingId } = useParams<{ listingId: string }>();

  const [listing, setListing] = useState<ListingResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [form, setForm] = useState<UpdateListingRequest & { categorySlug: string; latitude: number; longitude: number }>({
    title: "",
    description: "",
    price: 0,
    condition: "NEW",
    categorySlug: "",
    longitude: 0,
    latitude: 0,
  });

  const [errors, setErrors] = useState<CreateListingErrors>({
    title: "",
    description: "",
    price: "",
    categorySlug: "",
    condition: "",
    submit: "",
  });

  const [touched, setTouched] = useState<CreateListingTouched>({
    title: false,
    description: false,
    price: false,
    condition: false,
    categorySlug: false,
    longitude: false,
    latitude: false,
  });

  const [categories, setCategories] = useState<CategoryResponse[]>([]);
  const [subcategories, setSubcategories] = useState<CategoryResponse[]>([]);
  const [selectedParent, setSelectedParent] = useState<string>("");

  const [selectedImages, setSelectedImages] = useState<File[]>([]);
  const [previewUrls, setPreviewUrls] = useState<string[]>([]);
  const [newImageObjectUrls, setNewImageObjectUrls] = useState<string[]>([]);
  const [existingImages, setExistingImages] = useState<Array<{ publicId: string; imageUrl: string }>>([]);
  const [removedExistingImageIds, setRemovedExistingImageIds] = useState<Set<string>>(new Set());
  const [successMessage, setSuccessMessage] = useState<string>("");

  // Location state for the map marker
  const [selectedLocation, setSelectedLocation] = useState<LatLngLiteral | null>(null);

  // Load listing data and categories
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [categoriesData, listingData] = await Promise.all([
          getTopLevelCategories(),
          getListing(listingId!),
        ]);

        setCategories(categoriesData);
        setListing(listingData);

        // Pre-populate form with listing data
        setForm({
          title: listingData.title,
          description: listingData.description,
          price: listingData.price,
          condition: listingData.condition,
          categorySlug: listingData.category.categorySlug,
          latitude: listingData.latitude,
          longitude: listingData.longitude,
        });

        // Set existing images
        setExistingImages(listingData.images);
        setPreviewUrls(listingData.images.map(img => img.imageUrl));

        // Find parent category and set subcategories
        const findParentCategory = (cats: CategoryResponse[], targetSlug: string): CategoryResponse | null => {
          for (const cat of cats) {
            if (cat.categorySlug === targetSlug) {
              return cat;
            }
            if (cat.children) {
              const found = findParentCategory(cat.children, targetSlug);
              if (found) return found;
            }
          }
          return null;
        };

        // Find the category in the tree
        const category = findParentCategory(categoriesData, listingData.category.categorySlug);
        if (category?.parentSlug) {
          // It's a subcategory, find parent
          const parent = categoriesData.find(c => c.categorySlug === category.parentSlug);
          if (parent) {
            setSelectedParent(parent.categorySlug);
            setSubcategories(parent.children);
          }
        } else {
          // It's a top-level category
          const parent = categoriesData.find(c => c.categorySlug === listingData.category.categorySlug);
          if (parent) {
            setSelectedParent(parent.categorySlug);
            setSubcategories(parent.children);
          }
        }

        // Set location
        if (listingData.latitude && listingData.longitude) {
          setSelectedLocation({
            lat: listingData.latitude,
            lng: listingData.longitude,
          });
        }
      } catch (err: any) {
        setErrors((prev) => ({
          ...prev,
          submit: err?.response?.data?.message || "Failed to load listing",
        }));
      } finally {
        setLoading(false);
      }
    };

    if (listingId) {
      fetchData();
    }
  }, [listingId]);

  // Load subcategories when parent changes
  useEffect(() => {
    if (selectedParent) {
      getSubcategories(selectedParent).then(setSubcategories).catch(console.error);
    }
  }, [selectedParent]);

  // Validation helpers
  const validateTitle = (title: string): string => {
    if (!title) return "Title is required";
    if (title.length < 3) return "Title must be at least 3 characters";
    if (title.length > 100) return "Title must be less than 100 characters";
    return "";
  };

  const validateDescription = (description: string): string => {
    if (!description) return "Description is required";
    if (description.length < 10) return "Description must be at least 10 characters";
    if (description.length > 2000)
      return "Description must be less than 2000 characters";
    return "";
  };

  const validatePrice = (price: number): string => {
    if (!price) return "Price is required";
    if (price <= 0) return "Price must be greater than 0";
    if (price > 999999) return "Price must be less than 999,999";
    return "";
  };

  const validateCategory = (category: string): string => {
    if (!category) return "Category is required";
    return "";
  };

  const validateCondition = (condition: string): string => {
    if (!condition) return "Condition is required";
    return "";
  };

  const validateField = (name: string, value: string) => {
    let error = "";

    switch (name) {
      case "title":
        error = validateTitle(value);
        break;
      case "description":
        error = validateDescription(value);
        break;
      case "price":
        error = validatePrice(parseFloat(value) || 0);
        break;
      case "categorySlug":
        error = validateCategory(value);
        break;
      case "condition":
        error = validateCondition(value);
        break;
    }

    setErrors((prev) => ({ ...prev, [name]: error }));
  };

  const validateForm = (): boolean => {
    const titleError = validateTitle(form.title || "");
    const descriptionError = validateDescription(form.description || "");
    const priceError = validatePrice(form.price || 0);
    const categoryError = validateCategory(form.categorySlug);
    const conditionError = validateCondition(form.condition || "");

    setErrors({
      title: titleError,
      description: descriptionError,
      price: priceError,
      categorySlug: categoryError,
      condition: conditionError,
      submit: "",
    });

    setTouched({
      title: true,
      description: true,
      price: true,
      categorySlug: true,
      condition: true,
      longitude: true,
      latitude: true,
    });

    return (
      !titleError && !descriptionError && !priceError && !categoryError && !conditionError
    );
  };

  // Form event handlers
  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    let { name, value } = e.target;

    if (name === "title" || name === "description") {
      // Sanitize user input
      value = value.replace(/[<>{}[\]\\/"';:&]/g, "");
    }

    if (name === "price" || name === "latitude" || name === "longitude") {
      const numeric = value === "" ? 0 : parseFloat(value);
      setForm((prev) => ({
        ...prev,
        [name]: numeric,
      }));

      // If user edits coordinates manually, also update map marker
      if (name === "latitude" || name === "longitude") {
        const nextLat = name === "latitude" ? numeric : form.latitude;
        const nextLng = name === "longitude" ? numeric : form.longitude;
        if (nextLat && nextLng) {
          setSelectedLocation({ lat: nextLat, lng: nextLng });
        }
      }
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }

    if (errors.submit) {
      setErrors((prev) => ({ ...prev, submit: "" }));
    }

    if (touched[name as keyof CreateListingTouched]) {
      validateField(name, value);
    }
  };

  const handleBlur = (
    e: React.FocusEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setTouched((prev) => ({ ...prev, [name]: true }));
    validateField(name, value);
  };

  const handleParentChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const parentSlug = e.target.value;
    setSelectedParent(parentSlug);

    const parent = categories.find((c) => c.categorySlug === parentSlug);
    if (parent) {
      setSubcategories(parent.children);
    }
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || []);

    const totalImages = existingImages.filter(img => !removedExistingImageIds.has(img.publicId)).length + selectedImages.length;
    if (files.length + totalImages > 5) {
      setErrors((prev) => ({
        ...prev,
        submit: "Maximum 5 images allowed",
      }));
      return;
    }

    const validFiles = files.filter((file) => {
      if (!["image/jpeg", "image/png", "image/webp"].includes(file.type)) {
        setErrors((prev) => ({
          ...prev,
          submit: "Only JPEG, PNG, and WebP images are allowed",
        }));
        return false;
      }
      if (file.size > 5 * 1024 * 1024) {
        setErrors((prev) => ({
          ...prev,
          submit: "Each image must be less than 5MB",
        }));
        return false;
      }
      return true;
    });

    const newPreviews = validFiles.map((file) => URL.createObjectURL(file));
    setSelectedImages((prev) => [...prev, ...validFiles]);
    setNewImageObjectUrls((prev) => [...prev, ...newPreviews]);

    if (errors.submit) {
      setErrors((prev) => ({ ...prev, submit: "" }));
    }
  };

  const handleRemoveImage = (index: number) => {
    // Check if it's an existing image or a new one
    const visibleExistingImages = existingImages.filter(img => !removedExistingImageIds.has(img.publicId));
    const existingCount = visibleExistingImages.length;
    
    if (index < existingCount) {
      // It's an existing image - mark it for removal
      const imageToRemove = visibleExistingImages[index];
      setRemovedExistingImageIds(prev => new Set(prev).add(imageToRemove.publicId));
    } else {
      // It's a new image
      const newImageIndex = index - existingCount;
      // Clean up object URL first
      const urlToRevoke = newImageObjectUrls[newImageIndex];
      if (urlToRevoke) {
        URL.revokeObjectURL(urlToRevoke);
      }
      // Remove from both arrays
      setSelectedImages((prev) => prev.filter((_, i) => i !== newImageIndex));
      setNewImageObjectUrls((prev) => prev.filter((_, i) => i !== newImageIndex));
    }
  };

  // Handle map click to set location and update form
  const handleLocationSelect = (coords: LatLngLiteral) => {
    setSelectedLocation(coords);
    setForm((prev) => ({
      ...prev,
      latitude: coords.lat,
      longitude: coords.lng,
    }));
    setTouched((prev) => ({
      ...prev,
      latitude: true,
      longitude: true,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      setSubmitting(true);
      
      // Prepare update request - only include changed fields
      const updateRequest: UpdateListingRequest = {
        title: form.title,
        description: form.description,
        price: form.price,
        condition: form.condition as "NEW" | "LIKE_NEW" | "GOOD" | "FAIR" | "POOR",
      };

      // If category changed, we need to handle it (but backend UpdateListingRequest doesn't have categorySlug)
      // For now, we'll skip category updates in edit mode, or we could add it to the backend
      
      // Note: Backend replaces ALL images when images are provided
      // If user modified images (added new or removed existing), send new images
      // If no image changes, don't send images parameter to keep existing ones
      const hasImageChanges = selectedImages.length > 0 || removedExistingImageIds.size > 0;
      const imagesToSend = hasImageChanges ? selectedImages : undefined;

      await updateListing(listingId!, updateRequest, imagesToSend);

      setSuccessMessage("Listing updated successfully!");

      setTimeout(() => {
        navigate(`/listings/${listingId}`);
      }, 2000);
    } catch (err: any) {
      const errorMessage =
        err?.response?.data?.message ||
        "Failed to update listing. Please try again.";
      setErrors((prev) => ({ ...prev, submit: errorMessage }));
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate(`/listings/${listingId}`);
  };

  const isFormValid =
    !errors.title &&
    !errors.description &&
    !errors.price &&
    !errors.categorySlug &&
    !errors.condition &&
    !!form.title &&
    !!form.description &&
    !!form.categorySlug &&
    !!form.condition;

  const location: LatLngLiteral | null =
    selectedLocation ||
    (form.latitude !== 0 || form.longitude !== 0
      ? { lat: form.latitude, lng: form.longitude }
      : null);

  // Update previewUrls to include existing images (that weren't removed) and new images
  useEffect(() => {
    const existingUrls = existingImages
      .filter(img => !removedExistingImageIds.has(img.publicId))
      .map(img => img.imageUrl);
    setPreviewUrls([...existingUrls, ...newImageObjectUrls]);
  }, [existingImages, removedExistingImageIds, newImageObjectUrls]);

  // Cleanup object URLs on unmount
  useEffect(() => {
    return () => {
      newImageObjectUrls.forEach(url => URL.revokeObjectURL(url));
    };
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-surface-light">
        <Navbar />
        <CategoryMenu categories={categories} />
        <div className="max-w-2xl mx-auto px-4 py-8">
          <div className="text-center">Loading...</div>
        </div>
      </div>
    );
  }

  if (!listing) {
    return (
      <div className="min-h-screen bg-surface-light">
        <Navbar />
        <CategoryMenu categories={categories} />
        <div className="max-w-2xl mx-auto px-4 py-8">
          <div className="text-center text-red-600">Listing not found</div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-surface-light">
      <Navbar />
      <CategoryMenu categories={categories} />
      <div className="max-w-2xl mx-auto px-4 py-8">
        <CreateListingForm
          form={{
            title: form.title || "",
            description: form.description || "",
            price: form.price || 0,
            condition: form.condition || "NEW",
            categorySlug: form.categorySlug || "",
            latitude: form.latitude || 0,
            longitude: form.longitude || 0,
          }}
          errors={errors}
          touched={touched}
          loading={submitting}
          isFormValid={isFormValid}
          categories={categories}
          subcategories={subcategories}
          selectedParent={selectedParent}
          selectedImages={selectedImages}
          previewUrls={previewUrls}
          successMessage={successMessage}
          location={location}
          onLocationSelect={handleLocationSelect}
          onChange={handleChange}
          onBlur={handleBlur}
          onSubmit={handleSubmit}
          onParentChange={handleParentChange}
          onImageChange={handleImageChange}
          onRemoveImage={handleRemoveImage}
          onCancel={handleCancel}
          submitButtonText="Update Listing"
          loadingButtonText="Updating..."
          formTitle="Edit Listing"
          formDescription="Update your listing details below"
        />
      </div>
    </div>
  );
};

export { EditListingPage };

