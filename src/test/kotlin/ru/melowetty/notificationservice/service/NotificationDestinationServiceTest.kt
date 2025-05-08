package ru.melowetty.notificationservice.service

import java.util.UUID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import ru.melowetty.notificationservice.annotation.notification.email.EmailNotification
import ru.melowetty.notificationservice.annotation.notification.telegram.TelegramNotification
import ru.melowetty.notificationservice.model.user.UserInfo

@ExtendWith(MockitoExtension::class)
class NotificationDestinationServiceTest {
    @InjectMocks
    private lateinit var notificationDestinationService: NotificationDestinationService

    @Mock
    private lateinit var userService: UserService

    @Test
    fun `test get destinations by regular notification`() {
        @TelegramNotification("test")
        @EmailNotification("test")
        class RegularNotification

        val notification = RegularNotification()
        val userId = UUID.randomUUID()

        val email = "test@mail.ru"
        val telegramId = 123456789L

        Mockito.`when`(userService.getUserInfo(userId)).thenReturn(UserInfo(email, telegramId))

        val result = notificationDestinationService.getNotificationDestinations(notification, userId.toString())

        Assertions.assertEquals(2, result.size)

        Assertions.assertTrue(result.containsKey(EmailNotification::class.java))
        Assertions.assertEquals(email, result[EmailNotification::class.java])

        Assertions.assertTrue(result.containsKey(TelegramNotification::class.java))
        Assertions.assertEquals(telegramId, result[TelegramNotification::class.java])
    }
}