-- question 1
select Questions.*
from Questions,Users
where Questions.user = Users.id and Users.name="Alice";

-- question 2 
select Users.name, Questions.*
from Questions join (
  -- find highest score for user
  select user, max(rank) as rank
  from Questions
  group by user
) maxrank on Questions.rank = maxrank.rank, Users
where Users.id = Questions.user;

-- question 3
select answers.text, answers.rank, answers.date
from answers, questions
where answers.question = questions.id and 
  Questions.text like '%MySQL%'
order by answers.rank desc;

-- question 4 
select distinct users.*
from users, questions
where users.id = questions.user and 
  questions.text like '%MySQL%'and 
  questions.user in (
    select q2.user
    from questions q2
    where q2.text like '%Java%'
  );

-- question 5
select tags.*
from tags
where tags.id not in (
  -- get tags not used after date
  select tags.id
  from tags, questions, question_tag
  where question_tag.tag = tags.id and
    question_tag.question = questions.id and
    questions.date > '2014-01-01'
  ) and tags.id in (
  -- make sure tag was used
    select tag
    from question_tag);

-- question 6
select count(*)
from questions
-- question rank > 3
where questions.rank > 3 and 
  questions.id not in (
    -- answer rank > 3 
    select distinct answers.question
    from answers
    where answers.rank <= 3
  );

-- question 7
-- outer join to get tags for all users
create view users_joined_tags as
  select 
    users.id userid,
    users.name username,
    questions.id questionid,
    tags.id tagid,
    tags.tagName tagname
  from users 
  left join questions 
    ON users.id = questions.user
  left join question_tag 
    ON questions.id = question_tag.question
  left join tags 
    ON tags.id = question_tag.tag;

-- outer join to get all users for tags
create view tags_joined_users as
  select 
    users.id userid,
    users.name username,
    questions.id questionid,
    tags.id tagid,
    tags.tagName tagname
  from users
  right join questions 
    ON users.id = questions.user
  right join question_tag 
    ON questions.id = question_tag.question
  right join tags 
    ON tags.id = question_tag.tag;

select 
  userid, 
  username, 
  tagid, 
  tagname, 
  count(questionid) tag_use_count
from (
  -- outer join
  select * from users_joined_tags 
  union 
  select * from tags_joined_users
) outerjoin
group by userid, tagid;

-- question 8
select questions.*
from questions, (
  select  answers.question, count(*) answer_count
  from answers
  group by answers.question
  order by answer_count desc
  -- get top 5
  limit 5
) top5
where questions.id = top5.question;

-- question 9
-- get tags associated with users
create view user_tag_joined as
  select distinct
    users.id userid, 
    users.name username, 
    tags.id tagid
  from users
  left join questions 
    ON users.id = questions.user
  left join question_tag 
    ON questions.id = question_tag.question
  left join tags 
    ON tags.id = question_tag.tag;

-- get sorted string representing all tags used
create view user_tagconcat as
  select 
    userid,
    username,
    group_concat(distinct tagid order by username , tagid) tagConcat
  from user_tag_joined
  group by userid;

-- compare strings between each combination of users
select t1.username user1, t2.username user2
from user_tagconcat t1 
  inner join
  user_tagconcat t2 
  ON t1.userid < t2.userid
where t1.userid != t2.userid and t1.tagConcat = t2.tagConcat;

-- question 10
-- count the number of answers each question
create view user_answer_count as
select questions.id qid,
  questions.user quser,
    count(answers.id) acount
from questions left join answers 
  ON questions.id = answers.question
group by questions.id;

-- get average answer count for each user
select users.name, avg(acount) AAC
from users inner join user_answer_count 
ON users.id = quser
group by users.id
order by AAC desc
limit 3;