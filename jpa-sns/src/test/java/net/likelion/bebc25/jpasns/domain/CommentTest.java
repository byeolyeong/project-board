package net.likelion.bebc25.jpasns.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CommentTest {

    @Autowired
    private EntityManager em;
    private Long newMemberId;
    private Long newPostId;
    private Long newCommentId;

    @BeforeEach // 각 테스트 케이스 실행 전 실행할 코드
    void setUp(){
        // 1. 회원 등록
        Member member = new Member("haru@test.com", "1234", "하루");
        em.persist(member); // insert 쿼리문이 실행되어 회원이 등록됨
        this.newMemberId = member.getId(); // 새로 등록된 회원 ID

        // 2. 게시글 등록
        Post post = new Post(newMemberId, "멍멍");
        em.persist(post); // insert 쿼리문이 생성되고 게시글이 등록됨
        this.newPostId = post.getId(); // 새로 등록된 게시글 ID

        // 3. 댓글 등록
        Comment comment = new Comment(newMemberId, newPostId, "댓글1");
        em.persist(comment); // insert 쿼리문이 실행되고 댓글이 등록됨
        this.newCommentId = comment.getId(); // 새로 등록된 댓글

        em.clear(); // 캐시 삭제
    }

    @Test
    @DisplayName("댓글 등록")
//    @Commit //테스트 종료 후 롤백 시키지 않고 커밋함
    void create(){
        Comment comment = new Comment(newMemberId, newPostId, "댓글2");
        em.persist(comment); // insert 쿼리문이 실행되고 댓글이 등록됨

        em.clear(); // 캐시 비우기

        // 등록한 댓글 조회
        Comment findComment = em.find(Comment.class, comment.getId()); // select 쿼리문이 실행되어 댓글이 조회됨

        // 조회한 댓글 정보를 이용해서 댓글 등록 검증
        assertThat(findComment).isNotNull();
        assertThat(findComment.getId()).isEqualTo(comment.getId());
        assertThat(findComment.getPostId()).isEqualTo(newPostId);
        assertThat(findComment.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("댓글 조회")
    void read(){
        Comment findComment = em.find(Comment.class, newCommentId);

        // 영속성 컨텍스트의 1차 캐시값에서 즉시 반환되며 select 쿼리를 실행하지 않음
        Comment findComment2 = em.find(Comment.class, newCommentId);

        assertThat(findComment).isSameAs(findComment2); // 동일한 주소를 가지고 있는 객체인지 여부(==)

        assertThat(findComment).isNotNull();
        assertThat(findComment.getContent()).isEqualTo("댓글1");
        assertThat(findComment.getMemberId()).isEqualTo(newMemberId);
        assertThat(findComment.getMemberId()).isEqualTo(newPostId);
    }

    @Test
    @DisplayName("댓글 수정")
    void update(){
        Comment targetComment = em.find(Comment.class, newCommentId);

        // 준영속 상태로 엔티티 변환
        em.detach(targetComment);

        // 엔티티의 속성이 수정되면 update 쿼리가 생성
        // 실행은 바로 되지 않고 버퍼에 저장되고 커밋 직전에 실행됨(쓰기 지연)
        targetComment.changeContent("수정된 댓글");

        em.flush(); // 버퍼의 데이터를 DB에 전송(실제 update 실행)
        em.clear();

        Comment updatedComment = em.find(Comment.class, newCommentId);
        assertThat(updatedComment.getContent()).isEqualTo("수정된 댓글");
    }

    @Test
    @DisplayName("댓글 삭제")
    void delete(){
        Comment targetComment = em.find(Comment.class, newCommentId);
        em.remove(targetComment); // delete 쿼리문 생성해서 쓰기 지연 SQL 버퍼에 저장

        Comment findComment = em.find(Comment.class, newCommentId);
        assertThat(findComment).isNull();
    }
}
