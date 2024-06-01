package com.example.DiningReviewAPI.repository;

import com.example.DiningReviewAPI.model.Entities.RestaurantEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Restaurant Repository, which extends CRUD functionality by more
 * specific query methods.
 *
 * @author Jonathan El Jusup
 */
public interface RestaurantRepository extends CrudRepository<RestaurantEntity, Long> {
    Integer countAllByNameAndZipcode(String name, Integer zipcode);

    List<RestaurantEntity> findAllByZipcodeAndRatingPeanutNotNullOrderByOverallRatingDesc(Integer zipcode);
    List<RestaurantEntity> findAllByZipcodeAndRatingEggNotNullOrderByOverallRatingDesc(Integer zipcode);
    List<RestaurantEntity> findAllByZipcodeAndRatingDiaryNotNullOrderByOverallRatingDesc(Integer zipcode);
}
