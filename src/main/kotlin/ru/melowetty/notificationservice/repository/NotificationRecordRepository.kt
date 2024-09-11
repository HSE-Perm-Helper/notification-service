package ru.melowetty.notificationservice.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.melowetty.notificationservice.domain.entity.NotificationRecord
import java.util.*

interface NotificationRecordRepository : MongoRepository<NotificationRecord, UUID>