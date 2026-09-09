package net.likelion.bebc25.sns.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.likelion.bebc25.sns.dto.ApiErrorResponse;
import net.likelion.bebc25.sns.dto.PostCreateRequest;
import net.likelion.bebc25.sns.dto.PostResponse;
import net.likelion.bebc25.sns.dto.PostSearchRequest;
import net.likelion.bebc25.sns.dto.PostUpdateRequest;
import net.likelion.bebc25.sns.service.PostService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// Swagger/OpenAPI 문서에서 게시글 API 그룹의 정보를 설정함
// Swagger UI에서 "SNS 게시글 API"라는 이름으로 표시됨
@Tag(name = "SNS 게시글 API", description = "피드 게시글 등록, 조회, 수정, 삭제를 담당하는 REST 컨트롤러")
// 이 Controller에서 발생할 수 있는 공통 500 에러 응답을 정의함
// 별도의 메서드에 지정하지 않아도 이 Controller의 API 문서에 적용된다.
@ApiResponse(
        responseCode = "500",
        description = "내부 서버 오류",
        // 500 오류 응답의 JSON 구조를 ApiErrorResponse 기준으로 문서화함
        content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
)

// REST API 요청을 처리하는 Controller
// 반환값은 주로 JSON 형태로 응답
//@RestController
// 게시글 API의 공통 URL
// 실제 요청 주소: /api/v1/posts
@RequestMapping("/api/v1/posts")
public class PostRestControllerSwagger {

    private final PostService postService;

    public PostRestControllerSwagger(PostService postService) {
        this.postService = postService;
    }

    // Swagger UI에 해당 API의 설명을 표시한다.
    @Operation(summary = "게시글 목록 조회 및 검색", description = "검색 키워드 및 정렬 조건에 부합하는 게시글 목록을 반환합니다.")
    // 정상적인 응답 상태 코드를 Swagger 문서에 표시한다.
    @ApiResponse(responseCode = "200", description = "목록 조회 성공")
    // GET 요청을 처리한다.
    @GetMapping
    public ResponseEntity<List<PostResponse>> getPostList(
            // 검색 조건을 Query Parameter로 받는다.
            // @ParameterObject를 사용하면 Swagger UI에서
            // PostSearchRequest의 필드들을 각각의 요청 파라미터로 보여준다.
            @ParameterObject
            @ModelAttribute PostSearchRequest searchRequest) {
        // 검색 조건을 Service에 전달하고 게시글 목록을 조회한다.
        return ResponseEntity.ok(postService.searchPosts(searchRequest));
    }

