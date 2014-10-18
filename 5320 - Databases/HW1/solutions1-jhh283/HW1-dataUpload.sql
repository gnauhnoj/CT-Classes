CREATE database CS5301_HW1_StackOverflow;
use CS5301_HW1_StackOverflow;
create table `Users` (
  `id` int not null auto_increment,
  `name` varchar(25) not null,
  primary key (`id`)
);
create table `Questions` (
  `id` int not null auto_increment,
  `text` varchar(10000)	not null,
  `user` int not null,
  `rank` int not null default 0,
  `date` date not null,
  primary key (`id`),
  foreign key (`user`) references Users(`id`)
);
create table `Answers` (
  `id` int not null auto_increment,
  `text` varchar(10000) not null,
  `user` int not null,
  `rank` int not null default 0,
  `question` int not null,
  `date` date not null, 
  primary key (`id`),
  foreign key (`user`) references Users(`id`),
  foreign key (`question`) references Questions(`id`)
);
create table `Comments` (
  `id` int not null auto_increment,
  `text` varchar(10000) not null,
  `user` int not null,
  `answer` int not null,
  `date` date not null, 
  primary key (`id`),
  foreign key (`user`) references Users(`id`),
  foreign key (`answer`) references Answers(`id`)
);
create table `Tags` (
  `id` int not null auto_increment,
  `tagName` varchar(30) not null unique,
  primary key (`id`)
);
create table `Question_Tag` (
  `id` int not null auto_increment,
  `question` int not null,
  `tag` int not null,
  primary key (`id`),
  foreign key (`question`) references Questions(`id`),
  foreign key (`tag`) references Tags(`id`)
);

LOAD DATA INFILE '/Users/jhh11/Copy/CT/5320 - Databases/HW1/solutions1-jhh283/data/users.txt' 
  INTO TABLE Users   
  FIELDS TERMINATED BY ','   
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;
LOAD DATA INFILE '/Users/jhh11/Copy/CT/5320 - Databases/HW1/solutions1-jhh283/data/questions.txt' 
  INTO TABLE Questions   
  FIELDS TERMINATED BY ','   
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;
LOAD DATA INFILE '/Users/jhh11/Copy/CT/5320 - Databases/HW1/solutions1-jhh283/data/tags.txt' 
  INTO TABLE Tags   
  FIELDS TERMINATED BY ','   
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;
LOAD DATA INFILE '/Users/jhh11/Copy/CT/5320 - Databases/HW1/solutions1-jhh283/data/question_tag.txt' 
  INTO TABLE Question_Tag   
  FIELDS TERMINATED BY ','   
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;
LOAD DATA INFILE '/Users/jhh11/Copy/CT/5320 - Databases/HW1/solutions1-jhh283/data/answers.txt' 
  INTO TABLE Answers   
  FIELDS TERMINATED BY ','   
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;
LOAD DATA INFILE '/Users/jhh11/Copy/CT/5320 - Databases/HW1/solutions1-jhh283/data/comments.txt' 
  INTO TABLE Comments   
  FIELDS TERMINATED BY ','   
  LINES TERMINATED BY '\n'
  IGNORE 1 LINES;
  