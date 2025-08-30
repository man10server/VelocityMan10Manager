package red.man10.velocity.manager.database.models

import org.ktorm.entity.Entity
import java.time.LocalDateTime
import java.util.UUID

interface ScoreLog: Entity<ScoreLog> {
    companion object : Entity.Factory<ScoreLog>()
    var id: Int
    var player: String
    var uuid: UUID
    var score: Int
    var note: String?
    var issuer: String?
    var nowScore: Int
    var date: LocalDateTime?
}