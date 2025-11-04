package beans;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    // 댓글 등록
    public int insertComment(CommentDTO dto) {
        String sql = "INSERT INTO comments(post_id, writer, content, created_at, updated_at) VALUES(?, ?, ?, NOW(), NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dto.getPostId());
            ps.setString(2, dto.getWriter());
            ps.setString(3, dto.getContent());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 댓글 삭제
    public int deleteComment(int id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 댓글 수정
    public int updateComment(CommentDTO dto) {
        String sql = "UPDATE comments SET content = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getContent());
            ps.setInt(2, dto.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 단일 댓글 조회
    public CommentDTO getCommentById(int id) {
        String sql = "SELECT id, post_id, writer, content, created_at, updated_at FROM comments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CommentDTO dto = new CommentDTO();
                dto.setId(rs.getInt("id"));
                dto.setPostId(rs.getInt("post_id"));
                dto.setWriter(rs.getString("writer"));
                dto.setContent(rs.getString("content"));
                dto.setCreatedAt(rs.getTimestamp("created_at"));
                dto.setUpdatedAt(rs.getTimestamp("updated_at"));
                return dto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 특정 게시글 댓글 목록 조회
    public List<CommentDTO> getCommentsByPostId(int postId) {
        List<CommentDTO> list = new ArrayList<>();
        String sql = "SELECT id, post_id, writer, content, created_at, updated_at FROM comments WHERE post_id = ? ORDER BY created_at ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CommentDTO dto = new CommentDTO();
                dto.setId(rs.getInt("id"));
                dto.setPostId(rs.getInt("post_id"));
                dto.setWriter(rs.getString("writer"));
                dto.setContent(rs.getString("content"));
                dto.setCreatedAt(rs.getTimestamp("created_at"));
                dto.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}


