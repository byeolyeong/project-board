-- 1. jpa_db 데이터베이스 생성
DROP DATABASE IF EXISTS `jpa_db`;
CREATE DATABASE IF NOT EXISTS `jpa_db`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 2. 사용자 계정 생성
DROP USER IF EXISTS 'jpa-app';
CREATE USER IF NOT EXISTS 'jpa-app'@'%' IDENTIFIED BY 'Jpa1234!';

-- 3. jpa_db에 대한 모든 권한 부여
GRANT ALL PRIVILEGES ON `jpa_db`.* TO 'jpa-app'@'%';
