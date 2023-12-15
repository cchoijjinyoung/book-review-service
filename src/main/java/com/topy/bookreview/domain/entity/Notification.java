package com.topy.bookreview.domain.entity;

import com.topy.bookreview.domain.entity.type.NotificationType;
import com.topy.bookreview.domain.entity.type.TargetType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Member receiver;

  @ManyToOne
  private Member caller;

  private String content;

  @Enumerated(EnumType.STRING)
  private NotificationType notificationType;

  private LocalDateTime readDate;

  @Enumerated(EnumType.STRING)
  private TargetType targetType;

  private Long targetId;
}
