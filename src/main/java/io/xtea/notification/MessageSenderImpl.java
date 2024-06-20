package io.xtea.notification;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-04-15 22:22
 */
@Service
@Slf4j
public class MessageSenderImpl implements MessageSender {

    TelegramBot bot = new TelegramBot("494218211:AAHz9v1L3P6l6ce5DVq89Fakwrr5LMCBCZs");

    long chatId = 433388442;

    @EventListener(ApplicationReadyEvent.class)
    public void initBot() {
//        log.info("register telegram Bot ");
//        // Register for updates
//        bot.setUpdatesListener(updates -> {
//            // ... process updates
//            // return id of last processed update or confirm them all
//            return UpdatesListener.CONFIRMED_UPDATES_ALL;
//        });
    }

    @Override
    public void send(String msg) {
        SendResponse response = bot.execute(new SendMessage(chatId, msg));
        log.info("send message response is {}", response);
    }
}
