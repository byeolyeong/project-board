package net.likelion.bebc25.sns.mapper;

import net.likelion.bebc25.sns.dto.PostCreateDto;
import net.likelion.bebc25.sns.dto.PostResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 자동으로 메서드 단위로 커밋됨
public class PostMapperTest {

    @Autowired
    private PostMapper postMapper;

    @Test
    @DisplayName("게시글 한 건 조회 테스트")
    void findByIdTest(){
        // 존재하는 1번 게시글을 조회
        PostResponseDto post = postMapper.findById(1L); // 1번 게시글을 찾음
        assertThat(post).isNotNull(); // 1번 게시글이 존재하는지 찾음
        assertThat(post.id()).isEqualTo(1L); //

        // 존재하지 않는 게시글 조회
        PostResponseDto notFoundPost = postMapper.findById(99999899999L);
        assertThat(notFoundPost).isNull();

    }

    @Test
    @DisplayName("게시글 등록 테스트")
    void saveTest(){
        PostCreateDto newPost = new PostCreateDto(1L,"신규 게시글 등록 테스트", "hello.png");

        // 게시글 등록
        postMapper.save(newPost);

        // 1번 사용자의 게시글 목록 조회
        List<PostResponseDto> memberPosts = postMapper.findByMemberId(1L);

        assertThat(memberPosts).isNotEmpty();

        // 방금 작성한 게시글 조회
        PostResponseDto lastPost = memberPosts.getFirst();

        // 조회된 마지막 글이 이전에 등록한 글과 같은 내용인가?
        assertThat(lastPost.content()).isEqualTo(newPost.getContent());
        assertThat(lastPost.memberId()).isEqualTo(newPost.getMemberId());
        assertThat(lastPost.imageUrl()).isEqualTo(newPost.getImageUrl());
    }

    @Test
    @DisplayName("작성자 ID 기반 게시글 목록 조회 테스트")
    void findByMemberIdTest() {
        // given: 1번 회원 ID 기준 조회
        Long targetMemberId = 1L;

        // when: 해당 회원의 게시글 목록 조회
        List<PostResponseDto> posts = postMapper.findByMemberId(targetMemberId);

        // then: 조회된 모든 게시글의 memberId가 1번인지 검증
        assertThat(posts).isNotNull();
        for (PostResponseDto post : posts) {
            assertThat(post.memberId()).isEqualTo(targetMemberId);
        }
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void updateTest() {
        // given: 1번 게시글 대상 수정할 본문과 이미지 준비
        Long targetPostId = 1L;
        String updatedContent = "수정 완료된 게시글 본문입니다.";
        String updatedImageUrl = "https://image.com/updated.jpg";

        // when: 게시글 수정 실행
        postMapper.update(targetPostId, updatedContent, updatedImageUrl);

        // then: 단건 조회 후 수정된 내용이 정상 반영되었는지 검증
        PostResponseDto updatedPost = postMapper.findById(targetPostId);
        assertThat(updatedPost).isNotNull();
        assertThat(updatedPost.content()).isEqualTo(updatedContent);
        assertThat(updatedPost.imageUrl()).isEqualTo(updatedImageUrl);
    }

    @Test
    @DisplayName("게시글 단건 삭제 테스트")
    void deleteByIdTest() {
        // given: 1번 회원의 신규 게시글을 먼저 등록하고 생성된 ID 확인
        PostCreateDto post = new PostCreateDto(1L, "삭제될 임시 게시글", null);
        postMapper.save(post);

        List<PostResponseDto> posts = postMapper.findByMemberId(1L);
        Long targetPostId = posts.getFirst().id();

        // when: 단건 삭제 실행
        postMapper.deleteById(targetPostId);

        // then: 삭제 후 단건 조회 시 null이 반환되는지 검증
        PostResponseDto deletedPost = postMapper.findById(targetPostId);
        assertThat(deletedPost).isNull();
    }
}
