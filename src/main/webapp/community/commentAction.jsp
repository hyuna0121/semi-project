<%@page import="beans.CommentDTO"%>
<%@ page import="beans.CommentDAO" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%
request.setCharacterEncoding("UTF-8");
String action = request.getParameter("action");
CommentDAO dao = new CommentDAO();

try {
    if ("insert".equals(action)) {
        int postId = Integer.parseInt(request.getParameter("postId"));
        String writer = request.getParameter("writer");
        String content = request.getParameter("content");

        CommentDTO dto = new CommentDTO();
        dto.setPostId(postId);
        dto.setWriter(writer);
        dto.setContent(content);

        dao.insertComment(dto);
        response.sendRedirect("commentList.jsp?postId=" + postId);

    } else if ("delete".equals(action)) {
        int commentId = Integer.parseInt(request.getParameter("commentId"));
        int postId = Integer.parseInt(request.getParameter("postId"));

        dao.deleteComment(commentId);
        response.sendRedirect("commentList.jsp?postId=" + postId);

    } else if ("update".equals(action)) {
        int commentId = Integer.parseInt(request.getParameter("commentId"));
        int postId = Integer.parseInt(request.getParameter("postId"));
        String content = request.getParameter("content");

        CommentDTO dto = new CommentDTO();
        dto.setId(commentId);
        dto.setPostId(postId);
        dto.setContent(content);

        dao.updateComment(dto);
        response.sendRedirect("commentList.jsp?postId=" + postId);

    } else {
        out.println("<p style='color:red;'>알 수 없는 action</p>");
    }
} catch (Exception e) {
    e.printStackTrace();
    out.println("<p style='color:red;'>오류 발생: " + e.getMessage() + "</p>");
}
%>








