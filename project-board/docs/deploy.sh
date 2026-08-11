# 로컬에서 실행
# 로컬의 빌드된 최종 코드를 EC2에 업로드
scp -i "first-key.pem" build/libs/spring-board-0.0.1-SNAPSHOT.jar ec2-user@54.116.119.148://home/ec2-user

# EC2에 SSH로 원격 접속
ssh -i "first-key.pem" ec2-user@54.116.119.148

##### EC2에서 실행 ######
#자바 서버 실행
java -jar spring-board-0.0.1-SNAPSHOT.jar

#MySQL 클라이언트 RDS 서비스에 접속
mysql -h board-db.cl0auo426nog.ap-northeast-2.rds.amazonaws.com -u admin -p

CREATE DATABASE board_db;

CREATE USER 'board_app'@'%' IDENTIFIED BY 'Board123!';

GRANT ALL PRIVILEGES ON board_db.* TO 'board_app'@'%';

FLUSH PRIVILEGES;

EXIT;


