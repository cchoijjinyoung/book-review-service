package com.topy.bookreview.api.domain.repository;

import com.topy.bookreview.api.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  Slice<Review> findByIsbn(String isbn, Pageable pageable);
}
