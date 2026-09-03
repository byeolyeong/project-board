package net.likelion.bebc25.sns.service;

import net.likelion.bebc25.sns.dto.PostCreateDto;
import net.likelion.bebc25.sns.dto.PostDetailResponseDto;
import net.likelion.bebc25.sns.dto.PostResponseDto;
import net.likelion.bebc25.sns.dto.PostSearchCondition;
import net.likelion.bebc25.sns.dto.PostUpdateDto;
import net.likelion.bebc25.sns.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    public PostServiceImpl(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPost(PostCreateDto dto) {
        postMapper.save(dto);
        return dto.getId();
    }

    @Override
    public PostResponseDto getPostById(Long id) {
        PostResponseDto post = postMapper.findById(id);
        if (post == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다. ID: " + id);
        }
        return post;
    }

    @Override
    public PostDetailResponseDto getPostDetailById(Long id) {
        PostDetailResponseDto detail = postMapper.findPostDetailById(id);
        if (detail == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다. ID: " + id);
        }
        return detail;
    }

    @Override
    public List<PostResponseDto> searchPosts(PostSearchCondition condition) {
        return postMapper.searchPosts(condition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePost(Long id, PostUpdateDto dto) {
        // 수정 대상 게시글 존재 여부 사전 검증
        if (postMapper.findById(id) == null) {
            throw new IllegalArgumentException("수정할 게시글이 존재하지 않습니다. ID: " + id);
        }
        postMapper.update(id, dto.content(), dto.imageUrl());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long id) {
        // 삭제 대상 게시글 존재 여부 사전 검증
        if (postMapper.findById(id) == null) {
            throw new IllegalArgumentException("삭제할 게시글이 존재하지 않습니다. ID: " + id);
        }
        postMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePosts(List<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            throw new IllegalArgumentException("삭제할 게시글 ID 목록이 비어있습니다.");
        }
        postMapper.deleteByIds(idList);
    }
}