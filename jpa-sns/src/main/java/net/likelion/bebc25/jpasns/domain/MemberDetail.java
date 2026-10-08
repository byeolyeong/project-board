package net.likelion.bebc25.jpasns.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column(nullable = false, length = 1)
    private String marketing_agreed;

    public MemberDetail(Long id, String introduction, String address, String marketingAgreed) {
        this.id = id;
        this.introduction = introduction;
        this.address = address;
        this.marketing_agreed = marketingAgreed;
    }

    public MemberDetail(Long id, String introduction, String address) {
        this(id, introduction, address, "N");
    }
}
