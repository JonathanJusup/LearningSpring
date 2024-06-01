package com.example.DiningReviewAPI.repository;

import com.example.DiningReviewAPI.model.Entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * User Repository, which extends CRUD functionality by more
 * specific query methods.
 *
 * @author Jonathan El Jusup
 */
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    public Optional<UserEntity> findByName(String name);
}
