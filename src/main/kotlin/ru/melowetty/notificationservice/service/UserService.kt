package ru.melowetty.notificationservice.service

import java.util.UUID
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.requiredBody
import ru.melowetty.notificationservice.exception.NotRetryableException
import ru.melowetty.notificationservice.model.user.UserInfo

@Service
class UserService(
    @Qualifier("userServiceRestClient")
    private val restClient: RestClient
) {
    @Retryable(
        include = [RuntimeException::class],
        exclude = [NotRetryableException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0)
    )
    @Cacheable(cacheNames = [USER_INFO_CACHE])
    fun getUserInfo(id: UUID): UserInfo {
        val response = restClient.get().uri("user/{id}", id)
            .retrieve()
            .onStatus({ it == HttpStatus.NOT_FOUND }) { _, _ ->
                throw NotRetryableException("User with id $id not found")
            }
            .requiredBody<UserServiceResponse>()

        return UserInfo(
            email = response.email,
            telegramId = response.telegramId
        )
    }

    data class UserServiceResponse(
        val id: UUID,
        val email: String,
        val telegramId: Long
    )

    companion object {
        private const val USER_INFO_CACHE = "user-info"
    }
}
