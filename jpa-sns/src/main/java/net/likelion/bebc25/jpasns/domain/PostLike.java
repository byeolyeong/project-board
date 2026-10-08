package net.likelion.bebc25.jpasns.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_post_like_member_post", columnNames = {"member_id", "post" +
                "_id"})
})
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false,  updatable = false)
    private LocalDateTime createdAt;

    public PostLike(Long memberId, Long postId) {
        this.memberId = memberId;
        this.postId = postId;
        this.createdAt = LocalDateTime.now();
    }
}
