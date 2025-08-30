CREATE TABLE `connection_log` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `player` VARCHAR(16) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `uuid` VARCHAR(36) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
    `server` VARCHAR(16) NOT NULL COMMENT '接続サーバー名' COLLATE 'utf8mb4_0900_ai_ci',
    `connected_time` DATETIME NOT NULL COMMENT '接続時刻',
    `disconnected_time` DATETIME NULL DEFAULT NULL COMMENT '切断時刻',
    `connection_seconds` INT NULL DEFAULT NULL COMMENT '接続していた秒数: Disconnect時に保存',
    `ip` VARCHAR(256) NULL DEFAULT NULL COMMENT '接続元IPアドレス' COLLATE 'utf8mb4_0900_ai_ci',
    `port` INT NULL DEFAULT NULL COMMENT '接続元ポート',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `connection_log_uuid_index` (`uuid`) USING BTREE
)
COLLATE='utf8mb4_0900_ai_ci'
ENGINE=InnoDB
AUTO_INCREMENT=23
;