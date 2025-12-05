import { useState } from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";

interface Image {
  imageUrl: string;
}

interface ImageGalleryProps {
  images: Image[];
  title: string;
}

const ImageGallery = ({ images, title }: ImageGalleryProps) => {
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);

  const hasImages = images && images.length > 0;
  const currentImage = images?.[selectedImageIndex];

  // Move to previous image (loop to last if at index 0)
  const handlePrev = () => {
    setSelectedImageIndex((prev) =>
      prev === 0 ? images.length - 1 : prev - 1
    );
  };

  // Move to next image (loop to first if at last index)
  const handleNext = () => {
    setSelectedImageIndex((prev) =>
      prev === images.length - 1 ? 0 : prev + 1
    );
  };

  if (!hasImages) {
    return (
      <div className="w-full h-[500px] flex items-center justify-center bg-gray-100 rounded-lg">
        <span className="text-gray-400 text-sm">No image available</span>
      </div>
    );
  }

  return (
    <div className="flex gap-4">
      {/* ----------------------------- */}
      {/* Thumbnail List (left side)   */}
      {/* ----------------------------- */}
      {images.length > 1 && (
        <div className="flex flex-col gap-2 w-16 max-h-[500px] overflow-y-auto">
          {images.map((image, index) => (
            <button
              key={index}
              // Change image on click OR on hover
              onClick={() => setSelectedImageIndex(index)}
              onMouseEnter={() => setSelectedImageIndex(index)}
              className={`w-16 h-16 rounded-md overflow-hidden border flex-shrink-0 ${
                selectedImageIndex === index
                  ? "border-primary"
                  : "border-gray-200 hover:border-gray-300"
              }`}
            >
              <img
                src={image.imageUrl}
                alt={`Thumbnail ${index + 1}`}
                className="w-full h-full object-cover"
              />
            </button>
          ))}
        </div>
      )}

      {/* ----------------------------- */}
      {/* Main Image Area (right side)  */}
      {/* ----------------------------- */}
      <div className="relative flex-1 bg-gray-50 rounded-lg overflow-hidden group">
        <img
          src={currentImage.imageUrl}
          alt={title}
          className="w-full h-[500px] object-contain"
        />

        {/* Navigation arrows (show on hover) */}
        {images.length > 1 && (
          <>
            {/* Left arrow */}
            <button
              type="button"
              onClick={handlePrev}
              className="absolute left-4 top-1/2 -translate-y-1/2 
                         w-10 h-10 bg-white rounded-full shadow-md 
                         flex items-center justify-center 
                         opacity-0 group-hover:opacity-100 
                         transition-opacity hover:bg-gray-50"
            >
              <ChevronLeft size={24} className="text-gray-800" />
            </button>

            {/* Right arrow */}
            <button
              type="button"
              onClick={handleNext}
              className="absolute right-4 top-1/2 -translate-y-1/2 
                         w-10 h-10 bg-white rounded-full shadow-md 
                         flex items-center justify-center 
                         opacity-0 group-hover:opacity-100 
                         transition-opacity hover:bg-gray-50"
            >
              <ChevronRight size={24} className="text-gray-800" />
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default ImageGallery;
