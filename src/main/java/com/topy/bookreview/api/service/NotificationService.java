package com.topy.bookreview.api.service;

import static com.topy.bookreview.global.exception.ErrorCode.SSE_EMITTER_NOT_FOUND;
import static com.topy.bookreview.redis.Topic.CHANNEL_NOTIFICATION;
import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

import com.topy.bookreview.api.domain.entity.Notification;
import com.topy.bookreview.api.domain.repository.NotificationRepository;
import com.topy.bookreview.api.dto.NotificationResponseDto;
import com.topy.bookreview.global.event.ReviewLikedEvent;
import com.topy.bookreview.global.exception.CustomException;
import com.topy.bookreview.global.exception.ErrorCode;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final SseEmitterRepository sseEmitterRepository;

  private static final String NOTIFICATION_EVENT_NAME = "notification_event";


  @Transactional
  public NotificationResponseDto save(ReviewLikedEvent event) {
    Notification savedNotification = notificationRepository.save(Notification.of(event));
    return NotificationResponseDto.fromEntity(savedNotification);
  }

  public void sendRealtime(String channel, NotificationResponseDto data) {
    log.info("sendRealtime 호출");
    String userId = channel.substring(CHANNEL_NOTIFICATION.getPrefix().length());
    SseEmitter sseEmitter = sseEmitterRepository.get(userId)
        .orElseThrow(() -> new CustomException(SSE_EMITTER_NOT_FOUND));

    try {
      sseEmitter.send(event().id(userId).name(NOTIFICATION_EVENT_NAME).data(data));
    } catch (IOException e) {
      sseEmitterRepository.deleteById(userId);
      throw new CustomException(ErrorCode.NOTIFICATION_SEND_ERROR);
    }
  }
}
