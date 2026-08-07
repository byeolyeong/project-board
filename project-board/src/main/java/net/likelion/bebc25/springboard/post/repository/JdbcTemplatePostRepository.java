package net.likelion.bebc25.springboard.post.repository;

import net.likelion.bebc25.springboard.post.dto.PostDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcTemplatePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplatePostRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<PostDto> postRowMapper = (ResultSet rs, int rowNum) -> {
        return PostDto.builder()
                    .id(rs.getInt("id"))
                    .title(rs.getString("title"))
                    .author(rs.getString("author"))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .content(rs.getString("content"))
                    .secret(rs.getBoolean("secret"))
                    .memberId(rs.getInt("member_id"))
                    .build();
    };

    @Override
    public List<PostDto> findAll() {
        return jdbcTemplate.query("SELECT * FROM post ORDER BY id DESC", postRowMapper);
    }

    @Override
    public List<PostDto> search(String type, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String searchKeyword = "%" + keyword.trim() + "%";
        if ("title".equals(type)) {
            return jdbcTemplate.query("SELECT * FROM post WHERE title LIKE ? ORDER BY id DESC", postRowMapper, searchKeyword);
        } else if ("content".equals(type)) {
            return jdbcTemplate.query("SELECT * FROM post WHERE content LIKE ? ORDER BY id DESC", postRowMapper, searchKeyword);
        } else if ("author".equals(type)) {
            return jdbcTemplate.query("SELECT * FROM post WHERE author LIKE ? ORDER BY id DESC", postRowMapper, searchKeyword);
        } else {
            return jdbcTemplate.query("SELECT * FROM post WHERE title LIKE ? OR content LIKE ? ORDER BY id DESC", postRowMapper, searchKeyword, searchKeyword);
        }
    }

    @Override
    public List<PostDto> search(String type, String keyword, int offset, int limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return jdbcTemplate.query("SELECT * FROM post ORDER BY id DESC LIMIT ? OFFSET ?", postRowMapper, limit, offset);
        }
        String searchKeyword = "%" + keyword.trim() + "%";
        if ("title".equals(type)) {
            return jdbcTemplate.query("SELECT * FROM post WHERE title LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?", postRowMapper, searchKeyword, limit, offset);
        } else if ("content".equals(type)) {
            return jdbcTemplate.query("SELECT * FROM post WHERE content LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?", postRowMapper, searchKeyword, limit, offset);
        } else if ("author".equals(type)) {
            return jdbcTemplate.query("SELECT * FROM post WHERE author LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?", postRowMapper, searchKeyword, limit, offset);
        } else {
            return jdbcTemplate.query("SELECT * FROM post WHERE title LIKE ? OR content LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?", postRowMapper, searchKeyword, searchKeyword, limit, offset);
        }
    }

    @Override
    public int count(String type, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post", Integer.class);
            return count != null ? count : 0;
        }
        String searchKeyword = "%" + keyword.trim() + "%";
        if ("title".equals(type)) {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post WHERE title LIKE ?", Integer.class, searchKeyword);
            return count != null ? count : 0;
        } else if ("content".equals(type)) {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post WHERE content LIKE ?", Integer.class, searchKeyword);
            return count != null ? count : 0;
        } else if ("author".equals(type)) {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post WHERE author LIKE ?", Integer.class, searchKeyword);
            return count != null ? count : 0;
        } else {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post WHERE title LIKE ? OR content LIKE ?", Integer.class, searchKeyword, searchKeyword);
            return count != null ? count : 0;
        }
    }

    @Override
    public PostDto findById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM post WHERE id = ?", postRowMapper, id);
    }

    @Override
    public void save(PostDto post) {
        jdbcTemplate.update("INSERT INTO post (title, author, content, secret, member_id) VALUES (?, ?, ?, ?, ?)"
                , post.getTitle()
                , post.getAuthor()
                , post.getContent()
                , post.isSecret()
                , post.getMemberId());
    }

    @Override
    public void update(PostDto post) {
        jdbcTemplate.update("UPDATE post SET title = ?, author = ?, content = ?, secret = ? WHERE id = ?"
                , post.getTitle()
                , post.getAuthor()
                , post.getContent()
                , post.isSecret()
                , post.getId());
    }

    @Override
    public void deleteById(int id) {
        jdbcTemplate.update("DELETE FROM post WHERE id = ?", id);
    }
}
