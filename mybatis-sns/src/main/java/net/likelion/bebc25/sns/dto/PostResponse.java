package net.likelion.bebc25.sns.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PostResponse(
        @Schema(description = "게시글 고유 식별자(PK)", example = "1")
        Long id,
        @Schema(description = "게시글 작성자 아이디", example = "1")
        Long memberId,
        @Schema(description = "게시글 본문 내용", example = "스프링 부트 학습 중..")
        String content,
        @Schema(description = "첨부 이미지 URL", example = "https://sample.com/images/hello.png", nullable = true)
        String imageUrl,
        @Schema(description = "좋아요 수", example = "0")
        int likeCount,
        @Schema(description = "게시글 작성 시간", example = "2026-09-08T04:23:00")
        LocalDateTime createdAt,
        @Schema(description = "게시글 수정 시간", example = "2026-09-08T04:23:00")
        LocalDateTime updatedAt
) {
    // 신규 게시글 등록 요청 DTO로 게시글 응답 DTD를 생성하는 팩토리 메서드
    public static PostResponse from(PostCreateRequest dto){
        return new PostResponse(
                dto.getId(),
                dto.getMemberId(),
                dto.getContent(),
                dto.getImageUrl(),
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}