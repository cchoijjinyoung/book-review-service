package com.topy.bookreview.api.service;

import static com.topy.bookreview.redis.Topic.CHANNEL_NOTIFICATION;
import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

import com.topy.bookreview.redis.Topic;
import com.topy.bookreview.redis.listener.NotificationSubscriber;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

  private final SseEmitterRepository sseEmitterRepository;

  private final RedisMessageListenerContainer messageListenerContainer;

  private final NotificationSubscriber notificationSubscriber;

  private static final long SSE_EMITTER_TIME_OUT = 1000L * 60 * 60 * 3;

  @Transactional
  public SseEmitter connect(Long userId) {
    String key = String.valueOf(userId);
    SseEmitter sseEmitter = sseEmitterRepository.save(key, new SseEmitter(SSE_EMITTER_TIME_OUT));

    sseEmitter.onCompletion(() -> {
      cancelSubscribeRedisChannel(userId);
      sseEmitterRepository.deleteById(key);
    });
    sseEmitter.onTimeout(sseEmitter::complete);
    sseEmitter.onError((throwable) -> {
      log.error("SSE emitter onError", throwable);
      sseEmitter.complete();
    });

    // 더미 이벤트 발송
    try {
      sseEmitter.send(event().name("Connect 성공"));
      subscribeRedisChannel(userId);
    } catch (IOException e) {
      log.error("SSE dummy send IOException 발생", e);
    }

    return sseEmitter;
  }

  private void subscribeRedisChannel(Long userId) {
    String channelName = CHANNEL_NOTIFICATION.getPrefix() + userId;
    messageListenerContainer.addMessageListener(notificationSubscriber,
        new ChannelTopic(channelName));
    log.info("redis pub/sub 알림 채널 구독 ={}", channelName);
  }

  private void cancelSubscribeRedisChannel(Long userId) {
    messageListenerContainer.removeMessageListener(notificationSubscriber,
        new ChannelTopic(Topic.CHANNEL_NOTIFICATION.getPrefix() + userId));
    log.info("redis pub/sub 유저 알림 채널 구독 취소");
  }
}
