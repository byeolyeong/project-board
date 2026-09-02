package net.likelion.bebc25.sns.mapper;

import net.likelion.bebc25.sns.dto.PostCreateDto;
import net.likelion.bebc25.sns.dto.PostDetailResponseDto;
import net.likelion.bebc25.sns.dto.PostResponseDto;
import net.likelion.bebc25.sns.dto.PostSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    // 게시글 전체 목록 조회
    List<PostResponseDto> findAll();

    // ID 기반 게시글 단건 조회
    PostResponseDto findById(@Param("id") Long id);

    // 작성자 ID 기반 게시글 목록 조회
    List<PostResponseDto> findByMemberId(@Param("memberId") Long memberId);

    // 신규 게시글 등록 (Auto Increment ID 자동 바인딩)
    void save(PostCreateDto post);

    // 게시글 본문 및 이미지 수정
    void update(@Param("id") Long id, @Param("content") String content, @Param("imageUrl") String imageUrl);

    // 게시글 단건 삭제
    void deleteById(Long id);

    // 복합 ResultMap 조인 상세 조회 (게시글 + 작성자 + 댓글목록)
    PostDetailResponseDto findPostDetailById(Long id);

    // PostMapper.java 파일에 메서드 추가

    // 동적 검색 조건 및 정렬 기반 게시글 목록 조회
    List<PostResponseDto> searchPosts(PostSearchCondition condition);

    // 다중 게시글 ID 일괄 삭제 (foreach)
    void deleteByIds(@Param("idList") List<Long> idList);

    // 동적 SQL 정렬 기반 게시글 목록 조회
    List<PostResponseDto> findPostsWithSort(PostSearchCondition condition);

    // 동적 SQL 부분 수정
    void updateSelective(Map<String, Object> params);

    // 공통 SQL 조각을 이용한 단건 조회
    PostResponseDto findByIdWithInclude(Long id);
}