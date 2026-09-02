--  테이블에 부여된 모든 인덱스(PK, FK, 수동 인덱스)의 세부 정보 확인
SHOW INDEX FROM post;

SHOW INDEX FROM member;


-- post 테이블의 created_at 컬럼에 일반 인덱스 생성
CREATE INDEX idx_post_created_at ON post (created_at);

-- post 테이블의 member_id와 created_at을 결합한 복합 인덱스 생성
CREATE INDEX idx_post_member_created ON post (member_id, created_at DESC);


-- DROP INDEX 문법
DROP INDEX idx_post_created_at ON post;

-- 기존 FK 인덱스를 다시 생성
CREATE INDEX member_id ON post (member_id);

-- ALTER TABLE 문법
ALTER TABLE post DROP INDEX idx_post_member_created;




-- 1. like_count 인덱스가 없을 때: 50만 건 전체를 메모리/디스크에서 정렬하느라 지연 발생
SELECT * FROM post ORDER BY like_count DESC LIMIT 10;

-- 2. like_count 단일 인덱스 생성
CREATE INDEX idx_post_like_count ON post (like_count DESC);

-- 3. 인덱스 적용 후 재실행: 50만 건 정렬 없이 인덱스 최상단 10건만 즉시 반환
SELECT * FROM post ORDER BY like_count DESC LIMIT 10;




-- 부적절한 예시: 인덱스 컬럼(like_count)에 산술 연산 적용
SELECT * FROM post WHERE like_count * 2 >= 1000 ORDER BY like_count;

-- 개선된 예시: 인덱스 컬럼을 가공하지 않고 상수 영역에서 비교 연산 수행
SELECT * FROM post WHERE like_count >= 500 ORDER BY like_count;




-- 1. content 컬럼에 일반 B-Tree 접두사 인덱스 생성 (TEXT 타입은 앞 255자 길이 지정)
CREATE INDEX idx_post_content ON post (content(255));

-- 부적절한 예시: 일반 B-Tree 인덱스가 존재해도 양쪽 와일드카드 검색 시 50만 건 풀 테이블 스캔 발생
SELECT COUNT(*) FROM post WHERE content LIKE '%99%';



-- 2. ngram 파서 기반 Full-Text(전문 검색) 인덱스 생성 (길이 지정 없이 본문 전체 역색인)
CREATE FULLTEXT INDEX ft_idx_post_content ON post (content) WITH PARSER ngram;

-- 개선된 예시: MATCH ... AGAINST 구문을 통한 Full-Text 인덱스 탐색
SELECT COUNT(*) FROM post WHERE MATCH(content) AGAINST('99' IN BOOLEAN MODE);




-- 부적절한 예시: 문자열 컬럼에 숫자 리터럴(12345) 대입 -> 내부적으로 CAST(content AS DOUBLE) 호출로 50만 건 풀 테이블 스캔 발생
SELECT COUNT(*) FROM post WHERE content = 12345;

-- 개선된 예시: 컬럼과 동일한 문자열 타입 리터럴로 비교하여 인덱스 정상 탐색
SELECT COUNT(*) FROM post WHERE content = '12345';



-- 인덱스를 사용하지 못하는 쿼리 (컬럼 가공으로 인한 풀 테이블 스캔)
EXPLAIN SELECT * FROM post WHERE like_count * 2 >= 1980 ORDER BY like_count;

-- 인덱스를 정상 활용하는 쿼리 (선택도 1% 이내로 인덱스 레인지 스캔 채택)
EXPLAIN SELECT * FROM post WHERE like_count >= 990 ORDER BY like_count;

-- 인덱스가 있어도 풀 테이블 스캔을 선택하는 쿼리 (손익분기점 초과: 전체 데이터의 50% 조회)
EXPLAIN SELECT * FROM post WHERE like_count >= 500 ORDER BY like_count;



-- 최초 첫 페이지 조회
SELECT * FROM post ORDER BY id DESC LIMIT 10;

-- 10개의 게시글을 건너띄고 10개 조회(페이지당 10개씩 보여줄 경우 2 페이지 조회)
SELECT * FROM post ORDER BY id DESC LIMIT 10 OFFSET 10;

-- 499990개의 게시글을 건너띄고 10개 조회(페이지당 10개씩 보여줄 경우 마지막 페이지 조회)
SELECT * FROM post ORDER BY id DESC LIMIT 10 OFFSET 499990;



-- 최초 첫 페이지 조회
SELECT id, content, image_url, member_id, created_at
FROM post
ORDER BY id DESC
LIMIT 10;

-- 이후 무한 스크롤 요청 시 (1 페이지에서 읽은 마지막 post_id = 499991을 기준으로 다음 10건 조회, 2 페이지)
SELECT id, content, image_url, member_id, created_at
FROM post
WHERE id < 499991
ORDER BY id DESC
LIMIT 10;

-- 이후 무한 스크롤 요청 시 (49999 페이지에서 읽은 마지막 post_id = 11을 기준으로 다음 10건 조회, 마지막 페이지)
SELECT id, content, image_url, member_id, created_at
FROM post
WHERE id < 11
ORDER BY id DESC
LIMIT 10;





