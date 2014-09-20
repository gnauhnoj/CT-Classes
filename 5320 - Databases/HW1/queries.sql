-- question 1
select Questions.text
  from Questions,Users
  where Questions.user = Users.id and Users.name="Alice";

-- question 2
select Users.name, Questions.text, Questions.rank
  from Questions join (
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

-- question 4 - need to change data to verify
select distinct users.name
  from users, questions
  where users.id = questions.user and
    questions.text like '%MySQL%' and
    questions.user in (
      select q2.user 
        from questions q2
        where q2.text like '%Java%'
  );

-- question 5 -- more efficient way?
select distinct tags.tagName
from tags
where tags.id not in (
  select tags.id
    from tags, questions, question_tag
    where question_tag.tag = tags.id and
      question_tag.question = questions.id and
      questions.date > '2014-01-01'
) and tags.id in (
  select tags.id
      from tags, questions, question_tag
      where question_tag.tag = tags.id and
    question_tag.question = questions.id
);