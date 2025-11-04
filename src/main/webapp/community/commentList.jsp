<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="beans.CommentDAO, beans.CommentDTO" %>

<%
    // 1. postId 가져오기
    String postIdParam = request.getParameter("postId");
    int postId = 0;
    if (postIdParam != null && !postIdParam.isEmpty()) {
        try {
            postId = Integer.parseInt(postIdParam);
        } catch (NumberFormatException e) {
            out.println("<p style='color:red;'>⚠️ postId 형식 오류</p>");
        }
    } else {
        out.println("<p style='color:orange;'>⚠️ postId가 전달되지 않았습니다. 기본값 0 사용</p>");
    }

    // 2. DAO로 댓글 목록 가져오기
    CommentDAO dao = new CommentDAO();
    List<CommentDTO> comments = dao.getCommentsByPostId(postId);
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>댓글 목록</title>
    <style>
    	
        .btn-gray {
            background-color: #ccc;
            color: #000;
            border: none;
            padding: 3px 8px;
            cursor: pointer;
            margin-left: 5px;
        }
        .comment-item {
            margin-bottom: 10px;
            border-bottom: 1px solid #eee;
            padding-bottom: 5px;
        }
        .subContent{
        	display: flex;
        	justify-content: flex-end;
        }
    </style>
</head>
<body>
<h3>댓글 목록 (<%= comments != null ? comments.size() : 0 %>)</h3>

<div id="comment-section">
    <% if (comments != null && !comments.isEmpty()) {
        for (CommentDTO c : comments) { %>
            <div class="comment-item">
                <strong><%= c.getWriter() %></strong>:
                <%= c.getContent() %>
                <div class="subContent">
		        	<small style="font-size: 0.7em;">(작성: <%= c.getCreatedAt() %>, 수정: <%= c.getUpdatedAt() %>)</small>
		
		            <!-- 수정 버튼 -->
		            <form action="commentEdit.jsp" method="get" style="display:inline;">
		            	<input type="hidden" name="commentId" value="<%= c.getId() %>" />
		                <button type="submit" class="btn-gray">수정</button>
		            </form>
		
		            <!-- 삭제 버튼 -->
		            <form action="commentAction.jsp" method="post" style="display:inline;">
		            	<input type="hidden" name="action" value="delete" />
		                <input type="hidden" name="commentId" value="<%= c.getId() %>" />
		                <input type="hidden" name="postId" value="<%= postId %>" />
		                <button type="submit" class="btn-gray" onclick="return confirm('정말 삭제할까요?');">삭제</button>
		            </form>
		    	</div>
            </div>
    <%  }
      } else { %>
        <p>등록된 댓글이 없습니다.</p>
    <% } %>
</div>

<hr>

<h3>댓글 등록</h3>
<form action="commentAction.jsp" method="post">
    <input type="hidden" name="action" value="insert">
    <input type="hidden" name="postId" value="<%= postId %>">

    작성자: <input type="text" name="writer" required><br>
    내용: <textarea name="content" rows="3" cols="50" required></textarea><br>
    <button type="submit" class="btn-gray">등록</button>
</form>

</body>
</html>









