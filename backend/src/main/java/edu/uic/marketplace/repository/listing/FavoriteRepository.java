package edu.uic.marketplace.repository.listing;

import edu.uic.marketplace.model.listing.Favorite;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Favorite.FavoriteId> {

    /**
     * Find favorite by username and listing
     */
    Optional<Favorite> findByUser_UsernameAndListing_ListingId(String username, Long listingId);

    boolean existsByUserAndListing(User user, Listing listing);

    Page<Favorite> findByUser_Username(String username, Pageable pageable);

    Long countByListing_ListingId(Long listingId);

    Long countByUser_Username(String username);

    @Query("SELECT f.listing.listingId FROM Favorite f WHERE f.user.username = :username")
    List<Long> findListingIdsByUser_Username(@Param("username") String username);

    void deleteByUserAndListing(User user, Listing listing);

    void deleteByListing_ListingId(Long listingId);

    void deleteByUser_Username(String username);

    /**
     * Find all favorites by user (for batch operations)
     */
    List<Favorite> findByUser(User user);

    @Query("""
           SELECT l.publicId 
           FROM Favorite f 
           JOIN f.listing l
           WHERE f.user.username = :username 
             AND l.publicId IN :listingPublicIds
           """)
    List<String> findFavoritedListingPublicIds(
            @Param("username") String username,
            @Param("listingPublicIds") List<String> listingPublicIds
    );
}