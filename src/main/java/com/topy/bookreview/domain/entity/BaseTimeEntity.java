package com.topy.bookreview.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdDateTIme;

  @LastModifiedDate
  @Column(nullable = true)
  private LocalDateTime modifiedDateTIme;

}
