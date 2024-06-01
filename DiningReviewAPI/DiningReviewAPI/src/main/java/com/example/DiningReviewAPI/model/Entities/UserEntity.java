package com.example.DiningReviewAPI.model.Entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * User Entity for database model.
 *
 * @author Jonathan El Jusup
 */
@Data
@NoArgsConstructor
@Entity
public class UserEntity {

    @Id
    @GeneratedValue
    private Long Id;

    @NonNull private String name;
    @NonNull private String city;
    @NonNull private String state;
    @NonNull private Integer zipcode;
    @NonNull private Boolean has_peanut_allergy;
    @NonNull private Boolean has_egg_allergy;
    @NonNull private Boolean has_diary_allergy;
}
