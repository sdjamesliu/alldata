ALTER TABLE `am_component_package_task`
    ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT 'VERSION';

ALTER TABLE `am_app_package`
    ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT 'VERSION';

ALTER TABLE `am_app_package_task`
    ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT 'VERSION';

ALTER TABLE `am_deploy_app`
    ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT 'VERSION';

ALTER TABLE `am_deploy_component`
    ADD COLUMN `version` INT NOT NULL DEFAULT 0 COMMENT 'VERSION';