    // Swagger UI에 API의 용도와 설명을 표시한다
    @Operation(summary = "게시글 단건 상세 조회", description = "기본 키 ID에 해당하는 게시글의 상세 정보를 조회합니다.")
    // 이 API에서 발생할 수 있는 여러 HTTP 응답을 정의
    @ApiResponses({
            // 게시글 조회 성공 → HTTP 200 OK
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            // 게시글이 존재하지 않는 경우 → HTTP 404 Not Found
            @ApiResponse(
                    responseCode = "404",
                    description = "게시글이 존재하지 않음",
                    // 404 오류 응답의 JSON 구조를 ApiErrorResponse로 문서화한다.
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostDetail(
            // Swagger UI에서 Path Variable에 대한 설명과 예시를 표시한다.
            @Parameter(description = "조회할 게시글 ID", example = "1")
            // URL의 {id} 부분을 Long 타입의 id 변수에 전달한다
            @PathVariable("id") Long id) {
        // 게시글 ID를 Service에 전달하여 게시글을 조회한다.
        // 조회 성공 → HTTP 200 OK
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // Swagger UI에 API의 용도와 설명을 표시한다
    @Operation(summary = "신규 게시글 등록", description = "회원 ID와 게시글 본문, 이미지 URL을 전달받아 피드에 등록합니다.")
    // 게시글 생성 API에서 발생할 수 있는 응답을 정의한다.
    @ApiResponses({
            // 게시글 생성 성공 → HTTP 201 Created
            @ApiResponse(
                    responseCode = "201",
                    description = "게시글 생성 성공",
                    // 201 응답의 Location Header에 생성된 게시글의 URI가 들어간다는 것을 문서화한다
                    headers = @Header(name = "Location", description = "생성된 게시글의 상세 조회 URI 경로", schema = @Schema(type = "string"))
                    // Header 값의 데이터 타입을 String으로 지정한다.
            ),
            // 입력값 검증 실패 → HTTP 400 Bad Request
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 유효성 검증 실패",
                    // 400 오류 응답은 ApiErrorResponse 형태로 반환된다는 것을 문서화한다.
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            // Swagger UI에 Header 값의 설명과 예시를 표시한다.
            @Parameter(description = "작성자 회원 ID", example = "1")
            // HTTP Header의 X-Member-Id 값을 가져온다.
            // 예: X-Member-Id: 1
            @RequestHeader("X-Member-Id") Long memberId,
            // JSON Request Body를 PostCreateRequest 객체로 변환한다.
            // @Valid를 사용하여 DTO의 @NotNull, @NotBlank, @Size 등의
            // 유효성 검증을 수행한다.
            @Valid @RequestBody PostCreateRequest request) {
        // 임시 Header에서 전달받은 회원 ID를 게시글 생성 DTO에 저장한다.
        request.setMemberId(memberId); // 임시 헤더의 회원 식별자를 모델(DTO)에 주입
        // 게시글 생성을 Service에 요청한다.
        PostResponse createdPost = postService.createPost(request);
        // 생성된 게시글의 상세 조회 URI를 만든다.
        // 예: /api/v1/posts/10
        URI location = URI.create("/api/v1/posts/" + createdPost.id());
        // HTTP 201 Created와 Location Header를 함께 반환한다.
        // body에는 생성된 게시글 정보를 담는다.
        return ResponseEntity.created(location).body(createdPost);
    }

    // Swagger UI에 게시글 수정 API의 설명을 표시한다.
    @Operation(summary = "게시글 수정", description = "게시글 ID와 수정할 본문 내용을 전달받아 데이터를 갱신합니다.")
    // 게시글 수정 API에서 발생할 수 있는 응답을 정의한다.
    @ApiResponses({
            // 수정 성공 → HTTP 200 OK
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 유효성 검증 실패",
                    // 입력값 검증 실패 → HTTP 400 Bad Request
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            // 본인이 작성한 게시글이 아닌 경우 → HTTP 403 Forbidden
            @ApiResponse(
                    responseCode = "403",
                    description = "본인 작성 게시글이 아니므로 수정 권한 없음",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            // 수정하려는 게시글이 존재하지 않는 경우 → HTTP 404 Not Found
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 대상 게시글이 존재하지 않음",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })

    // PUT 요청을 처리한다.
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            // Swagger UI에 회원 ID에 대한 설명과 예시를 표시한다.
            @Parameter(description = "작성자 회원 ID", example = "1")
            // HTTP Header에서 현재 회원의 ID를 가져온다.
            @RequestHeader("X-Member-Id") Long memberId,
            // Swagger UI에 Path Variable의 설명과 예시를 표시한다.
            @Parameter(description = "수정할 게시글 ID", example = "1")
            // URL의 {id} 부분을 게시글 ID로 받는다.
            @PathVariable("id") Long id,
            // JSON Body를 PostUpdateRequest로 변환하고 유효성 검증한다.
            @Valid @RequestBody PostUpdateRequest request) {
        // 수정하려는 게시글을 먼저 조회
        PostResponse post = postService.getPostById(id);
        // 게시글 작성자와 현재 요청한 회원의 ID가 같은지 확인한다.
        if (!post.memberId().equals(memberId)) {
            // 본인이 작성한 게시글이 아니면 수정할 수 없다
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // 권한 확인이 완료되면 게시글을 수정한다.
        postService.updatePost(id, request);
        // 수정된 게시글을 다시 조회한다.
        PostResponse updatedPost = postService.getPostById(id);
        // 수정 성공 → HTTP 200 OK와 수정된 게시글을 반환한다.
        return ResponseEntity.ok(updatedPost);
    }

    // Swagger UI에 게시글 삭제 API의 설명을 표시한다.
    @Operation(summary = "게시글 삭제", description = "게시글 ID를 전달받아 해당 자원을 삭제합니다.")
    // 게시글 삭제 API에서 발생할 수 있는 응답을 정의한다.
    @ApiResponses({
            // 삭제 성공 → HTTP 204 No Content
            @ApiResponse(
                    responseCode = "204",
                    description = "게시글 삭제 완료",
                    // 204 응답은 Body를 사용하지 않으므로 응답 스키마를 숨긴다.
                    content = @Content(schema = @Schema(hidden = true))
            ),
            // 본인이 작성한 게시글이 아닌 경우 → HTTP 403 Forbidden
            @ApiResponse(
                    responseCode = "403",
                    description = "본인 작성 게시글이 아니므로 삭제 권한 없음",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            // 삭제하려는 게시글이 존재하지 않는 경우 → HTTP 404 Not Found
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 대상 게시글이 존재하지 않음",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })

    // DELETE 요청을 처리한다.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            // Swagger UI에 회원 ID에 대한 설명과 예시를 표시한다.
            @Parameter(description = "작성자 회원 ID", example = "1")
            // HTTP Header에서 현재 회원의 ID를 가져온다
            @RequestHeader("X-Member-Id") Long memberId,
            // Swagger UI에 삭제할 게시글 ID에 대한 설명과 예시를 표시한다
            @Parameter(description = "삭제할 게시글 ID", example = "1")
            // URL의 {id} 부분을 게시글 ID로 받는다.
            @PathVariable("id") Long id) {
        // 삭제하려는 게시글을 먼저 조회한다.
        PostResponse post = postService.getPostById(id);
        // 게시글 작성자와 현재 요청한 회원의 ID가 같은지 확인한다.
        if (!post.memberId().equals(memberId)) {
            // 본인이 작성한 게시글이 아니면 삭제할 수 없다.
            throw new IllegalStateException("본인의 게시글만 삭제 가능합니다.");
        }
        // 권한 확인이 완료되면 게시글을 삭제한다.
        postService.deletePost(id);
        // 삭제 성공 → HTTP 204 No Content를 반환한다.
        return ResponseEntity.noContent().build();
    }
}