<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="beans.CommentDAO" %>
<%@ page import="beans.CommentDTO" %>

<%
    // 1. commentId 파라미터 가져오기
    String commentIdParam = request.getParameter("commentId");
    if (commentIdParam == null || commentIdParam.isEmpty()) {
        out.println("<p style='color:red;'>⚠️ commentId 파라미터가 없습니다.</p>");
        return;
    }

    int commentId = 0;
    try {
        commentId = Integer.parseInt(commentIdParam);
    } catch (NumberFormatException e) {
        out.println("<p style='color:red;'>⚠️ commentId 형식이 잘못되었습니다.</p>");
        return;
    }

    // 2. DAO를 통해 단일 댓글 조회
    CommentDAO dao = new CommentDAO();
    CommentDTO comment = dao.getCommentById(commentId);

    if (comment == null) {
        out.println("<p style='color:red;'>⚠️ 해당 댓글이 존재하지 않습니다.</p>");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>댓글 수정</title>
</head>
<body>
    <h3>댓글 수정</h3>
    <form action="commentAction.jsp" method="post">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="commentId" value="<%= comment.getId() %>">
        <input type="hidden" name="postId" value="<%= comment.getPostId() %>">

        <p>
            작성자: <strong><%= comment.getWriter() %></strong>
        </p>

        <p>
            내용:<br>
            <textarea name="content" rows="5" cols="50"><%= comment.getContent() %></textarea>
        </p>

        <button type="submit">수정 완료</button>
        <button type="button" onclick="history.back();">취소</button>
    </form>
</body>
</html>



