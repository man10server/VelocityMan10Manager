package red.man10.velocity.manager.database.models

import org.ktorm.entity.Entity
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

interface PlayerData: Entity<PlayerData> {
    companion object : Entity.Factory<PlayerData>()
    var id: Int
    var uuid: UUID
    var player: String
    var freezeUntil: LocalDateTime?
    var muteUntil: LocalDateTime?
    var jailUntil: LocalDateTime?
    var banUntil: LocalDateTime?
    var banMessageOverride: String?
    var msbUntil: LocalDateTime?
    var score: Int

    fun isFrozen(): Boolean {
        return freezeUntil?.isAfter(LocalDateTime.now()) ?: false
    }

    fun isMuted(): Boolean {
        return muteUntil?.isAfter(LocalDateTime.now()) ?: false
    }

    fun isJailed(): Boolean {
        return jailUntil?.isAfter(LocalDateTime.now()) ?: false
    }

    fun isBanned(): Boolean {
        return banUntil?.isAfter(LocalDateTime.now()) ?: false
    }

    fun isMSB(): Boolean {
        return msbUntil?.isAfter(LocalDateTime.now()) ?: false
    }

    fun resetFreeze() {
        freezeUntil = null
        flushChanges()
    }

    fun resetMute() {
        muteUntil = null
        flushChanges()
    }

    fun resetJail() {
        jailUntil = null
        flushChanges()
    }

    fun resetBan() {
        banUntil = null
        banMessageOverride = null
        flushChanges()
    }

    fun resetMSB() {
        msbUntil = null
        flushChanges()
    }

    private fun createOrAddTime(dateTime: LocalDateTime?, timeToAdd: Long): LocalDateTime {
        val nonNullDateTime = dateTime ?: LocalDateTime.now()
        return nonNullDateTime.plusSeconds(timeToAdd)
    }

    fun addFreezeTime(timeToAdd: Long) {
        freezeUntil = createOrAddTime(freezeUntil, timeToAdd)
        flushChanges()
    }

    fun addMuteTime(timeToAdd: Long) {
        muteUntil = createOrAddTime(muteUntil, timeToAdd)
        flushChanges()
    }

    fun addJailTime(timeToAdd: Long) {
        jailUntil = createOrAddTime(jailUntil, timeToAdd)
        flushChanges()
    }

    fun addBanTime(timeToAdd: Long) {
        banUntil = createOrAddTime(banUntil, timeToAdd)
        banMessageOverride = null
        flushChanges()
    }

    fun addMSBTime(timeToAdd: Long) {
        msbUntil = createOrAddTime(msbUntil, timeToAdd)
        flushChanges()
    }
}