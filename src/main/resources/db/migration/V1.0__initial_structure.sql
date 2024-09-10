CREATE TABLE IF NOT EXISTS `document` (
    `document_id` VARCHAR(16) NOT NULL,
    `status` VARCHAR(16) NOT NULL,
    `detail` TEXT,
    PRIMARY KEY (`document_id`)
);
