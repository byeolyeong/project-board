package net.likelion.bebc25.sns.dto;

public record MemberResponseDto(
        Long id,
        String nickname,
        String profileImage
) {}