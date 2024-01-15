package com.topy.bookreview.redis.listener;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.topy.bookreview.api.dto.NotificationResponseDto;
import com.topy.bookreview.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSubscriber implements MessageListener {

  private final NotificationService notificationService;
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    log.info("NotificationSubscriber - onMessage 호출");

    String channel = new String(message.getChannel(), UTF_8);

    RedisSerializer<?> serializer = redisTemplate.getValueSerializer();
    NotificationResponseDto data = (NotificationResponseDto) serializer.deserialize(
        message.getBody());

    notificationService.sendRealtime(channel, data);
  }
}
