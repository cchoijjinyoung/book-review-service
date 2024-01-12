package com.topy.bookreview.api.domain.repository;

import com.topy.bookreview.api.domain.entity.Like;
import com.topy.bookreview.api.domain.entity.Member;
import com.topy.bookreview.api.domain.entity.Review;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

  Optional<Like> findByMemberAndReview(Member member, Review review);

}
