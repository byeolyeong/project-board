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
UPDATE post SET content = 'content와 이미지 수정', image_url = 'also_image.png' WHERE id = 499998

DELETE FROM post WHERE id IN (499997, 499996);

--  좋아요 등록
INSERT INTO post_like (member_id, post_id)
	VALUES (1, 2);

-- 좋아요 취소
DELETE FROM post_like 
WHERE member_id = 1 AND post_id = 2;

SELECT * FROM post_like;

-- 좋아요 등록 여부
SELECT COUNT(*) 
FROM post_like 
WHERE member_id = 1 AND post_id = 2;

-- 좋아요 수치 증가
UPDATE post
SET like_count = like_count + 1
WHERE id = 2;

SELECT * FROM post WHERE id = 2;

-- 좋아요 수치 감소
UPDATE post
SET like_count = GREATEST(like_count - 1, 0)
WHERE id = 2;

SELECT * FROM post WHERE id = 2;


-- email로 회원 정보 조회
SELECT * FROM member WHERE email = 'user1@example.com'










