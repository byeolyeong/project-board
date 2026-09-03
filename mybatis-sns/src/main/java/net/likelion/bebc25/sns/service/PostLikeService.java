package net.likelion.bebc25.sns.service;

import net.likelion.bebc25.sns.dto.LikeToggleResponseDto;

public interface PostLikeService {
    LikeToggleResponseDto toggleLike(Long memberId, Long postId);
}
