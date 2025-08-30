package red.man10.velocity.manager.database.models

import org.ktorm.entity.Entity
import java.time.LocalDateTime

interface BanIP: Entity<BanIP> {
    companion object : Entity.Factory<BanIP>()
    var id: Int
    var ipAddress: String
    var date: LocalDateTime?
    var reason: String?
}