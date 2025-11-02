CREATE TABLE `player_data` (
   `id` INT NOT NULL AUTO_INCREMENT,
   `uuid` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
   `player` VARCHAR(16) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
   `freeze_until` DATETIME NULL DEFAULT NULL,
   `mute_until` DATETIME NULL DEFAULT NULL,
   `jail_until` DATETIME NULL DEFAULT NULL,
   `ban_until` DATETIME NULL DEFAULT NULL,
   `ban_message_override` TEXT NULL COLLATE 'utf8mb4_0900_ai_ci',
   `msb_until` DATETIME NULL DEFAULT NULL,
   `score` INT NULL DEFAULT '0',
   PRIMARY KEY (`id`) USING BTREE,
   INDEX `player_data_uuid_mcid_index` (`uuid`, `player`) USING BTREE
)
COMMENT='プレイヤーデータ'
COLLATE='utf8mb4_0900_ai_ci'
ENGINE=InnoDB
;
