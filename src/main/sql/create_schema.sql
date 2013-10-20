--
-- Create schema responses
--
DROP DATABASE responses;
CREATE DATABASE IF NOT EXISTS responses;
USE responses;

-- Table creation
CREATE TABLE  `Company` (
  `id` int NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `Contact` (
  `id` bigint(20) NOT NULL auto_increment,
  `email` varchar(255) default NULL,
  `abuse` tinyint(1) NOT NULL,
  `creationDate` datetime default NULL,
  `url` varchar(255) default NULL,
  `questionId` bigint(20) default NULL,
  `answerId` bigint(20) default NULL,
  `subject` varchar(255) default NULL,
  `message` varchar(5000) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX contact_creationdate_index ON Contact (creationDate);

CREATE TABLE  `Instance` (
  `id` int NOT NULL auto_increment,
  `name` varchar(50) NOT NULL,
  `longName` varchar(100) NOT NULL,
  `description` varchar(10000) default NULL,
  `type` int NOT NULL,
  `enabled` int NOT NULL,
  `company_id` int NOT NULL,
  KEY `FK_instance_company_id` (`company_id`),
  CONSTRAINT `FK_instance_company_id` FOREIGN KEY (`company_id`) REFERENCES `Company` (`id`),
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX instance_name_index ON Instance (name);

CREATE TABLE  `Users` (
  `id` int NOT NULL auto_increment,
  `email` varchar(255) UNIQUE NOT NULL,
  `password` varchar(255) default NULL,
  `enabled` tinyint(1) NOT NULL,
  `firstName` varchar(255) default NULL,
  `lastName` varchar(255) default NULL,
  `language` varchar(255) default NULL,
  `dateFormat` varchar(255) default NULL,
  `creationDate` datetime default NULL,
  `lastAccessDate` datetime default NULL,
  `company_id` int NOT NULL,
  KEY `FK_users_company_id` (`company_id`),
  CONSTRAINT `FK_users_company_id` FOREIGN KEY (`company_id`) REFERENCES `Company` (`id`),
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX user_email_index ON Users (email);

CREATE TABLE  `Tag` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `text` varchar(20) NOT NULL,
  `size` INT NOT NULL,
  `instance_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_tag_instance` (`instance_id`),
  CONSTRAINT `FK_tag_instance` FOREIGN KEY (`instance_id`) REFERENCES `Instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `Favorite_Tag` (
  `Users_id` int NOT NULL,
  `favoriteTags_id` int UNSIGNED NOT NULL,
  PRIMARY KEY  (`Users_id`,`favoriteTags_id`),
  KEY `FK2923667794A4B91B` (`favoriteTags_id`),
  KEY `FK292366776AC0AF11` (`Users_id`),
  CONSTRAINT `FK292366776AC0AF11` FOREIGN KEY (`Users_id`) REFERENCES `Users` (`id`),
  CONSTRAINT `FK2923667794A4B91B` FOREIGN KEY (`favoriteTags_id`) REFERENCES `Tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `Ignored_Tag` (
  `Users_id` int NOT NULL,
  `ignoredTags_id` int UNSIGNED NOT NULL,
  PRIMARY KEY  (`Users_id`,`ignoredTags_id`),
  KEY `FKBF5B226D6AC0AF11` (`Users_id`),
  KEY `FKBF5B226D23D202A5` (`ignoredTags_id`),
  CONSTRAINT `FKBF5B226D23D202A5` FOREIGN KEY (`ignoredTags_id`) REFERENCES `Tag` (`id`),
  CONSTRAINT `FKBF5B226D6AC0AF11` FOREIGN KEY (`Users_id`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `persistent_logins` (
  username varchar(255) not null,
  series varchar(64) primary key,
  token varchar(64) not null,
  last_used timestamp not null);

CREATE TABLE  `Question` (
  `id` bigint(20) NOT NULL auto_increment,
  `title` varchar(140) default NULL,
  `text` varchar(10000) default NULL,
  `views` bigint(20) NOT NULL,
  `creationDate` datetime default NULL,
  `updateDate` datetime default NULL,
  `votesSize` int NOT NULL,
  `answersSize` int NOT NULL,
  `bestAnswerId` bigint(20) NOT NULL,
  `user_id` int default NULL,
  `instance_id` int NOT NULL,
  `wfState` int default NULL,
  `wfAssignedUser` int default NULL,
  `wfDate` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `FKBE5CA006E7366E94` (`user_id`),
  KEY `FK_question_instance` (`instance_id`),
  CONSTRAINT `FKBE5CA006E7366E94` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`),
  CONSTRAINT `FK_question_instance` FOREIGN KEY (`instance_id`) REFERENCES `Instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX question_creationdate_index ON Question (instance_id, creationDate);
CREATE INDEX question_views_index ON Question (instance_id, views);
CREATE INDEX question_updatedate_index ON Question (instance_id, updateDate);
CREATE INDEX question_votessize_index ON Question (instance_id, votesSize);
CREATE INDEX question_wf_index ON Question (wfState, wfAssignedUser, wfDate);

CREATE TABLE  `QuestionVote` (
  `id` bigint(20) NOT NULL UNIQUE auto_increment,
  `question_id` bigint(20) NOT NULL,
  `user_id` int NOT NULL,
  `value` int(11) NOT NULL,
  PRIMARY KEY  (`question_id`, `user_id`),
  KEY `FKFF89F410E7366E94` (`user_id`),
  KEY `FKFF89F4107FB51ECE` (`question_id`),
  CONSTRAINT `FKFF89F4107FB51ECE` FOREIGN KEY (`question_id`) REFERENCES `Question` (`id`),
  CONSTRAINT `FKFF89F410E7366E94` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX questionvote_id_index ON QuestionVote (id);

CREATE TABLE  `Question_Tag` (
  `Questions_id` bigint(20) NOT NULL,
  `tags_id` int UNSIGNED NOT NULL,
  PRIMARY KEY  (`Questions_id`,`tags_id`),
  KEY `FKFF8DA3C167937337` (`tags_id`),
  KEY `FKFF8DA3C17FB51ECE` (`Questions_id`),
  CONSTRAINT `FKFF8DA3C17FB51ECE` FOREIGN KEY (`Questions_id`) REFERENCES `Question` (`id`),
  CONSTRAINT `FKFF8DA3C167937337` FOREIGN KEY (`tags_id`) REFERENCES `Tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `QuestionComment` (
  `id` bigint(20) NOT NULL UNIQUE auto_increment,
  `value` varchar(500) default NULL,
  `question_id` bigint(20) NOT NULL,
  `user_id` int NOT NULL,
  `creationDate` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_questioncomment_question_id` (`question_id`),
  KEY `FK_questioncomment_user_id` (`user_id`),
  CONSTRAINT `FK_questioncomment_question_id` FOREIGN KEY (`question_id`) REFERENCES `Question` (`id`),
  CONSTRAINT `FK_questioncomment_user_id` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `Answer` (
  `id` bigint(20) NOT NULL auto_increment,
  `text` varchar(10000) default NULL,
  `creationDate` datetime default NULL,
  `votesSize` int NOT NULL,
  `question_id` bigint(20) default NULL,
  `user_id` int default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK752F2BDEE7366E94` (`user_id`),
  KEY `FK752F2BDE7FB51ECE` (`question_id`),
  CONSTRAINT `FK752F2BDE7FB51ECE` FOREIGN KEY (`question_id`) REFERENCES `Question` (`id`),
  CONSTRAINT `FK752F2BDEE7366E94` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `AnswerVote` (
  `id` bigint(20) NOT NULL UNIQUE auto_increment,
  `value` int(11) NOT NULL,
  `answer_id` bigint(20) NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY  (`answer_id`, `user_id`),
  KEY `FKB002D3E8C68E14CE` (`answer_id`),
  KEY `FKB002D3E8E7366E94` (`user_id`),
  CONSTRAINT `FKB002D3E8C68E14CE` FOREIGN KEY (`answer_id`) REFERENCES `Answer` (`id`),
  CONSTRAINT `FKB002D3E8E7366E94` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX answervote_id_index ON AnswerVote (id);

CREATE TABLE  `AnswerComment` (
  `id` bigint(20) NOT NULL UNIQUE auto_increment,
  `value` varchar(500) default NULL,
  `answer_id` bigint(20) NOT NULL,
  `user_id` int NOT NULL,
  `creationDate` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_answercomment_answer_id` (`answer_id`),
  KEY `FK_answercomment_user_id` (`user_id`),
  CONSTRAINT `FK_answercomment_answer_id` FOREIGN KEY (`answer_id`) REFERENCES `Answer` (`id`),
  CONSTRAINT `FK_answercomment_user_id` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `Role` (
  `role` varchar(255) NOT NULL,
  PRIMARY KEY  (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `Users_Role` (
  `Users_id` int NOT NULL,
  `roles_role` varchar(255) NOT NULL,
  PRIMARY KEY  (`Users_id`,`roles_role`),
  KEY `FKB1C808D6AC0AF11` (`Users_id`),
  KEY `FKB1C808D2296202` (`roles_role`),
  CONSTRAINT `FKB1C808D2296202` FOREIGN KEY (`roles_role`) REFERENCES `Role` (`role`),
  CONSTRAINT `FKB1C808D6AC0AF11` FOREIGN KEY (`Users_id`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `Users_Instance` (
  `Users_id` int NOT NULL,
  `instances_id` int NOT NULL,
  PRIMARY KEY  (`Users_id`,`instances_id`),
  KEY `fk_users_instance_users_id` (`Users_id`),
  KEY `fk_users_instance_instances_id` (`instances_id`),
  CONSTRAINT `fk_users_instance_users_id` FOREIGN KEY (`Users_id`) REFERENCES `Users` (`id`),
  CONSTRAINT `fk_users_instance_instances_id` FOREIGN KEY (`instances_id`) REFERENCES `Instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `Expertize` (
  `id` bigint(20) NOT NULL UNIQUE auto_increment,
  `tag_id` int UNSIGNED NOT NULL,
  `user_id` int NOT NULL,
  `points` int(11) NOT NULL,
  PRIMARY KEY  (`tag_id`, `user_id`),
  KEY `FK_expertize_user_id` (`user_id`),
  KEY `FK_expertize_tag_id` (`tag_id`),
  CONSTRAINT `FK_expertize_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `Tag` (`id`),
  CONSTRAINT `FK_expertize_user_id` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX expertize_id_index ON Expertize (id);
CREATE INDEX expertize_points_index ON Expertize (points);

CREATE TABLE  `Watch` (
  `id` bigint(20) NOT NULL UNIQUE auto_increment,
  `question_id` bigint(20) default NULL,
  `user_id` int default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK4F7D4AFE7366E94` (`user_id`),
  KEY `FK4F7D4AF7FB51ECE` (`question_id`),
  CONSTRAINT `FK4F7D4AF7FB51ECE` FOREIGN KEY (`question_id`) REFERENCES `Question` (`id`),
  CONSTRAINT `FK4F7D4AFE7366E94` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX watch_index ON Watch (question_id, user_id);

CREATE TABLE  `Workflow` (
  `id` bigint(20) NOT NULL UNIQUE auto_increment,
  `question_id` bigint(20) default NULL,
  `user_id` int default NULL,
  `assignedUser_id` bigint(20) default NULL,
  `stateDate` datetime default NULL,
  `state` int default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_workflow_question_id` (`question_id`),
  KEY `FK_workflow_user_id` (`user_id`),
  CONSTRAINT `FK_workflow_question_id` FOREIGN KEY (`question_id`) REFERENCES `Question` (`id`),
  CONSTRAINT `FK_workflow_user_id` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX workflow_assignedUser_id_index ON Workflow (assignedUser_id);

CREATE TABLE  `SearchQuery` (
  `id` bigint(20) NOT NULL UNIQUE auto_increment,
  `text` varchar(255) NOT NULL,
  `size` int NOT NULL,
  `updateDate` datetime default NULL,
  `instance_id` int NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_searchquery_instance` (`instance_id`),
  CONSTRAINT `FK_searchquery_instance` FOREIGN KEY (`instance_id`) REFERENCES `Instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX searchquery_index ON SearchQuery (text, instance_id);

CREATE TABLE  `TagSummary` (
  `id` bigint(20) UNIQUE NOT NULL auto_increment,
  `tag1` int NOT NULL,
  `tag2` int NOT NULL,
  `tag3` int NOT NULL,
  `tag4` int NOT NULL,
  `tag5` int NOT NULL,
  `size` int NOT NULL,
  PRIMARY KEY  (`tag1`, `tag2`,`tag3`,`tag4`,`tag5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX tagsummary_index ON TagSummary (id);

CREATE INDEX tagsummary_tag1_index ON TagSummary (tag1, tag3, size);
CREATE INDEX tagsummary_tag2_index ON TagSummary (tag2, tag3, size);

CREATE INDEX tagsummary_tag3_index ON TagSummary (tag1, tag2, tag4, size);
CREATE INDEX tagsummary_tag4_index ON TagSummary (tag1, tag3, tag4, size);
CREATE INDEX tagsummary_tag5_index ON TagSummary (tag2, tag3, tag4, size);

CREATE INDEX tagsummary_tag6_index ON TagSummary (tag1, tag2, tag3, tag4, size);
CREATE INDEX tagsummary_tag7_index ON TagSummary (tag1, tag2, tag3, tag5, size);
CREATE INDEX tagsummary_tag8_index ON TagSummary (tag1, tag2, tag4, tag5, size);
CREATE INDEX tagsummary_tag9_index ON TagSummary (tag1, tag3, tag4, tag5, size);

--
-- Load data
--

INSERT INTO `Role` (`role`) VALUES ("ROLE_SU");
INSERT INTO `Role` (`role`) VALUES ("ROLE_USER");
INSERT INTO `Role` (`role`) VALUES ("ROLE_ADMIN");
INSERT INTO `Role` (`role`) VALUES ("ROLE_MODERATOR");
INSERT INTO `Role` (`role`) VALUES ("ROLE_SUPPORT");

-- Insert companies
INSERT INTO `Company` (`id`,`name`) VALUES ("1","No company");
INSERT INTO `Company` (`id`,`name`) VALUES ("2","Responses");
INSERT INTO `Company` (`id`,`name`) VALUES ("3","Soci&eacute;t&eacute; exemple");

-- Insert instances
INSERT INTO `Instance` (`id`,`name`,`longName`,`type`,`enabled`,`company_id`, `description`) VALUES ('1','developpement','Développement informatique','1','1','1','Bienvenue sur l\'instance publique de Responses d&eacute;di&eacute;e au d&eacute;veloppement informatique. Vous pouvez poser vos questions, ou y r&eacute;pondre, sur tous les th&egrave;mes li&eacute;s au d&eacute;veloppement, et plus particuli&egrave;ment sur Java, Spring, Tomcat, les bases de donn&eacute;es et les technologies Web.');
INSERT INTO `Instance` (`id`,`name`,`longName`,`type`,`enabled`,`company_id`, `description`) VALUES ('2','demo','Instance de démonstration privée','0','1','1','Ceci est l\'instance de d&eacute;monstration priv&eacute;e de Responses. Elle sert uniquement &agrave; montrer le fonctionnement de l\'application, ses questions et r&eacute;ponses ne veulent rien dire.');

-- Insert Admin
INSERT INTO `Users` (`id`, `email`,`password`,`enabled`,`firstName`,`lastName`,`dateFormat`,`creationDate`,`lastAccessDate`,`company_id`) VALUES ("1","julien@julien-dubois.com","resp0","1","Julien","Dubois","MM/dd/yyyy",now(),now(), "2");
INSERT INTO `Users_Role` (`users_id`,`roles_role`) VALUES ("1","ROLE_SU");
INSERT INTO `Users_Role` (`users_id`,`roles_role`) VALUES ("1","ROLE_USER");
INSERT INTO `Users_Role` (`users_id`,`roles_role`) VALUES ("1","ROLE_ADMIN");
INSERT INTO `Users_Role` (`users_id`,`roles_role`) VALUES ("1","ROLE_MODERATOR");
INSERT INTO `Users_Role` (`users_id`,`roles_role`) VALUES ("1","ROLE_SUPPORT");
INSERT INTO `Users_Instance` (`users_id`,`instances_id`) VALUES ("1","1");
INSERT INTO `Users_Instance` (`users_id`,`instances_id`) VALUES ("1","2");

-- Insert Test user
INSERT INTO `Users` (`id`, `email`,`password`,`enabled`,`firstName`,`lastName`,`dateFormat`,`creationDate`,`lastAccessDate`,`company_id`) VALUES ("2","user@julien-dubois.com","user0","1","Test","User","MM/dd/yyyy",now(),now(), "3");
INSERT INTO `Users_Role` (`users_id`,`roles_role`) VALUES ("2","ROLE_USER");
INSERT INTO `Users_Instance` (`users_id`,`instances_id`) VALUES ("2","2");

-- Insert Moderator
INSERT INTO `Users` (`id`, `email`,`password`,`enabled`,`firstName`,`lastName`,`dateFormat`,`creationDate`,`lastAccessDate`,`company_id`) VALUES ("3","moderator@julien-dubois.com","moderator0","1","Test","Moderator","MM/dd/yyyy",now(),now(), "3");
INSERT INTO `Users_Role` (`users_id`,`roles_role`) VALUES ("3","ROLE_USER");
INSERT INTO `Users_Role` (`users_id`,`roles_role`) VALUES ("3","ROLE_MODERATOR");
INSERT INTO `Users_Instance` (`users_id`,`instances_id`) VALUES ("3","2");

-- Insert Support
INSERT INTO `Users` (`id`, `email`,`password`,`enabled`,`firstName`,`lastName`,`dateFormat`,`creationDate`,`lastAccessDate`,`company_id`) VALUES ("4","support@julien-dubois.com","support0","1","Test","Support","MM/dd/yyyy",now(),now(), "3");
INSERT INTO `Users_Role` (`users_id`,`roles_role`) VALUES ("4","ROLE_USER");
INSERT INTO `Users_Role` (`users_id`,`roles_role`) VALUES ("4","ROLE_SUPPORT");
INSERT INTO `Users_Instance` (`users_id`,`instances_id`) VALUES ("4","2");


-- Insert welcome questions
INSERT INTO `Question` VALUES  (1,'Bienvenue sur Responses','<h1>Bienvenue</h1>\n<p>Ceci est la premi&egrave;re question de cette instance de Responses.</p><pre class="brush: java">public class Bienvenue {\n\tpublic static void main (String[] args){\n\t\tSystem.out.println(&quot;Hello World&quot;)\n\t}\n}</pre>',0,now(),now(),0,0,-1,1,1,-1,-1,now());
INSERT INTO `Question` VALUES  (2,'Bienvenue sur Responses','<h1>Bienvenue</h1>\n<p>Ceci est la premi&egrave;re question de cette instance de Responses.</p><pre class="brush: java">public class Bienvenue {\n\tpublic static void main (String[] args){\n\t\tSystem.out.println(&quot;Hello World&quot;)\n\t}\n}</pre>',0,now(),now(),0,0,-1,1,2,-1,-1,now());

-- Insert dummy questions in the demo instance
delimiter !
CREATE PROCEDURE insertDummyQuestions()
BEGIN
	DECLARE batch INT DEFAULT 50000;
	DECLARE questionId INT;
	WHILE batch > 0 DO
    		insert into Question (answersSize, creationDate, instance_id, title, text, updateDate, user_id, views, votesSize, bestAnswerId, wfState, wfAssignedUser, wfDate) values (2, now(), 1, 'Ceci est un titre', '<p>Voici le texte de la question.<p>', now(), 1, 0, 0, -1, 0, -1, now());
    		SET questionId = LAST_INSERT_ID();
		insert into Answer (creationDate, question_id, text, user_id, votesSize) values (now(), questionId, '<p>Voici une reponse</p>', 1, 0);
		insert into Answer (creationDate, question_id, text, user_id, votesSize) values (now(), questionId, '<p>Voici une autre reponse</p>', 1, 0);
		SET batch = batch - 1;
	END WHILE;
END
!
delimiter ;

-- Database updates
ALTER TABLE Users ADD COLUMN `website` varchar(100) default NULL;
ALTER TABLE Users ADD COLUMN `blog` varchar(100) default NULL;
ALTER TABLE Users ADD COLUMN `twitter` varchar(100) default NULL;
ALTER TABLE Users ADD COLUMN `linkedIn` varchar(100) default NULL;

