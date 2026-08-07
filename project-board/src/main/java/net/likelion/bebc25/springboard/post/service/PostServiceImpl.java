package net.likelion.bebc25.springboard.post.service;

import net.likelion.bebc25.springboard.post.dto.PageDto;
import net.likelion.bebc25.springboard.post.dto.PostDto;
import net.likelion.bebc25.springboard.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(@Qualifier("jdbcTemplatePostRepository") PostRepository postRepository){
        this.postRepository = postRepository;
    }

    @Override
    public List<PostDto> getPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<PostDto> searchPosts(String type, String keyword) {
        return postRepository.search(type, keyword);
    }

    @Override
    public PageDto<PostDto> searchPosts(String type, String keyword, int page, int size) {
        int validPage = page < 1 ? 1 : page;
        int validSize = size < 1 ? 10 : size;
        int offset = (validPage - 1) * validSize;
        int totalElements = postRepository.count(type, keyword);
        List<PostDto> posts = postRepository.search(type, keyword, offset, validSize);
        return new PageDto<>(posts, validPage, validSize, totalElements, 5);
    }

    @Override
    public PostDto getPost(int id) {
        return postRepository.findById(id);
    }

    @Override
    @Transactional
    public void writePost(PostDto post) {
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void editPost(PostDto post) {
        postRepository.update(post);
    }

    @Override
    @Transactional
    public void removePost(int id) {
        postRepository.deleteById(id);
    }
}
