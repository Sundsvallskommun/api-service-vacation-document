ALTER TABLE `document` ADD COLUMN `municipality_id` VARCHAR(4) NULL;

UPDATE `document` SET `municipality_id` = '2281';

ALTER TABLE `document` MODIFY `municipality_id` VARCHAR(4) NOT NULL;

ALTER TABLE `document` ADD INDEX municipality_id_index(`municipality_id`);
