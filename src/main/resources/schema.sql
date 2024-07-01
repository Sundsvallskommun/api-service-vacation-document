CREATE TABLE IF NOT EXISTS `document` (
    `document_id` VARCHAR(16) NOT NULL,
    `status` ENUM ('NOT_APPROVED', 'PROCESSING', 'DONE', 'ERROR'),
    `detail` TEXT,
    PRIMARY KEY (`document_id`)
);
