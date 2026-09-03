SELECT 
  p.id AS post_id,
  p.content AS post_content,
  p.image_url AS post_image_url,
  p.created_at AS post_created_at,
  m.id AS member_id,
  m.nickname AS member_nickname,
  m.profile_image AS member_profile_image,
  c.id AS comment_id,
  c.member_id AS commenter_id,
  cm.nickname AS commenter_nickname,
  c.content AS comment_content,
  c.created_at AS comment_created_at
FROM post p
INNER JOIN member m ON p.member_id = m.id
LEFT JOIN comment c ON p.id = c.post_id
LEFT JOIN member cm ON c.member_id = cm.id
WHERE p.id = 1
ORDER BY c.id ASC;

INSERT INTO comment (post_id, member_id, content, created_at)
VALUES 
	(1, 1, "1번 작성자의 댓글", NOW()),
	(1, 2, "2번 작성자의 댓글", NOW()),
	(1, 3, "3번 작성자의 댓글", NOW());


UPDATE post SET content = 'content만 수정' WHERE id = 500000;
UPDATE post SET image_url = 'only.png' WHERE id = 499999;

DELETE FROM post WHERE id IN (499997, 499996)