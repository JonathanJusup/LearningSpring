package com.example.DiningReviewAPI.model.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Restaurant Entity for database model.
 *
 * @author Jonathan El Jusup
 */
@Data
@Entity
@NoArgsConstructor
public class RestaurantEntity {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull private String name;
    @NonNull private Integer zipcode;

    private Double ratingPeanut = null;
    private Double ratingEgg = null;
    private Double ratingDiary = null;
    private Double overallRating = null;
}
