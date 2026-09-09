package net.likelion.bebc25.sns.controller;

import jakarta.validation.Valid;
import net.likelion.bebc25.sns.dto.PostCreateRequest;
import net.likelion.bebc25.sns.dto.PostResponse;
import net.likelion.bebc25.sns.dto.PostSearchRequest;
import net.likelion.bebc25.sns.dto.PostUpdateRequest;
import net.likelion.bebc25.sns.security.principal.CustomUserDetails;
import net.likelion.bebc25.sns.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// @RestController
// REST API 요청을 처리하는 Controller
// JSON 데이터를 요청받고 JSON 형태로 응답함

// @RequestMapping
// 게시글 API의 공통 URL
// 실제 요청 주소: /api/v1/posts
@RestController
@RequestMapping("/api/v1/posts")
public class PostRestController {

    // 게시글 관련 실제 작업은 PostService에게 맡김
    private final PostService postService;

    // Spring이 PostService 객체를 자동으로 주입
    // → 의존성 주입(DI)
    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 목록 조회, 검색
    // GET /api/v1/posts
    // GET 요청을 처리
    // REST API에서 GET은 주로 데이터 조회에 사용
    @GetMapping
    public ResponseEntity<List<PostResponse>> getPostsList(
            // URL의 Query Parameter를 PostSearchRequest 객체에 담음
            // 예: /api/v1/posts?keyword=Spring
            @ModelAttribute PostSearchRequest searchRequest){

        // 검색어에 해당하는 게시글 목록 조회 ( 검색 조건을 Service에 전달하여 게시글을 조회 )
        List<PostResponse> posts = postService.searchPosts(searchRequest);
        // 조회 성공 → HTTP 200 OK와 게시글 목록을 반환한다.
        return ResponseEntity.ok(posts);
    }


    // 게시글 생성
    // POST /api/v1/posts
    // POST 요청을 처리
    // REST API에서 POST는 주로 새로운 데이터 생성에 사용
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            // HTTP Header에서 회원 ID를 가져옴
            // 예: X-Member-Id: 1
            // 현재는 로그인 기능 대신 임시로 사용함
//            @RequestHeader("X-Member-Id") Long memberId, // 임시 헤더에서 추출
            @AuthenticationPrincipal CustomUserDetails userDetails,

            // HTTP Body의 JSON 데이터를 PostCreateRequest 객체로 자동 변환.
            @Valid @RequestBody PostCreateRequest request // JSON 요청 바디를 객체로 자동 매핑

    ){
        // Header에서 가져온 회원 ID를 게시글 생성 요청 객체에 저장
        request.setMemberId(userDetails.getId());

        // 게시글 생성을 Service에게 요청함
        // 생성된 게시글 정보를 PostResponse로 반환받음
        PostResponse createPost = postService.createPost(request);

        // 생성된 게시글의 URL을 생성
        // 예: /api/v1/posts/10
        URI location = URI.create("/api/v1/posts/" + createPost.id());

        // 게시글 생성 성공
        // HTTP 201 Created + 생성된 게시글 정보 반환
        // location에는 새로 생성된 게시글의 주소가 들어감
        return ResponseEntity.created(location).body(createPost); // 201
    }

    // 게시글 한 건 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable("id") Long id){
        PostResponse post = postService.getPostById(id);

        return ResponseEntity.ok(post);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable("id") Long id,
//            @RequestHeader("X-Member-Id") Long memberId, // 임시 헤더에서 추출
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostUpdateRequest request){
        // 수정 전 게시글 정보 조회
        PostResponse post = postService.getPostById(id);

        // 본인의 게시글일지 확인
        if(!post.memberId().equals(userDetails.getId())){
            // 작성자 본인이 아니면 수정 불가
            // → 권한이 없는 사용자의 수정 요청을 차단한다.
            throw new IllegalStateException("본인의 게시글만 수정이 가능합니다.");
        }

        // 수정 작업
        postService.updatePost(id, request);

        // 수정된 게시글 조회
        PostResponse updatePost = postService.getPostById(id);

        // 200 응답 상태 코드와 게시글 정보로 응답
        return ResponseEntity.ok(updatePost);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
        @PathVariable("id") Long id,
//        @RequestHeader("X-Member-Id") Long memberId
        @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        // 수정 전 게시글 정보 조회
        PostResponse post = postService.getPostById(id);

        // 본인의 게시글일지 확인
        if(!post.memberId().equals(userDetails.getId())){
            // 작성자 본인이 아니면 삭제 불가
            // → 권한이 없는 사용자의 삭제 요청을 차단한다.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 삭제 작업
        postService.deletePost(id);

        // 200 응답 상태 코드와 게시글 정보로 응답
        return ResponseEntity.noContent().build();

    }
}

