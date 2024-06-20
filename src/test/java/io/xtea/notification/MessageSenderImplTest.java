package io.xtea.notification;

import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-04-15 22:30
 */
public class MessageSenderImplTest {


    MessageSenderImpl sender = new MessageSenderImpl();

    @BeforeEach
    void setUp() {
        sender.initBot();
    }

    @Test
    void send() {
        sender.send("Build test succeed.");
    }
}