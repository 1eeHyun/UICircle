package edu.uic.marketplace.service.listing;

import edu.uic.marketplace.dto.request.listing.CreateListingRequest;
import edu.uic.marketplace.dto.request.listing.UpdateListingRequest;
import edu.uic.marketplace.dto.response.listing.ListingResponse;
import edu.uic.marketplace.model.listing.*;
import edu.uic.marketplace.model.user.User;
import edu.uic.marketplace.repository.listing.ListingRepository;
import edu.uic.marketplace.service.common.S3Service;
import edu.uic.marketplace.validator.auth.AuthValidator;
import edu.uic.marketplace.validator.listing.CategoryValidator;
import edu.uic.marketplace.validator.listing.ListingValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceImplTest {

    @Mock private ListingRepository listingRepository;
    @Mock private ListingValidator listingValidator;
    @Mock private AuthValidator authValidator;
    @Mock private CategoryValidator categoryValidator;
    @Mock private S3Service s3Service;

    @InjectMocks
    private ListingServiceImpl service;

    // --------- helpers ---------
    private User user(String username) {
        User u = new User();
        u.setUsername(username);
        u.setPublicId("user-pub-1");
        u.setUserId(1L);
        return u;
    }

    private Category category(String name, String slug) {
        Category c = new Category();
        c.setName(name);
        c.setSlug(slug);
        return c;
    }

    private Listing listing(User seller, Category category) {
        return Listing.builder()
                .seller(seller)
                .title("T")
                .description("D...............")
                .price(new BigDecimal("10.00"))
                .condition(ItemCondition.GOOD)
                .category(category)
                .status(ListingStatus.ACTIVE)
                .build();
    }

    // ====================================================================================
    // createListing
    // ====================================================================================
    @Test
    @DisplayName("createListing: saves listing with uploaded images and returns response")
    void createListing_ok_withImages() {

        // given
        String username = "lee";
        User seller = user(username);
        Category cat = category("Books", "books");

        CreateListingRequest req = CreateListingRequest.builder()
                .title("Book")
                .description("Nice book for sale")
                .price(new BigDecimal("12.34"))
                .condition(ItemCondition.GOOD)
                .slug("books")
                .latitude(41.1)
                .longitude(-87.6)
                .isNegotiable(true)
                .build();

        MultipartFile img1 = new MockMultipartFile("images", "a.jpg", "image/jpeg", new byte[]{1});
        MultipartFile img2 = new MockMultipartFile("images", "b.jpg", "image/jpeg", new byte[]{2});

        given(authValidator.validateUserByUsername(username)).willReturn(seller);
        given(categoryValidator.validateLeafCategory("books")).willReturn(cat);
        given(s3Service.upload(img1)).willReturn("https://s3/a.jpg");
        given(s3Service.upload(img2)).willReturn("https://s3/b.jpg");
        willAnswer(invocation -> invocation.getArgument(0))
                .given(listingRepository).save(any(Listing.class));

        // when
        ListingResponse res = service.createListing(username, req, List.of(img1, img2));

        // then
        assertThat(res.getTitle()).isEqualTo("Book");
        assertThat(res.getImages()).hasSize(2);
        assertThat(res.getImages()).extracting(i -> i.getImageUrl())
                .containsExactly("https://s3/a.jpg", "https://s3/b.jpg");

        then(authValidator).should().validateUserByUsername(username);
        then(categoryValidator).should().validateLeafCategory("books");
        then(s3Service).should(times(2)).upload(any(MultipartFile.class));
        then(listingRepository).should().save(any(Listing.class));
    }

    @Test
    @DisplayName("createListing: if an image upload fails, already-uploaded files are cleaned up")
    void createListing_uploadFails_rollsBackUploaded() {

        // given
        String username = "lee";
        User seller = user(username);
        Category cat = category("Books", "books");

        CreateListingRequest req = CreateListingRequest.builder()
                .title("Book").description("Nice book").price(new BigDecimal("10"))
                .condition(ItemCondition.GOOD).slug("books").latitude(1.0).longitude(2.0)
                .build();

        MultipartFile ok = new MockMultipartFile("images", "a.jpg", "image/jpeg", new byte[]{1});
        MultipartFile bad = new MockMultipartFile("images", "b.jpg", "image/jpeg", new byte[]{2});

        given(authValidator.validateUserByUsername(username)).willReturn(seller);
        given(categoryValidator.validateLeafCategory("books")).willReturn(cat);
        given(s3Service.upload(ok)).willReturn("https://s3/a.jpg");
        given(s3Service.upload(bad)).willThrow(new RuntimeException("S3 failed"));

        // when / then
        assertThatThrownBy(() -> service.createListing(username, req, List.of(ok, bad)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("S3 failed");

        then(s3Service).should().deleteByUrl("https://s3/a.jpg");
        then(listingRepository).should(never()).save(any());
    }

    // ====================================================================================
    // updateListing (images == null : keep | empty : clear | replace)
    // ====================================================================================
    @Test
    @DisplayName("updateListing: fields partially updated (no image change when images == null)")
    void updateListing_partial_noImageChange() {

        // given
        String username = "lee";
        User seller = user(username);
        Category cat = category("Books", "books");
        Listing existing = listing(seller, cat);
        existing.setPublicId("pub-1");
        // seed images
        existing.getImages().add(ListingImage.builder()
                .listing(existing).imageUrl("https://s3/old.jpg").displayOrder(0).build());

        UpdateListingRequest req = UpdateListingRequest.builder()
                .title("Edited Title")
                .price(new BigDecimal("99.99"))
                .build();

        given(authValidator.validateUserByUsername(username)).willReturn(seller);
        given(listingValidator.validateListingByPublicId("pub-1")).willReturn(existing);

        // when
        ListingResponse res = service.updateListing("pub-1", username, req, null);

        // then
        assertThat(res.getTitle()).isEqualTo("Edited Title");
        assertThat(res.getPrice()).isEqualByComparingTo("99.99");
        // images untouched
        assertThat(res.getImages()).hasSize(1);
        assertThat(res.getImages().get(0).getImageUrl()).isEqualTo("https://s3/old.jpg");

        then(s3Service).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("updateListing: images empty list => delete all old images (S3 + DB)")
    void updateListing_imagesEmpty_removesAll() {

        // given
        String username = "lee";
        User seller = user(username);
        Listing existing = listing(seller, category("Books", "books"));
        existing.setPublicId("pub-1");
        existing.getImages().add(ListingImage.builder()
                .listing(existing).imageUrl("https://s3/old1.jpg").displayOrder(0).build());
        existing.getImages().add(ListingImage.builder()
                .listing(existing).imageUrl("https://s3/old2.jpg").displayOrder(1).build());

        UpdateListingRequest req = UpdateListingRequest.builder().build();

        given(authValidator.validateUserByUsername(username)).willReturn(seller);
        given(listingValidator.validateListingByPublicId("pub-1")).willReturn(existing);

        // when
        ListingResponse res = service.updateListing("pub-1", username, req, List.of());

        // then
        then(s3Service).should().deleteFiles(List.of("https://s3/old1.jpg", "https://s3/old2.jpg"));
        assertThat(res.getImages()).isEmpty();
    }

    @Test
    @DisplayName("updateListing: replace images => deletes old and uploads new in order")
    void updateListing_replaceImages_ok() {

        // given
        String username = "lee";
        User seller = user(username);
        Listing existing = listing(seller, category("Books", "books"));
        existing.setPublicId("pub-1");
        existing.getImages().add(ListingImage.builder()
                .listing(existing).imageUrl("https://s3/old1.jpg").displayOrder(0).build());

        UpdateListingRequest req = UpdateListingRequest.builder().build();

        MultipartFile n1 = new MockMultipartFile("images", "x.jpg", "image/jpeg", new byte[]{1});
        MultipartFile n2 = new MockMultipartFile("images", "y.jpg", "image/jpeg", new byte[]{2});

        given(authValidator.validateUserByUsername(username)).willReturn(seller);
        given(listingValidator.validateListingByPublicId("pub-1")).willReturn(existing);
        willDoNothing().given(s3Service).deleteFiles(List.of("https://s3/old1.jpg"));
        given(s3Service.upload(n1)).willReturn("https://s3/x.jpg");
        given(s3Service.upload(n2)).willReturn("https://s3/y.jpg");

        // when
        ListingResponse res = service.updateListing("pub-1", username, req, List.of(n1, n2));

        // then
        assertThat(res.getImages()).extracting(i -> i.getImageUrl())
                .containsExactly("https://s3/x.jpg", "https://s3/y.jpg");
        // order check
        assertThat(res.getImages()).extracting(i -> i.getDisplayOrder())
                .containsExactly(0, 1);
    }

    @Test
    @DisplayName("updateListing: when upload fails mid-way, newly uploaded are reverted (deleteByUrl)")
    void updateListing_replaceImages_partialFail_rollbackNewOnes() {

        // given
        String username = "lee";
        User seller = user(username);
        Listing existing = listing(seller, category("Books", "books"));
        existing.setPublicId("pub-1");
        existing.getImages().add(ListingImage.builder()
                .listing(existing).imageUrl("https://s3/old1.jpg").displayOrder(0).build());

        UpdateListingRequest req = UpdateListingRequest.builder().build();

        MultipartFile ok = new MockMultipartFile("images", "ok.jpg", "image/jpeg", new byte[]{1});
        MultipartFile bad = new MockMultipartFile("images", "bad.jpg", "image/jpeg", new byte[]{2});

        given(authValidator.validateUserByUsername(username)).willReturn(seller);
        given(listingValidator.validateListingByPublicId("pub-1")).willReturn(existing);
        willDoNothing().given(s3Service).deleteFiles(List.of("https://s3/old1.jpg"));
        given(s3Service.upload(ok)).willReturn("https://s3/ok.jpg");
        given(s3Service.upload(bad)).willThrow(new RuntimeException("fail"));

        // when / then
        assertThatThrownBy(() -> service.updateListing("pub-1", username, req, List.of(ok, bad)))
                .isInstanceOf(RuntimeException.class);

        // cleanup of the already-uploaded new one
        then(s3Service).should().deleteByUrl("https://s3/ok.jpg");
    }

    // ====================================================================================
    // state transitions
    // ====================================================================================
    @Test
    @DisplayName("deleteListing: soft delete")
    void deleteListing_softDelete() {
        String username = "lee";
        User seller = user(username);
        Listing l = listing(seller, category("Books", "books"));
        l.setPublicId("pub-1");

        given(authValidator.validateUserByUsername(username)).willReturn(seller);
        given(listingValidator.validateListingByPublicId("pub-1")).willReturn(l);
        willDoNothing().given(listingValidator).validateSellerOwnership(seller, l.getSeller());

        service.deleteListing("pub-1", username);

        assertThat(l.getStatus()).isEqualTo(ListingStatus.DELETED);
        assertThat(l.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("inactivate/reactivate/markAsSold: valid transitions")
    void transitions_ok() {

        String username = "lee";
        User seller = user(username);

        // ACTIVE -> INACTIVE
        Listing l1 = listing(seller, category("C","c"));
        l1.setPublicId("pub-1");
        given(authValidator.validateUserByUsername(username)).willReturn(seller);
        given(listingValidator.validateListingByPublicId("pub-1")).willReturn(l1);
        willDoNothing().given(listingValidator).validateSellerOwnership(seller, l1.getSeller());
        service.inactivateListing("pub-1", username);
        assertThat(l1.getStatus()).isEqualTo(ListingStatus.INACTIVE);

        // INACTIVE -> ACTIVE
        Listing l2 = listing(seller, category("C","c"));
        l2.setPublicId("pub-2");
        l2.setStatus(ListingStatus.INACTIVE);
        given(listingValidator.validateListingByPublicId("pub-2")).willReturn(l2);
        service.reactivateListing("pub-2", username);
        assertThat(l2.getStatus()).isEqualTo(ListingStatus.ACTIVE);

        // ACTIVE -> SOLD
        Listing l3 = listing(seller, category("C","c"));
        l3.setPublicId("pub-3");
        given(listingValidator.validateListingByPublicId("pub-3")).willReturn(l3);
        service.markAsSold("pub-3", username);
        assertThat(l3.getStatus()).isEqualTo(ListingStatus.SOLD);
    }

    @Test
    @DisplayName("incrementViewCount: increments view counter on active listing")
    void incrementViewCount_ok() {

        Listing l = listing(user("lee"), category("C","c"));
        l.setPublicId("pub-1");

        given(listingValidator.validateActiveListingByPublicId("pub-1")).willReturn(l);

        service.incrementViewCount("pub-1");

        assertThat(l.getViewCount()).isEqualTo(1);
    }
}
