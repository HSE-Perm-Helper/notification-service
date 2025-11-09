package ru.melowetty.notificationservice.service

import java.util.UUID
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.requiredBody
import org.springframework.web.util.UriComponentsBuilder
import ru.melowetty.notificationservice.exception.NotRetryableException
import ru.melowetty.notificationservice.model.user.UserInfo

@Service
class UserService(
    @Qualifier("userServiceRestClient")
    private val restClient: RestClient,
    private val cacheManager: CacheManager,
) {
    @Retryable(
        include = [RuntimeException::class],
        exclude = [NotRetryableException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0)
    )
    fun getUsersInfo(ids: List<UUID>): List<UserInfo> {
        val cache = cacheManager.getCache(USER_INFO_CACHE)!!
        val result = mutableListOf<UserInfo>()
        val idsToFetch = mutableListOf<UUID>()

        for (id in ids) {
            val cached = cache.get(id, UserInfo::class.java)
            if (cached != null) {
                result.add(cached)
            } else {
                idsToFetch.add(id)
            }
        }

        if (idsToFetch.isNotEmpty()) {
            val fetched = fetchUsersInfo(idsToFetch)
            fetched.forEach { userInfo ->
                cache.put(userInfo.id, userInfo)
            }
            result.addAll(fetched)
        }

        return result
    }

    @Retryable(
        include = [RuntimeException::class],
        exclude = [NotRetryableException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0)
    )
    @Cacheable(cacheNames = [USER_INFO_CACHE])
    fun getUserInfo(id: UUID): UserInfo {
        val response = restClient.get().uri("v3/users/{id}", id)
            .retrieve()
            .onStatus({ it == HttpStatus.NOT_FOUND }) { _, _ ->
                throw NotRetryableException("User with id $id not found")
            }
            .requiredBody<UserInfoResponse>()

        return UserInfo(
            id = response.id,
            email = response.email,
            telegramId = response.telegramId
        )
    }

    private fun fetchUsersInfo(ids: List<UUID>): List<UserInfo> {
        val uri = UriComponentsBuilder.fromPath("v3/users")
            .queryParam("ids", ids)
            .build()
            .toUriString()
        val response = restClient.get().uri(uri)
            .retrieve()
            .onStatus({ it.is4xxClientError }) { _, _ ->
                throw NotRetryableException("Users cannot be found because of client error")
            }
            .requiredBody<List<UserInfoResponse>>()

        return response.map { user ->
            UserInfo(
                id = user.id,
                email = user.email,
                telegramId = user.telegramId
            )
        }
    }

    data class UserInfoResponse(
        val id: UUID,
        val email: String,
        val telegramId: Long
    )

    companion object {
        private const val USER_INFO_CACHE = "user-info"
    }
}
