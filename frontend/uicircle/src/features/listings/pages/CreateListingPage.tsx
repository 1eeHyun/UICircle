// src/features/listings/pages/CreateListingPage.tsx

import CategoryMenu from "@/components/CategoryMenu";
import Navbar from "@/components/Navbar";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  getTopLevelCategories,
  getAllActiveListings,
  createListing,
  CategoryResponse,
  CreateListingRequest,
} from "../services/ListingService";
import { CreateListingForm } from "../components/CreateListingForm";
import type {
  CreateListingErrors,
  CreateListingTouched,
} from "../types/CreateListingTypes";
import type { LatLngLiteral } from "leaflet";

const CreateListingPage = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState<CreateListingRequest>({
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

  const [loading, setLoading] = useState(false);
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
  const [successMessage, setSuccessMessage] = useState<string>("");

  // Location state for the map marker
  const [selectedLocation, setSelectedLocation] = useState<LatLngLiteral | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [categoriesData] = await Promise.all([
          getTopLevelCategories(),
          getAllActiveListings(0, 20, "createdAt", "DESC"),
        ]);

        setCategories(categoriesData);
        if (categoriesData.length > 0) {
          setSubcategories(categoriesData[0].children);
          setSelectedParent(categoriesData[0].categorySlug);
        }
      } catch (err: any) {
        setErrors((prev) => ({
          ...prev,
          submit: err?.response?.data?.message || "Failed to load data",
        }));
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

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
    const titleError = validateTitle(form.title);
    const descriptionError = validateDescription(form.description);
    const priceError = validatePrice(form.price);
    const categoryError = validateCategory(form.categorySlug);
    const conditionError = validateCondition(form.condition);

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
        const nextLat = name === "latitude" ? numeric : prevLatitude();
        const nextLng = name === "longitude" ? numeric : prevLongitude();
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

  // Helper to get latest latitude/longitude from form
  const prevLatitude = () => form.latitude;
  const prevLongitude = () => form.longitude;

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

    if (files.length + selectedImages.length > 5) {
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

    setSelectedImages((prev) => [...prev, ...validFiles]);

    const newPreviews = validFiles.map((file) => URL.createObjectURL(file));
    setPreviewUrls((prev) => [...prev, ...newPreviews]);

    if (errors.submit) {
      setErrors((prev) => ({ ...prev, submit: "" }));
    }
  };

  const handleRemoveImage = (index: number) => {
    setSelectedImages((prev) => prev.filter((_, i) => i !== index));
    URL.revokeObjectURL(previewUrls[index]);
    setPreviewUrls((prev) => prev.filter((_, i) => i !== index));
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
      setLoading(true);
      await createListing(
        {
          title: form.title,
          description: form.description,
          price: form.price,
          condition: form.condition as
            | "NEW"
            | "LIKE_NEW"
            | "GOOD"
            | "FAIR"
            | "POOR",
          categorySlug: form.categorySlug,
          latitude: form.latitude,
          longitude: form.longitude,
        },
        selectedImages.length > 0 ? selectedImages : undefined
      );

      setSuccessMessage("Listing created successfully!");

      setTimeout(() => {
        navigate("/home");
      }, 2000);
    } catch (err: any) {
      const errorMessage =
        err?.response?.data?.message ||
        "Failed to create listing. Please try again.";
      setErrors((prev) => ({ ...prev, submit: errorMessage }));
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate(-1);
  };

  const isFormValid =
    !errors.title &&
    !errors.description &&
    !errors.price &&
    !errors.categorySlug &&
    !errors.condition &&
    !!form.title &&
    !!form.description &&
    form.price > 0 &&
    !!form.categorySlug &&
    !!form.condition;

  const location: LatLngLiteral | null =
    selectedLocation ||
    (form.latitude !== 0 || form.longitude !== 0
      ? { lat: form.latitude, lng: form.longitude }
      : null);

  return (
    <div className="min-h-screen bg-surface-light">
      <Navbar />
      <CategoryMenu categories={categories} />
      <div className="max-w-2xl mx-auto px-4 py-8">
        <CreateListingForm
          form={form}
          errors={errors}
          touched={touched}
          loading={loading}
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
        />
      </div>
    </div>
  );
};

export { CreateListingPage };
