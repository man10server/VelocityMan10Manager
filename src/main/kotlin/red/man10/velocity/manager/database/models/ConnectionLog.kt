package red.man10.velocity.manager.database.models

import org.ktorm.entity.Entity
import java.time.LocalDateTime
import java.util.UUID

interface ConnectionLog: Entity<ConnectionLog> {
    companion object : Entity.Factory<ConnectionLog>()
    val id: Int
    var uuid: UUID
    var player: String
    var server: String
    var connectedTime: LocalDateTime?
    var disconnectedTime: LocalDateTime?
    var connectionSeconds: Int?
    var ip: String
    var port: Int
}