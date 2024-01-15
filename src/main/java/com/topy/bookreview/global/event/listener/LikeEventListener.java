package com.topy.bookreview.global.event.listener;

import static com.topy.bookreview.redis.Topic.CHANNEL_NOTIFICATION;

import com.topy.bookreview.api.dto.NotificationResponseDto;
import com.topy.bookreview.api.service.NotificationService;
import com.topy.bookreview.api.service.SseEmitterRepository;
import com.topy.bookreview.global.event.ReviewLikedEvent;
import com.topy.bookreview.redis.RedisManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventListener {

  private final NotificationService notificationService;
  private final RedisManager redisManager;
  private final SseEmitterRepository sseEmitterRepository;

  @EventListener
  public void handleReviewLikedEvent(ReviewLikedEvent event) {
    log.info("handleReviewLikedEvent 호출");
    NotificationResponseDto message = notificationService.save(event);

    String receiverId = String.valueOf(event.getReceiver().getId());

    if (sseEmitterRepository.get((receiverId)).isPresent()) {
      String channelName = CHANNEL_NOTIFICATION.getPrefix() + receiverId;
      redisManager.publish(channelName, message);
      log.info("Redis message publish - 채널 이름 = {}", channelName);
    }
  }
}
