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
  ListingSummaryResponse,
  CreateListingRequest 
} from "../services/ListingService";


interface CreateListingErrors {
  title: string;
  description: string;
  price: string;
  categorySlug: string;
  condition: string;
  submit: string;
}

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
  const [touched, setTouched] = useState({
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

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [categoriesData] = await Promise.all([
          getTopLevelCategories(),
          getAllActiveListings(0, 20, "createdAt", "DESC"),
        ]);
        
        setCategories(categoriesData);
        // Set initial subcategories if there are categories
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

  // Validation functions
  const validateTitle = (title: string): string => {
    if (!title) return "Title is required";
    if (title.length < 3) return "Title must be at least 3 characters";
    if (title.length > 100) return "Title must be less than 100 characters";
    return "";
  };

  const validateDescription = (description: string): string => {
    if (!description) return "Description is required";
    if (description.length < 10) return "Description must be at least 10 characters";
    if (description.length > 2000) return "Description must be less than 2000 characters";
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

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    let { name, value } = e.target;
    
    if (name === "title" || name === "description") {
      // Remove: < > { } [ ] \ " ' ; : &
      value = value.replace(/[<>{}[\]\\/"';:&]/g, "");
    }
    
    if (name === "price") {
      setForm({
        ...form,
        [name]: value === "" ? 0 : parseFloat(value),
      });
    } else {
      setForm({ ...form, [name]: value });
    }

    if (errors.submit) {
      setErrors({ ...errors, submit: "" });
    }

    if (touched[name as keyof typeof touched]) {
      validateField(name, value);
    }
  };

  const handleBlur = (
    e: React.FocusEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setTouched({ ...touched, [name]: true });
    validateField(name, value);
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

    return !titleError && !descriptionError && !priceError && !categoryError && !conditionError;
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
          condition: form.condition as "NEW" | "LIKE_NEW" | "GOOD" | "FAIR" | "POOR",
          slug: form.categorySlug,
          latitude: form.latitude,
          longitude: form.longitude,
        },
        selectedImages.length > 0 ? selectedImages : undefined
      );

      // Show success message
      setSuccessMessage("Listing created successfully!");
      
      // Redirect after 2 seconds
      setTimeout(() => {
        navigate("/home");
      }, 2000);
    } catch (err: any) {
      const errorMessage =
        err?.response?.data?.message || "Failed to create listing. Please try again.";
      setErrors((prev) => ({ ...prev, submit: errorMessage }));
    } finally {
      setLoading(false);
    }
  };

  const isFormValid =
    !errors.title &&
    !errors.description &&
    !errors.price &&
    !errors.categorySlug &&
    !errors.condition &&
    form.title &&
    form.description &&
    form.price > 0 &&
    form.categorySlug &&
    form.condition;

  const handleParentChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const parentSlug = e.target.value;
    setSelectedParent(parentSlug);
    
    const parent = categories.find(c => c.categorySlug === parentSlug);
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

    setSelectedImages([...selectedImages, ...validFiles]);

    // Create preview URLs
    const newPreviews = validFiles.map((file) => URL.createObjectURL(file));
    setPreviewUrls([...previewUrls, ...newPreviews]);
    
    // Clear submit error on successful upload
    if (errors.submit) {
      setErrors((prev) => ({ ...prev, submit: "" }));
    }
  };

  // Remove image handler
  const handleRemoveImage = (index: number) => {
    setSelectedImages(selectedImages.filter((_, i) => i !== index));
    URL.revokeObjectURL(previewUrls[index]);
    setPreviewUrls(previewUrls.filter((_, i) => i !== index));
  };

  return (
    <div className="min-h-screen bg-surface-light">
      <Navbar />
      <CategoryMenu categories={categories} />
      <div className="max-w-2xl mx-auto px-4 py-8">
        <div className="bg-white rounded-lg shadow-md p-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Create Listing</h1>
          <p className="text-gray-600 mb-8">Fill in the details below to list your item</p>

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Title Field */}
            <div>
              <label htmlFor="title" className="block text-sm font-medium text-gray-700">
                Title
              </label>
              <input
                id="title"
                name="title"
                type="text"
                placeholder="What are you selling?"
                value={form.title}
                onChange={handleChange}
                onBlur={handleBlur}
                className={`mt-1 block w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  touched.title && errors.title ? "border-red-300" : "border-gray-300"
                }`}
              />
              {touched.title && errors.title && (
                <p className="mt-1 text-sm text-red-600">{errors.title}</p>
              )}
              <p className="mt-1 text-xs text-gray-500">
                {form.title.length}/100 characters
              </p>
            </div>

            {/* Description Field */}
            <div>
              <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                Description
              </label>
              <textarea
                id="description"
                name="description"
                placeholder="Describe your item in detail..."
                rows={6}
                value={form.description}
                onChange={handleChange}
                onBlur={handleBlur}
                className={`mt-1 block w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  touched.description && errors.description
                    ? "border-red-300"
                    : "border-gray-300"
                }`}
              />
              {touched.description && errors.description && (
                <p className="mt-1 text-sm text-red-600">{errors.description}</p>
              )}
              <p className="mt-1 text-xs text-gray-500">
                {form.description.length}/2000 characters
              </p>
            </div>

            {/* Price Field */}
            <div>
              <label htmlFor="price" className="block text-sm font-medium text-gray-700">
                Price
              </label>
              <div className="mt-1 relative">
                <span className="absolute left-4 top-2 text-gray-500">$</span>
                <input
                  id="price"
                  name="price"
                  type="number"
                  placeholder="0.00"
                  step="0.01"
                  min="0"
                  value={form.price || ""}
                  onChange={handleChange}
                  onBlur={handleBlur}
                  className={`block w-full pl-8 px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                    touched.price && errors.price ? "border-red-300" : "border-gray-300"
                  }`}
                />
              </div>
              {touched.price && errors.price && (
                <p className="mt-1 text-sm text-red-600">{errors.price}</p>
              )}
            </div>

            {/* Parent Category Field */}
            <div>
              <label htmlFor="parentCategory" className="block text-sm font-medium text-gray-700">
                Category
              </label>
              <select
                id="parentCategory"
                value={selectedParent}
                onChange={handleParentChange}
                className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select a category...</option>
                {categories.map((cat) => (
                  <option key={cat.categorySlug} value={cat.categorySlug}>
                    {cat.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Subcategory Field */}
            <div>
              <label htmlFor="categorySlug" className="block text-sm font-medium text-gray-700">
                Subcategory
              </label>
              <select
                id="categorySlug"
                name="categorySlug"
                value={form.categorySlug}
                onChange={handleChange}
                onBlur={handleBlur}
                className={`mt-1 block w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  touched.categorySlug && errors.categorySlug
                    ? "border-red-300"
                    : "border-gray-300"
                }`}
              >
                <option value="">Select a subcategory...</option>
                {subcategories.map((subcat) => (
                  <option key={subcat.categorySlug} value={subcat.categorySlug}>
                    {subcat.name}
                  </option>
                ))}
              </select>
              {touched.categorySlug && errors.categorySlug && (
                <p className="mt-1 text-sm text-red-600">{errors.categorySlug}</p>
              )}
            </div>

            {/* Condition Field */}
            <div>
              <label htmlFor="condition" className="block text-sm font-medium text-gray-700">
                Item Condition
              </label>
              <select
                id="condition"
                name="condition"
                value={form.condition}
                onChange={handleChange}
                onBlur={handleBlur}
                className={`mt-1 block w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  touched.condition && errors.condition
                    ? "border-red-300"
                    : "border-gray-300"
                }`}
              >
                <option value="">Select a condition...</option>
                <option value="NEW">Brand New</option>
                <option value="LIKE_NEW">Like New</option>
                <option value="GOOD">Good</option>
                <option value="FAIR">Fair</option>
                <option value="POOR">Poor</option>
              </select>
              {touched.condition && errors.condition && (
                <p className="mt-1 text-sm text-red-600">{errors.condition}</p>
              )}
            </div>

            {/* Image Upload Field */}
            <div>
              <label htmlFor="images" className="block text-sm font-medium text-gray-700">
                Images (Optional)
              </label>
              <p className="text-xs text-gray-500 mb-2">Max 5 images, 5MB each. Formats: JPEG, PNG, WebP</p>
              <input
                id="images"
                type="file"
                multiple
                accept="image/jpeg,image/png,image/webp"
                onChange={handleImageChange}
                className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              
              {/* Image Previews */}
              {previewUrls.length > 0 && (
                <div className="mt-4 grid grid-cols-2 md:grid-cols-3 gap-4">
                  {previewUrls.map((url, index) => (
                    <div key={index} className="relative">
                      <img
                        src={url}
                        alt={`Preview ${index + 1}`}
                        className="w-full h-32 object-cover rounded-md"
                      />
                      <button
                        type="button"
                        onClick={() => handleRemoveImage(index)}
                        className="absolute top-1 right-1 bg-red-500 text-white rounded-full p-1 hover:bg-red-600"
                      >
                        ✕
                      </button>
                      <p className="text-xs text-gray-500 mt-1">
                        {index + 1} of {selectedImages.length}
                      </p>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Success Message */}
            {successMessage && (
              <div className="rounded-md bg-green-50 p-4">
                <p className="text-sm text-green-800">{successMessage}</p>
              </div>
            )}

            {/* Submit Error */}
            {errors.submit && (
              <div className="rounded-md bg-red-50 p-4">
                <p className="text-sm text-red-800">{errors.submit}</p>
              </div>
            )}

            {/* Submit Button */}
            <div className="flex gap-4">
              <button
                type="submit"
                disabled={loading || !isFormValid}
                className={`flex-1 py-2 px-4 rounded-md text-white font-medium transition ${
                  loading || !isFormValid
                    ? "bg-blue-400 cursor-not-allowed"
                    : "bg-blue-600 hover:bg-blue-700"
                }`}
              >
                {loading ? "Creating..." : "Create Listing"}
              </button>
              <button
                type="button"
                onClick={() => navigate(-1)}
                className="flex-1 py-2 px-4 rounded-md border border-gray-300 text-gray-700 font-medium hover:bg-gray-50 transition"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export { CreateListingPage };

