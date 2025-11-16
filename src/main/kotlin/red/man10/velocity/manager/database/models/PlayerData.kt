package red.man10.velocity.manager.database.models

import org.ktorm.entity.Entity
import java.time.LocalDateTime
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

    private fun setOrExtendExpiration(expiration: LocalDateTime?, secondsToAdd: Long): LocalDateTime {
        val now = LocalDateTime.now()
        val base = expiration
            ?.takeIf { now.isBefore(it) }
            ?: now
        return base.plusSeconds(secondsToAdd)
    }

    fun addFreezeTime(secondsToAdd: Long) {
        freezeUntil = setOrExtendExpiration(freezeUntil, secondsToAdd)
        flushChanges()
    }

    fun addMuteTime(secondsToAdd: Long) {
        muteUntil = setOrExtendExpiration(muteUntil, secondsToAdd)
        flushChanges()
    }

    fun addJailTime(secondsToAdd: Long) {
        jailUntil = setOrExtendExpiration(jailUntil, secondsToAdd)
        flushChanges()
    }

    fun addBanTime(secondsToAdd: Long) {
        banUntil = setOrExtendExpiration(banUntil, secondsToAdd)
        banMessageOverride = null
        flushChanges()
    }

    fun addMSBTime(secondsToAdd: Long) {
        msbUntil = setOrExtendExpiration(msbUntil, secondsToAdd)
        flushChanges()
    }
}