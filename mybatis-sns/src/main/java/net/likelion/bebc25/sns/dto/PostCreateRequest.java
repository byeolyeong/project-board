package net.likelion.bebc25.sns.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {
    private Long id;

//    @NotNull(message = "작성자 id는 필수입니다.")
    private Long memberId;

    @NotBlank(message = "본문 내용은 필수입니다.")
    @Size(max = 1000, message = "본문은 1000자 이하여야 합니다.")
    private String content;
    private String imageUrl;

    public PostCreateRequest(Long memberId, String content, String imageUrl) {
        this.memberId = memberId;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}