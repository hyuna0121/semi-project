<%@ page contentType="text/html; charset=UTF-8" language="java" %>

<form action="commentAction.jsp" method="post">
    <input type="hidden" name="action" value="insert">
    <input type="hidden" name="postId" value="<%= request.getParameter("postId") %>">

    <label>작성자:</label>
    <input type="text" name="writer" required>

    <label>내용:</label>
    <textarea name="content" required></textarea>

    <button type="submit">등록</button>
</form>

