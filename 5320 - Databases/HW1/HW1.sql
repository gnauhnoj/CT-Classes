CREATE database CS5301_HW1_StackOverflow;
use CS5301_HW1_StackOverflow;
create table `Users` (
  `id` int not null auto_increment,
  `username` varchar(15) not null,
  primary key (`id`)
);
create table `Questions` (
  `id` int not null auto_increment,
  `text` text not null,
  `user` int not null,
  `date` datetime not null,
  primary key (`id`),
  foreign key (`user`) references Users(`id`)
);
create table `Answers` (
  `id` int not null auto_increment,
  `text` text not null,
  `rank` int not null,
  `user` int not null,
  `question` int not null,
  `date` datetime not null, 
  primary key (`id`),
  foreign key (`user`) references Users(`id`),
  foreign key (`question`) references Questions(`id`)
);
create table `Comments` (
  `id` int not null auto_increment,
  `text` text not null,
  `user` int not null,
  `answer` int not null,
  `date` datetime not null, 
  primary key (`id`),
  foreign key (`user`) references Users(`id`),
  foreign key (`answer`) references Answers(`id`)
);
create table `Tags` (
  `id` int not null auto_increment,
  `tagName` varchar(30) not null,
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


