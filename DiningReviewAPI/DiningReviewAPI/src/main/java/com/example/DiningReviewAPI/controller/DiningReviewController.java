package com.example.DiningReviewAPI.controller;

import com.example.DiningReviewAPI.model.Allergies;
import com.example.DiningReviewAPI.model.Entities.RestaurantEntity;
import com.example.DiningReviewAPI.model.Entities.ReviewEntity;
import com.example.DiningReviewAPI.model.Entities.UserEntity;
import com.example.DiningReviewAPI.model.ReviewStatus;
import com.example.DiningReviewAPI.repository.RestaurantRepository;
import com.example.DiningReviewAPI.repository.ReviewRepository;
import com.example.DiningReviewAPI.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

/**
 * Dining Review Controller. Defines endpoints the user can interact with.
 * Functionality such as reading, creating, and modifying users, reviews
 * and restaurants are included in this controller.
 *
 * @author Jonathan El Jusup
 */
@RestController
public class DiningReviewController {

    //Repository for handling users in database
    private final UserRepository userRepository;

    //Repository for handling Restaurants in database
    private final RestaurantRepository restaurantRepository;

    //Repository for handling Reviews in database
    private final ReviewRepository reviewRepository;


    /**
     * Constructor.
     *
     * @param userRepository repository for handling users in database
     * @param restaurantRepository repository for handling restaurants in database
     * @param reviewRepository repository for handling reviews in database
     */
    public DiningReviewController(final UserRepository userRepository,
                                  final RestaurantRepository restaurantRepository,
                                  final ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Creates and saves new user in database. An already existing user cannot
     * be recreated, but may be modified (Handled by another endpoint).
     *
     * @param user New user to create
     * @return Newly created user
     */
    @PostMapping("/users")
    public ResponseEntity<UserEntity> registerUser(@RequestBody UserEntity user) {

        Optional<UserEntity> optionalUserEntity = userRepository.findByName(user.getName());
        if (optionalUserEntity.isPresent()) {
            //User already exists
            System.err.println("[REGISTER_USER()] User already exists");
            return ResponseEntity.badRequest().build();
        }

        UserEntity savedUser = userRepository.save(user);
        URI location = URI.create("/users/" + savedUser.getName());
        return ResponseEntity.created(location).build();
    }

    /**
     * Gets all registered users in database and returns them.
     * No additional Error Handling happens here.
     *
     * @return all users in database
     */
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Gets a specified user by name. A user may always be referenced by its unique name
     * instead of its unique database id.
     *
     * @param name user name
     * @return user in database with that name
     */
    @GetMapping("/users/{name}")
    public ResponseEntity<UserEntity> getUserByName(@PathVariable(name = "name") String name) {
        Optional<UserEntity> optionalUser = userRepository.findByName(name);
        if (optionalUser.isEmpty()) {
            System.err.println("[GET_USER_BY_NAME()] User doesn't exist");
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(optionalUser.get());
    }

    /**
     * Modifies existing user. Unique username is immutable. Other properties may be changed.
     *
     * @param name user name
     * @param user modified user data
     * @return modified user
     */
    @PutMapping("/users/{name}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable(name = "name") String name, @RequestBody UserEntity user) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByName(name);
        if (optionalUserEntity.isEmpty()) {
            System.err.println("[UPDATE_USER()] User does not exist!");
            return ResponseEntity.notFound().build();
        }

        UserEntity userToUpdate = optionalUserEntity.get();

        //Update user properties (Non-null values are guaranteed)
        //Name cannot be changed
        if (!userToUpdate.getName().equals(user.getName())) {
            System.err.println("[UPDATE_USER()|WARNING] User name cannot be changed, other properties are mutable");
        }

        userToUpdate.setCity(user.getCity());
        userToUpdate.setState(user.getState());
        userToUpdate.setZipcode(user.getZipcode());
        userToUpdate.setHas_peanut_allergy(user.getHas_peanut_allergy());
        userToUpdate.setHas_egg_allergy(user.getHas_egg_allergy());
        userToUpdate.setHas_diary_allergy(user.getHas_diary_allergy());

        UserEntity savedUser = userRepository.save(userToUpdate);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * Gets review by its unique database ID.
     *
     * @param id review ID in database
     * @return review in database
     */
    @GetMapping("/reviews/{id}")
    public ResponseEntity<ReviewEntity> getReviewById(@PathVariable(name = "id") Long id) {
        Optional<ReviewEntity> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isEmpty()) {
            //Review with given ID doesn't exist
            System.err.println("[GET_REVIEW_BY_ID()] Review doesn't exist");
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(optionalReview.get());
    }

    /**
     * User submits a review. A newly submitted review get a pending status and must be validated
     * by an admit before actually being taken into account in rating the restaurant.
     *
     * @param name user, that wants to submit a review for a restaurant
     * @param review review
     * @return newly created review
     */
    @PostMapping("/users/{name}/review")
    public ResponseEntity<ReviewEntity> submitReview(@PathVariable(name = "name") String name, @RequestBody ReviewEntity review) {
        Optional<UserEntity> optionalUser = userRepository.findByName(name);
        if (optionalUser.isEmpty()) {
            //User doesn't exist, therefore cannot submit a review
            System.err.println("[SUBMIT_REVIEW()] User doesn't exist");
            return ResponseEntity.badRequest().build();
        }

        if (!optionalUser.get().getName().equals(review.getAuthor())) {
            //User is not review author, therefore cannot submit a review
            System.err.println("[SUBMIT_REVIEW()] Registered user is not author");
            return ResponseEntity.badRequest().build();
        }

        if (restaurantRepository.findById(review.getRestaurantID()).isEmpty()) {
            //Restaurant mentioned in review doesn't exist
            System.err.println("[SUBMIT_REVIEW()] Restaurant in review doesn't exist");
            return ResponseEntity.badRequest().build();
        }

        review.setStatus(ReviewStatus.PENDING);
        ReviewEntity savedReview = reviewRepository.save(review);
        URI location = URI.create("/reviews/" + savedReview.getId());
        return ResponseEntity.created(location).build();
    }

    /**
     * An administrator can view all submitted and pending reviews.
     * No further error handling here.
     *
     * @return all pending reviews in database
     */
    @GetMapping("/admin/reviews")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<ReviewEntity> getAllReviews() {
        return reviewRepository.findAllByStatusEquals(ReviewStatus.PENDING);
    }

    /**
     * An admit can approve or reject a specified pending review.
     *
     * @param reviewID review ID
     * @param approve Flag, whether to approve or reject the review
     * @return Approved or rejected review
     */
    @PutMapping("/admin/reviews/{id}/status/{approve}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ReviewEntity> managePendingReview(@PathVariable(name = "id") Long reviewID, @PathVariable(name = "approve") Boolean approve) {
        System.out.println("HERE");
        Optional<ReviewEntity> optionalReview = reviewRepository.findById(reviewID);
        if (optionalReview.isEmpty()) {
            System.err.println("[MANAGE_PENDING_REVIEW()] Review doesn't exist");
            return ResponseEntity.notFound().build();
        }

        if (!optionalReview.get().getStatus().equals(ReviewStatus.PENDING)) {
            System.out.println("[MANAGE_PENDING_REVIEW()] Review is not pending anymore");
            return ResponseEntity.badRequest().build();
        }

        ReviewEntity reviewToApprove = optionalReview.get();
        reviewToApprove.setStatus(approve ? ReviewStatus.APPROVED : ReviewStatus.REJECTED);
        ReviewEntity savedReviewToApprove = reviewRepository.save(reviewToApprove);

        if (approve) {
            updateRestaurantRating(reviewToApprove.getRestaurantID());
        }

        return ResponseEntity.ok(savedReviewToApprove);
    }

    /**
     * Creates a new database entry for a new restaurant.
     *
     * @param restaurant restaurant to create
     * @return created restaurant
     */
    @PostMapping("/restaurant")
    public ResponseEntity<RestaurantEntity> createRestaurant(@RequestBody RestaurantEntity restaurant) {
        if (restaurantRepository.countAllByNameAndZipcode(restaurant.getName(), restaurant.getZipcode()) != 0) {
            //There already exists one restaurant with the same name and zipcode
            System.err.println("[CREATE_RESTAURANT()] Restaurant with same name and zipcode already exists");
            return ResponseEntity.badRequest().build();
        }

        RestaurantEntity savedRestaurant = restaurantRepository.save(restaurant);
        URI location = URI.create("/restaurant/" + savedRestaurant.getId());
        return ResponseEntity.created(location).build();

    }

    /**
     * Gets restaurant by its own unique ID in database.
     *
     * @param id restaurant ID
     * @return restaurant in database
     */
    @GetMapping("/restaurant/{id}")
    public ResponseEntity<RestaurantEntity> getRestaurantById(@PathVariable Long id) {
        Optional<RestaurantEntity> optionalRestaurant = restaurantRepository.findById(id);
        if (optionalRestaurant.isEmpty()) {
            //Restaurant with given ID doesn't exist
            System.err.println("[GET_RESTAURANT()] Restaurant doesn't exist");
            return ResponseEntity.notFound().build();
        }

        //May be necessary, if rating is not updated correctly
        updateRestaurantRating(id);

        RestaurantEntity restaurant = optionalRestaurant.get();
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping("/restaurant/{zipcode}/allergy/{allergy}")
    public Iterable<RestaurantEntity> getAllRestaurantsByZipcodeAndAllergy(@PathVariable(name = "zipcode") Integer zipcode, @PathVariable(name = "allergy") String allergy) {
        if (zipcode == null) {
            System.err.println("[GET_ALL_RESTAURANTS_BY_ZIPCODE_AND_ALLERGY] Invalid zipcode");
            return null;
        }


        switch (allergy) {
            case "Peanut":
                return restaurantRepository.findAllByZipcodeAndRatingPeanutNotNullOrderByOverallRatingDesc(zipcode);
            case "Egg":
                return restaurantRepository.findAllByZipcodeAndRatingEggNotNullOrderByOverallRatingDesc(zipcode);
            case "Diary":
                return restaurantRepository.findAllByZipcodeAndRatingDiaryNotNullOrderByOverallRatingDesc(zipcode);
            default:
                System.err.println("[GET_ALL_RESTAURANTS_BY_ZIPCODE_AND_ALLERGY()] Invalid specified allergy");
                break;
        }


        return null;
    }

    /**
     * Recalculates the restaurants ratings. Should be called, when a new review was approved.
     * Its generally a good idea to recalculate the ratings again when getting the restaurant
     * in case of errors.
     *
     * @param id restaurant ID
     */
    private void updateRestaurantRating(Long id) {
        Optional<RestaurantEntity> optionalRestaurant = restaurantRepository.findById(id);
        if (optionalRestaurant.isEmpty()) {
            return;
        }

        RestaurantEntity restaurant = optionalRestaurant.get();

        //Recalculate individual Ratings by approved reviews
        List<ReviewEntity> reviews = reviewRepository.findAllByRestaurantIDAndStatusEquals(id, ReviewStatus.APPROVED);
        double peanutRating = 0.0, eggRating = 0.0, diaryRating = 0.0;
        int peanutCount = 0, eggCount = 0, diaryCount = 0;
        for (ReviewEntity review : reviews) {
            if (review.getRatingPeanut() != null) {
                peanutRating += review.getRatingPeanut();
                peanutCount++;
            }

            if (review.getRatingEgg() != null) {
                eggRating += review.getRatingEgg();
                eggCount++;
            }

            if (review.getRatingDiary() != null) {
                diaryRating += review.getRatingDiary();
                diaryCount++;
            }
        }

        double newPeanutRating = peanutRating / (double) (peanutCount == 0 ? 1 : peanutCount);
        double newEggRating = eggRating / (double) (eggCount == 0 ? 1 : eggCount);
        double newDiaryRating = diaryRating / (double) (diaryCount == 0 ? 1 : diaryCount);

        DecimalFormat df = new DecimalFormat("0.00");

        //Average individual ratings
        restaurant.setRatingPeanut(Double.parseDouble(df.format(newPeanutRating).replace(",", ".")));
        restaurant.setRatingEgg(Double.parseDouble(df.format(newEggRating).replace(",", ".")));
        restaurant.setRatingDiary(Double.parseDouble(df.format(newDiaryRating).replace(",", ".")));

        //Calculate overall rating
        double divisor = newPeanutRating + newEggRating + newDiaryRating;
        double denominator = (peanutCount == 0 ? 0 : 1) + (eggCount == 0 ? 0 : 1) + (diaryCount == 0 ? 0 : 1);
        double newOverallRating = divisor / denominator;
        restaurant.setOverallRating(Double.parseDouble(df.format(newOverallRating).replace(",", ".")));
        restaurantRepository.save(restaurant);
    }
}
