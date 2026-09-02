package net.likelion.bebc25.sns.mapper;

import net.likelion.bebc25.sns.dto.PostCreateDto;
import net.likelion.bebc25.sns.dto.PostResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MyBatis Mapper 인터페이스
 *
 * @Mapper
 * > MyBatis가 이 인터페이스를 Mapper로 인식하도록 등록한다.
 *
 * 실제 SQL은 PostMapper.xml에서 작성하고,
 * 여기서는 SQL을 호출하기 위한 메서드만 선언한다.
 **/
@Mapper
public interface PostMapper {
    /**
     * ============================================================
     * 1. 게시글 등록
     * ============================================================
     *
     * PostCreateDto 객체 하나를 파라미터로 전달한다.
     *
     * DTO 하나를 전달하기 때문에 @Param을 생략할 수 있다.
     *
     * PostMapper.xml의
     * <insert id="save">
     * 와 연결된다.
     *
     * save(post) 호출
     * → PostCreateDto의 값 전달
     * → INSERT SQL 실행
     * → DB에서 생성된 id를 DTO의 id에 저장
     **/
    // 1. 단일 DTO 바인딩 (@Param 생략)
    void save(PostCreateDto post);

    /**
     * ============================================================
     * 2. 게시글 단건 조회
     * ============================================================
     *
     * 게시글 id를 전달받아 하나의 게시글을 조회한다.
     *
     * @Param("id")
     * → XML에서 #{id}라는 이름으로 사용할 수 있도록 지정한다.
     *
     * 예)
     * findById(10L)
     * → WHERE id = 10
     *
     * 조회 결과는 PostResponseDto 객체 하나로 반환된다.
     **/
    // 2. 단일 기본형 바인딩 (@Param 명시)
    // 게시글 id로 게시글 정보 조회
    // @Param 으로 PostMapper.xml 파일에서 id 라는 이름을 사용할 수 있게 지정
    // 기본 타입 하나일 때, @Param을 붙이지 않아도 되지만, 다중 타입일때는 무조건 지정해야함
    PostResponseDto findById(@Param("id") Long id);

    /**
     * ============================================================
     * 3. 특정 회원의 게시글 목록 조회
     * ============================================================
     *
     * memberId를 전달받아
     * 해당 회원이 작성한 게시글 목록을 조회한다.
     *
     * 반환 타입이 List이므로 여러 개의 게시글이 반환된다.
     *
     * @Param("memberId")
     * → XML에서 #{memberId}라는 이름으로 사용할 수 있다.
     **/
    // 3. 단일 기본형 파라미터 기반 목록 조회
    List<PostResponseDto> findByMemberId(@Param("memberId") Long memberId);

    /**
     * ============================================================
     * 4. 게시글 수정
     * ============================================================
     *
     * 수정에 필요한 값이 3개이므로
     * 각각 @Param을 지정한다.
     *
     * id
     * → 수정할 게시글의 id
     *
     * content
     * → 수정할 게시글 내용
     *
     * imageUrl
     * → 수정할 이미지 URL
     *
     * XML에서는 다음과 같이 사용할 수 있다.
     *
     * #{id}
     * #{content}
     * #{imageUrl}
     **/
    // 4. 다중 파라미터 바인딩 (@Param 필수)
    void update(@Param("id") Long id, @Param("content") String content, @Param("imageUrl") String imageUrl);

    /**
     * ============================================================
     * 5. 게시글 삭제
     * ============================================================
     *
     * 게시글 id를 전달받아 해당 게시글을 삭제한다.
     *
     * 예)
     * deleteById(10L)
     * → id가 10인 게시글 삭제
     **/
    // 5. 단일 기본형 단건 삭제
    void deleteById(@Param("id") Long id);
}
