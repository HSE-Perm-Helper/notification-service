package ru.melowetty.notificationservice.service

import java.util.UUID
import org.springframework.stereotype.Service
import ru.melowetty.notificationservice.model.user.UserInfo

@Service
class UserService {
    fun getUserInfo(id: UUID): UserInfo {
        return UserInfo(
            email = "melowetty@mail.ru",
            telegramId = 123
        )
    }
}
