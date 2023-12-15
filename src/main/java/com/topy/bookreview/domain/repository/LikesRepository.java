package com.topy.bookreview.domain.repository;

import com.topy.bookreview.domain.entity.Likes;
import com.topy.bookreview.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

}
