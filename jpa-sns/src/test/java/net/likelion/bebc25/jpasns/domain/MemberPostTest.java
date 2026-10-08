package net.likelion.bebc25.jpasns.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
// 원래는 테스트는 하나씩 만들어야함
public class MemberPostTest {
    @Autowired
    private EntityManager em;

    private Long newMemberId;

    @BeforeEach // 단위테스트할 때 사전에 해야할 작업이 있으면 해당 어노테이션 사용
    void setUp(){
        // 회원 등록
        Member member = new Member("test1@test.com", "1234", "테스터1");
        em.persist(member); // insert 구문 생성

        newMemberId = member.getId();

        em.flush(); // 쿼리 실행
        em.clear(); // 캐시 삭제

    }

    @Test
    @DisplayName("회원 조회")
    void memberFind(){
        Member findMember = em.find(Member.class, newMemberId); // select

        assertThat(findMember).isNotNull();
        assertThat(findMember.getEmail()).isEqualTo("test1@test.com");
        assertThat(findMember.getRole()).isEqualTo("USER");
        assertThat(findMember.getId()).isEqualTo(newMemberId);
    }

    @Test
    @DisplayName("게시글 등록 및 조회")
    void postSaveAndFind(){
        // 게시글 등록
        Post post = new Post(newMemberId, "첫번째 게시글");
        em.persist(post);

        em.flush();
        em.clear();

        // 등록한 게시글 조회
        Post findPost = em.find(Post.class, post.getId());
        assertThat(findPost).isNotNull();
        assertThat(findPost.getContent()).isEqualTo("첫번째 게시글");
        assertThat(findPost.getMemberId()).isEqualTo(newMemberId);
        assertThat(findPost.getCreatedAt()).isNotNull();
    }


}
