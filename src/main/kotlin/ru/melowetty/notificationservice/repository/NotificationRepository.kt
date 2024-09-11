package ru.melowetty.notificationservice.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ru.melowetty.notificationservice.domain.entity.Notification
import java.util.*

@Repository
interface NotificationRepository : MongoRepository<Notification, UUID>