package net.likelion.bebc25.sns.controller;

import jakarta.validation.Valid;
import net.likelion.bebc25.sns.dto.PostCreateRequest;
import net.likelion.bebc25.sns.dto.PostResponse;
import net.likelion.bebc25.sns.dto.PostSearchRequest;
import net.likelion.bebc25.sns.dto.PostUpdateRequest;
import net.likelion.bebc25.sns.security.principal.CustomUserDetails;
import net.likelion.bebc25.sns.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


// 게시글 관련 REST API 요청을 처리하는 Controller
@RestController

// 게시글 API의 공통 URL
// 실제 요청 주소: /api/v1/posts
@RequestMapping("/api/v1/posts")
public class PostRestController {

    // 게시글 관련 비즈니스 로직을 처리하는 Service
    private final PostService postService;


    // PostService를 생성자를 통해 주입받는다.
    // → 생성자 의존성 주입(DI)
    public PostRestController(PostService postService) {
        this.postService = postService;
    }


    // =========================================================
    // 게시글 목록 조회 및 검색
    // GET /api/v1/posts
    // =========================================================

    // 게시글 목록 조회 및 검색 요청을 처리한다.
    @GetMapping
    public ResponseEntity<List<PostResponse>> getPostList(

            // Query Parameter를 PostSearchRequest 객체에 자동으로 매핑한다.
            // 예: /api/v1/posts?keyword=Spring
            @ModelAttribute PostSearchRequest searchRequest) {

        // 검색 조건을 Service에 전달하여 게시글 목록을 조회한다.
        List<PostResponse> posts =
                postService.searchPosts(searchRequest);

        // 조회 성공 → HTTP 200 OK와 게시글 목록을 반환한다.
        return ResponseEntity.ok(posts);
    }


    // =========================================================
    // 게시글 등록
    // POST /api/v1/posts
    // =========================================================

    // 새로운 게시글을 등록한다.
    @PostMapping
    public ResponseEntity<PostResponse> createPost(

            // SecurityContext에 저장된 현재 로그인 사용자의 정보를 가져온다.
            // 로그인 인증이 완료된 경우 CustomUserDetails를 받을 수 있다.
            //
            // 기존의 X-Member-Id Header 방식 대신
            // Spring Security의 인증 정보를 사용한다.
            @AuthenticationPrincipal CustomUserDetails userDetails,

            // HTTP Body의 JSON 데이터를
            // PostCreateRequest 객체로 자동 변환한다.
            //
            // @Valid를 사용하여 DTO의 @NotBlank, @Size 등의
            // 유효성 검증도 함께 수행한다.
            @Valid @RequestBody PostCreateRequest request
    ) {

        // 현재 로그인한 사용자의 정보를 확인한다.
        // 학습 및 디버깅을 위한 출력이다.
        System.out.println(userDetails.getMember());

        // 인증된 사용자의 ID를 게시글 작성자 ID로 설정한다.
        // 클라이언트가 작성자 ID를 직접 전달하지 않도록 한다.
        request.setMemberId(userDetails.getId());

        // 게시글 생성을 Service에 요청한다.
        PostResponse createdPost =
                postService.createPost(request);

        // 생성된 게시글의 상세 조회 URL을 만든다.
        // 예: /api/v1/posts/10
        URI location =
                URI.create("/api/v1/posts/" + createdPost.id());

        // 게시글 생성 성공
        // HTTP 201 Created + Location Header + 생성된 게시글 정보 반환
        return ResponseEntity
                .created(location)
                .body(createdPost);
    }


    // =========================================================
    // 게시글 한 건 조회
    // GET /api/v1/posts/{id}
    // =========================================================

    // 게시글 ID를 이용하여 게시글 하나를 조회한다.
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(

            // URL 경로의 {id} 값을 Long 타입으로 가져온다.
            // 예: /api/v1/posts/10 → id = 10
            @PathVariable("id") Long id
    ) {

        // 전달받은 ID를 이용하여
        // Service 레이어의 게시글 조회 메서드를 호출한다.
        PostResponse post =
                postService.getPostById(id);

        // 조회된 게시글을 HTTP 200 OK로 반환한다.
        return ResponseEntity.ok(post);
    }


    // =========================================================
    // 게시글 수정
    // PUT /api/v1/posts/{id}
    // =========================================================

    // 게시글 수정 요청을 처리한다.
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(

            // 수정할 게시글의 ID를 URL에서 가져온다.
            @PathVariable("id") Long id,

            // SecurityContext에서 현재 로그인한 사용자의 정보를 가져온다.
            @AuthenticationPrincipal CustomUserDetails userDetails,

            // 수정할 게시글 내용을 JSON Body에서 가져온다.
            // @Valid를 이용해 입력값 유효성 검증을 수행한다.
            @Valid @RequestBody PostUpdateRequest request
    ) {


        // =========================================================
        // 게시글 작성자 확인
        // =========================================================

        // 기존에는 X-Member-Id Header에서 회원 ID를 가져와
        // Controller에서 직접 작성자 여부를 확인했다.
        //
        // 현재는 이 로직을 주석 처리한 상태이다.
        // 실제 권한 검사는 Service 레이어에서 처리하도록 구성할 수 있다.

//        // 수정 전에 게시글 정보 조회
//        PostResponse post = postService.getPostById(id);
//
//        // 현재 로그인한 사용자와 게시글 작성자가 같은지 확인
//        if (!post.memberId().equals(userDetails.getId())) {
//
//            // 본인의 게시글이 아니면 수정할 수 없도록 예외 발생
//            throw new IllegalStateException(
//                    "본인의 게시글만 수정이 가능합니다."
//            );
//        }


        // 게시글 수정 작업을 Service에 요청한다.
        postService.updatePost(id, request);


        // 수정된 게시글을 다시 조회한다.
        // 수정된 최신 데이터를 응답하기 위해 사용한다.
        PostResponse updatedPost =
                postService.getPostById(id);


        // 수정 성공 → HTTP 200 OK
        // 수정된 게시글 정보를 반환한다.
        return ResponseEntity.ok(updatedPost);
    }


    // =========================================================
    // 게시글 삭제
    // DELETE /api/v1/posts/{id}
    // =========================================================

    // 게시글 삭제 요청을 처리한다.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(

            // 삭제할 게시글의 ID를 URL에서 가져온다.
            @PathVariable("id") Long id,

            // SecurityContext에서 현재 로그인한 사용자의 정보를 가져온다.
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        // 게시글 삭제 작업을 Service에 요청한다.
        postService.deletePost(id);

        // 삭제 성공
        // HTTP 204 No Content를 반환한다.
        // 삭제 API이므로 별도의 응답 Body는 반환하지 않는다.
        return ResponseEntity.noContent().build();
    }

}