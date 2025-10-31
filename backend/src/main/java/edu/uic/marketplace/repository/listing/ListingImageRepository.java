package edu.uic.marketplace.repository.listing;

import edu.uic.marketplace.model.listing.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {
    
    /**
     * Find images by listing
     */
    List<ListingImage> findByListing_ListingId(Long listingId);
    
    /**
     * Find images by listing ordered by display order
     */
    List<ListingImage> findByListing_ListingIdOrderByDisplayOrderAsc(Long listingId);
    
    /**
     * Delete images by listing
     */
    void deleteByListing_ListingId(Long listingId);
    
    /**
     * Count images for listing
     */
    Long countByListing_ListingId(Long listingId);
}
