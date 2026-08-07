package net.likelion.bebc25.springboard.post.repository;

import net.likelion.bebc25.springboard.post.dto.PostDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostRepositoryTest {

  @Autowired
  private PostRepository postRepository;

  @Test
  @DisplayName("제목으로 게시글 검색 테스트")
  void searchByTitle() {
    // given & when
    List<PostDto> posts = postRepository.search("title", "스프링");

    // then
    assertThat(posts).isNotEmpty();
    assertThat(posts).allMatch(post -> post.getTitle().contains("스프링"));
  }

  @Test
  @DisplayName("내용으로 게시글 검색 테스트")
  void searchByContent() {
    // given & when
    List<PostDto> posts = postRepository.search("content", "내용");

    // then
    assertThat(posts).isNotNull();
  }

  @Test
  @DisplayName("작성자로 게시글 검색 테스트")
  void searchByAuthor() {
    // given & when
    List<PostDto> posts = postRepository.search("author", "홍길동");

    // then
    assertThat(posts).isNotNull();
  }

  @Test
  @DisplayName("제목+내용으로 게시글 검색 테스트")
  void searchByTitleContent() {
    // given & when
    List<PostDto> posts = postRepository.search("titleContent", "게시글");

    // then
    assertThat(posts).isNotNull();
  }

  @Test
  @DisplayName("검색어가 없을 경우 전체 목록 반환 테스트")
  void searchWithEmptyKeyword() {
    // given & when
    List<PostDto> allPosts = postRepository.findAll();
    List<PostDto> searchPosts = postRepository.search("title", "");

    // then
    assertThat(searchPosts.size()).isEqualTo(allPosts.size());
  }

  @Test
  @DisplayName("페이징 조회 테스트")
  void searchWithPagination() {
    // given & when
    List<PostDto> pagePosts = postRepository.search(null, null, 0, 2);

    // then
    assertThat(pagePosts).isNotNull();
    assertThat(pagePosts.size()).isLessThanOrEqualTo(2);
  }

  @Test
  @DisplayName("게시글 개수 조회 테스트")
  void countTest() {
    // given & when
    int totalCount = postRepository.count(null, null);

    // then
    assertThat(totalCount).isGreaterThanOrEqualTo(0);
  }
}
