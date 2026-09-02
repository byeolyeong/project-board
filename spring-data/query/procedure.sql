-- 회원(member) 100명 대량 생성 프로시저
DELIMITER //
CREATE PROCEDURE insert_bulk_member()
BEGIN
    DECLARE i INT DEFAULT 1;
    SET autocommit = 0; -- 자동 커밋 취소
    
    WHILE i <= 100 DO
        INSERT INTO member (email, password, nickname, created_at)
        VALUES (
            CONCAT('user', i, '@example.com'),
            '1111',
            CONCAT('사용자', i),
            NOW()
        );
        SET i = i + 1;
    END WHILE;
    
    COMMIT;
    SET autocommit = 1;
END //
DELIMITER ;

-- 프로시저 실행 (100명 생성)
CALL insert_bulk_member();


-- 게시글(post) 50만 건 대량 생성 프로시저
DELIMITER //
CREATE PROCEDURE insert_bulk_post()
BEGIN
    DECLARE i INT DEFAULT 1;
    SET autocommit = 0; -- 자동 커밋 취소
    
    WHILE i <= 500000 DO
        INSERT INTO post (member_id, content, like_count, created_at)
        VALUES (
            FLOOR(1 + (RAND() * 100)), -- 위에서 만든 1~100번 회원 중 랜덤 매칭
            CONCAT('테스트 게시글 내용입니다 #', i),
            FLOOR(RAND() * 1000), -- 0~999
            NOW() - INTERVAL FLOOR(RAND() * 30) DAY -- 현재로부터 0일 ~ 29일 전 날짜 생성
        );
        SET i = i + 1;

        -- 1만 건마다 중간 커밋하여 메모리 확보
        IF (i % 10000 = 0) THEN
            COMMIT;
        END IF;
    END WHILE;
    
    COMMIT;
    SET autocommit = 1;
END //
DELIMITER ;

-- 프로시저 실행 (50만 건 생성, 약 10~20초 소요)
CALL insert_bulk_post();