package net.likelion.bebc25.springboard.post.service;

import net.likelion.bebc25.springboard.post.dto.PageDto;
import net.likelion.bebc25.springboard.post.dto.PostDto;

import java.util.List;

public interface PostService {
    List<PostDto> getPosts();
    List<PostDto> searchPosts(String type, String keyword);
    PageDto<PostDto> searchPosts(String type, String keyword, int page, int size);
    PostDto getPost(int id);
    void writePost(PostDto post);
    void editPost(PostDto post);
    void removePost(int id);
}
