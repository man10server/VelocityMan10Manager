package red.man10.velocity.manager.database.models

import org.ktorm.entity.Entity
import java.time.LocalDateTime
import java.util.UUID

interface MessageLog: Entity<MessageLog> {
    companion object : Entity.Factory<MessageLog>()
    var id: Int
    var uuid: UUID?
    var player: String?
    var message: String?
    var server: String?
    var date: LocalDateTime?
}