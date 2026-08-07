package net.likelion.bebc25.springboard.post.controller;

import jakarta.servlet.http.HttpSession;
import net.likelion.bebc25.springboard.member.dto.SessionMemberDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.springboard.post.dto.PageDto;
import net.likelion.bebc25.springboard.post.dto.PostDto;
import net.likelion.bebc25.springboard.post.service.PostService;

@Controller
@Slf4j
@RequestMapping("/post")
public class BoardController {

    private final PostService postService;

    public BoardController(PostService postService){
        this.postService = postService;
    }

    // 게시글 목록 및 검색, 페이징 조회하는 컨트롤러
    @GetMapping("/list")
    public String getBoardList(@RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size,
                               @RequestParam(value = "type", required = false) String type,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               Model model){
        // 게시글 페이징 목록 조회
        PageDto<PostDto> pageResponse = postService.searchPosts(type, keyword, page, size);
        model.addAttribute("pageResponse", pageResponse);
        model.addAttribute("posts", pageResponse.getContent());
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        return "board/list";
    }

    // 게시글 상세 조회하는 컨트롤러
    @GetMapping("/detail")
    public String getDetail(@RequestParam("id") int id, Model model){
        PostDto post = postService.getPost(id);
        model.addAttribute("post", post);
        return "board/detail"; // 템플릿 파일 경로
    }

    // 게시글 등록 화면을 요청하는 컨트롤러
    @GetMapping("/write")
    public String getWriteForm(@ModelAttribute("postForm") PostDto post){ // 모델에 자동으로 주입까지 됨(postDto 이름으로)
        return "board/write";
    }

    // 게시글 수정 화면을 요청하는 컨트롤러
    @GetMapping("/edit")
    public String getEditForm(@RequestParam("id") int id, Model model){
        PostDto post = postService.getPost(id);
        model.addAttribute("postForm", post);
        return "board/write";
    }

    // 게시글 등록 요청을 처리하는 컨트롤러
    @PostMapping("/write")
    public String writePost(@Valid @ModelAttribute("postForm") PostDto post, // Validation 검증 대상 객체
                            BindingResult bindingResult){ // Validation 검증 결과 저장 객체(대상 객체 뒤에 기술해야 함)
        if(bindingResult.hasErrors()){ // 검증에 실패했을 경우
            return "board/write"; // 작성중이던 페이지로 다시 보낸다.
        }
        post.setMemberId(1);
        postService.writePost(post);
        return "redirect:/post/list"; // 브라우저에 list로 재요청하라고 응답
    }

    // 게시글 수정 요청을 처리하는 컨트롤러
    @PostMapping("/edit")
    public String editPost(@Valid @ModelAttribute("postForm") PostDto post,
                           BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "board/write";
        }
        postService.editPost(post);
        return "redirect:/post/detail?id=" + post.getId();
    }

    // 게시글 삭제 요청을 처리하는 컨트롤러
    @PostMapping("/delete")
    public String deletePost(@RequestParam int id, HttpSession session){
        PostDto postDto = postService.getPost(id);
        SessionMemberDto loginMember  = (SessionMemberDto) session.getAttribute("loginMember");

        // 삭제 권한 체크
        if(loginMember != null && (loginMember.getId() == postDto.getMemberId() || loginMember.getRole().equals("admin"))){
            postService.removePost(id);
        }


        return "redirect:/post/list";
    }
}