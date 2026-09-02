package net.likelion.bebc25.sns.dto;

import java.time.LocalDateTime;

public record PostResponseDto(
        Long id,
        Long memberId,
        String content,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}