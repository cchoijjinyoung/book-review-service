package com.topy.bookreview.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "review")
public class Review extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @Column(nullable = false)
  private Member member;

  @Column(nullable = false)
  private String isbn;

  @Column(nullable = false)
  private int rating;

  @Column(nullable = false)
  private String text;

  @Column(nullable = false)
  private long likeCount;
}
