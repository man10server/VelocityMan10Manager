CREATE TABLE `score_log` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `player` VARCHAR(16) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `uuid` VARCHAR(36) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `score` INT NULL DEFAULT NULL,
    `note` VARCHAR(256) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `issuer` VARCHAR(16) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `now_score` INT NULL DEFAULT NULL,
    `date` DATETIME NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `score_log_mcid_uuid_index` (`player`, `uuid`) USING BTREE
)
COLLATE='utf8mb4_0900_ai_ci'
ENGINE=InnoDB
;
