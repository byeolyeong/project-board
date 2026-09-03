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

    // 1. 게시글 전체 목록 조회
    List<PostResponseDto> findAll();

    // 2. ID 기반 게시글 단건 조회
    PostResponseDto findById(@Param("id") Long id);

    // 3. 작성자 ID 기반 게시글 목록 조회
    List<PostResponseDto> findByMemberId(@Param("memberId") Long memberId);

    // 4. 신규 게시글 등록 (Auto Increment ID 자동 바인딩)
    void save(PostCreateDto post);

    // 5. 게시글 본문 및 이미지 수정
    void update(@Param("id") Long id, @Param("content") String content, @Param("imageUrl") String imageUrl);

    // 6. 게시글 단건 삭제
    void deleteById(Long id);

    // 7. 복합 ResultMap 조인 상세 조회 (게시글 + 작성자 + 댓글목록)
    PostDetailResponseDto findPostDetailById(Long id);

    // 8. 다중 조건 동적 검색 (<where>, <if>)
    List<PostResponseDto> searchPosts(PostSearchCondition condition);

    // 9. 동적 정렬 분기 조회 (<choose>, <when>, <otherwise>)
    List<PostResponseDto> findPostsWithSort(PostSearchCondition condition);

    // 10. 동적 부분 수정 (<set>, <if>)
    void updateSelective(Map<String, Object> params);

    // 11. 다중 ID 일괄 삭제 (<foreach>)
    void deleteByIds(@Param("idList") List<Long> idList);
}