// src/features/listings/components/CreateListingForm.tsx
// Presentational form component for creating a listing

import React from "react";
import type { CreateListingFormProps } from "../types/CreateListingTypes";
import { LocationPicker } from "@/components/LocationPicker";

export const CreateListingForm: React.FC<CreateListingFormProps> = ({
  form,
  errors,
  touched,
  loading,
  isFormValid,
  categories,
  subcategories,
  selectedParent,
  selectedImages,
  previewUrls,
  successMessage,
  location,
  onLocationSelect,
  onChange,
  onBlur,
  onSubmit,
  onParentChange,
  onImageChange,
  onRemoveImage,
  onCancel,
}) => {
  return (
    <div className="bg-white rounded-lg shadow-md p-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-2">Create Listing</h1>
      <p className="text-gray-600 mb-8">Fill in the details below to list your item</p>

      <form onSubmit={onSubmit} className="space-y-6">
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
            onChange={onChange}
            onBlur={onBlur}
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
          <label
            htmlFor="description"
            className="block text-sm font-medium text-gray-700"
          >
            Description
          </label>
          <textarea
            id="description"
            name="description"
            placeholder="Describe your item in detail..."
            rows={6}
            value={form.description}
            onChange={onChange}
            onBlur={onBlur}
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
              onChange={onChange}
              onBlur={onBlur}
              className={`block w-full pl-8 px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                touched.price && errors.price
                  ? "border-red-300"
                  : "border-gray-300"
              }`}
            />
          </div>
          {touched.price && errors.price && (
            <p className="mt-1 text-sm text-red-600">{errors.price}</p>
          )}
        </div>

        {/* Parent Category Field */}
        <div>
          <label
            htmlFor="parentCategory"
            className="block text-sm font-medium text-gray-700"
          >
            Category
          </label>
          <select
            id="parentCategory"
            value={selectedParent}
            onChange={onParentChange}
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
          <label
            htmlFor="categorySlug"
            className="block text-sm font-medium text-gray-700"
          >
            Subcategory
          </label>
          <select
            id="categorySlug"
            name="categorySlug"
            value={form.categorySlug}
            onChange={onChange}
            onBlur={onBlur}
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
          <label
            htmlFor="condition"
            className="block text-sm font-medium text-gray-700"
          >
            Item Condition
          </label>
          <select
            id="condition"
            name="condition"
            value={form.condition}
            onChange={onChange}
            onBlur={onBlur}
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

        {/* Location Field */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Location (optional)
          </label>
          <p className="text-xs text-gray-500 mb-2">
            Click on the map to set a meetup location. Coordinates will be saved with your
            listing.
          </p>

          <LocationPicker value={location} onChange={onLocationSelect} />

          {/* Coordinate inputs */}
          <div className="mt-3 grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label
                htmlFor="latitude"
                className="block text-xs font-medium text-gray-600"
              >
                Latitude
              </label>
              <input
                id="latitude"
                name="latitude"
                type="number"
                step="0.000001"
                value={form.latitude || ""}
                onChange={onChange}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Click on map or enter manually"
              />
            </div>
            <div>
              <label
                htmlFor="longitude"
                className="block text-xs font-medium text-gray-600"
              >
                Longitude
              </label>
              <input
                id="longitude"
                name="longitude"
                type="number"
                step="0.000001"
                value={form.longitude || ""}
                onChange={onChange}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Click on map or enter manually"
              />
            </div>
          </div>
        </div>

        {/* Image Upload Field */}
        <div>
          <label htmlFor="images" className="block text-sm font-medium text-gray-700">
            Images (Optional)
          </label>
          <p className="text-xs text-gray-500 mb-2">
            Max 5 images, 5MB each. Formats: JPEG, PNG, WebP
          </p>
          <input
            id="images"
            type="file"
            multiple
            accept="image/jpeg,image/png,image/webp"
            onChange={onImageChange}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

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
                    onClick={() => onRemoveImage(index)}
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
            onClick={onCancel}
            className="flex-1 py-2 px-4 rounded-md border border-gray-300 text-gray-700 font-medium hover:bg-gray-50 transition"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};
