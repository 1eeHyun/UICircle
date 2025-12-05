package edu.uic.marketplace.repository.listing;

import edu.uic.marketplace.model.listing.Favorite;
import edu.uic.marketplace.model.listing.Listing;
import edu.uic.marketplace.model.listing.ListingStatus;
import edu.uic.marketplace.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Favorite.FavoriteId> {

    /**
     * Find favorite by username and listing
     */
//    Optional<Favorite> findByUser_UsernameAndListing_ListingId(String username, Long listingId);

    @Query("""
        SELECT f
        FROM Favorite f
        JOIN FETCH f.listing l
        WHERE f.user.username = :username
          AND l.deletedAt IS NULL
          AND l.status = :status
        """)
    Page<Favorite> findActiveFavoritesByUsername(
            @Param("username") String username,
            @Param("status") ListingStatus status,
            Pageable pageable
    );

    boolean existsByUserAndListing(User user, Listing listing);

    Page<Favorite> findByUser_Username(String username, Pageable pageable);

    Long countByListing_ListingId(Long listingId);

    Long countByUser_Username(String username);

    @Query("""
            SELECT l.publicId 
            FROM Favorite f
            JOIN f.listing l
            WHERE f.user.username = :username
            """)
    List<String> findListingIdsByUser_Username(@Param("username") String username);


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

    boolean existsById_UserIdAndId_ListingId(Long userId, Long listingId);
}