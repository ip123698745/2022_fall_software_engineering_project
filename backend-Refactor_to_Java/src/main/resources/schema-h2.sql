CREATE TABLE `Repositories` (
	`id`	INTEGER NOT NULL AUTO_INCREMENT,
	`accountColonPw`	varchar(255),
	`apiToken`	varchar(255),
	`name`	varchar(255),
	`owner`	varchar(255),
	`projectKey`	varchar(255),
	`type`	varchar(255),
	`url`	varchar(255),
	`repoId`	INTEGER,
	PRIMARY KEY(`id`)
);

CREATE TABLE `Users`
(
    `account`    VARCHAR(255) NOT NULL,
    `name`       VARCHAR(255),
    `avatar_url` VARCHAR(255),
    `authority`  VARCHAR(255),
    `password`   VARCHAR(255),
    PRIMARY KEY (`account`)
);