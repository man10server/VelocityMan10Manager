package red.man10.velocity.manager.database.models

import org.ktorm.entity.Entity
import java.time.LocalDateTime
import java.util.UUID

interface CommandLog: Entity<CommandLog> {
    companion object : Entity.Factory<CommandLog>()
    var id: Int
    var uuid: UUID
    var player: String?
    var command: String?
    var server: String?
    var date: LocalDateTime?
}