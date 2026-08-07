package net.likelion.bebc25.springboard.post.repository;

import net.likelion.bebc25.springboard.post.dto.PostDto;

import java.util.List;

public interface PostRepository {
    List<PostDto> findAll();
    List<PostDto> search(String type, String keyword);
    List<PostDto> search(String type, String keyword, int offset, int limit);
    int count(String type, String keyword);
    PostDto findById(int id);
    void save(PostDto post);
    void update(PostDto post);
    void deleteById(int id);
}
