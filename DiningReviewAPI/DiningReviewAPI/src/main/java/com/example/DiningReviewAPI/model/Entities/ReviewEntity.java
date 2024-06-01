package com.example.DiningReviewAPI.model.Entities;


import com.example.DiningReviewAPI.model.ReviewStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Review Entity for database model.
 *
 * @author Jonathan El Jusup
 */
@Data
@NoArgsConstructor
@Entity
public class ReviewEntity {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull private String author;
    @NonNull private Long restaurantID;
    private String comment;

    private Integer ratingPeanut;
    private Integer ratingEgg;
    private Integer ratingDiary;

    private ReviewStatus status;

}

