package net.likelion.bebc25.sns.mapper;

import org.apache.ibatis.annotations.Param;

public interface PostLikeMapper {

    // 좋아요 등록
    void insertLike(@Param("memberId") Long memberId, @Param("postId") Long PostId);

    // 좋아요 취소
    void deleteLike(@Param("memberId") Long memberId, @Param("postId") Long PostId);

    // 특정 회원의 게시글 좋아요 등록 여부 조회(0, 1)
    void countLike(@Param("memberId") Long memberId, @Param("postId") Long PostId);

}
