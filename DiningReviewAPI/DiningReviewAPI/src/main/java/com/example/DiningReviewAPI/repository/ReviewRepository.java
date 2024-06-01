package com.example.DiningReviewAPI.repository;

import com.example.DiningReviewAPI.model.Entities.ReviewEntity;
import com.example.DiningReviewAPI.model.ReviewStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Review Repository, which extends CRUD functionality by more
 * specific query methods.
 *
 * @author Jonathan El Jusup
 */
public interface ReviewRepository extends CrudRepository<ReviewEntity, Long> {
    public List<ReviewEntity> findAllByStatusEquals(ReviewStatus status);
    public List<ReviewEntity> findAllByRestaurantIDAndStatusEquals(Long id, ReviewStatus status);
}
