<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.travel.dao.ChatDAO"%>
<%@ page import="com.travel.dto.ChatDTO"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
String cp = request.getContextPath();

String userId = (String) session.getAttribute("loginId");

final int PAGE_SIZE = 10;
int pageNo = 1;
int totalComments = 0;
int totalPages = 1;

try {
    String pageParam = request.getParameter("page");
    if (pageParam != null) {
    	pageNo = Integer.parseInt(pageParam);
    }
} catch (NumberFormatException e) {
	pageNo = 1;
}

int offset = (pageNo - 1) * PAGE_SIZE;

List<ChatDTO> comments = null;
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

try {
    ChatDAO dao = new ChatDAO();
    
    totalComments = dao.countUserComments(userId);
    totalPages = (int) Math.ceil((double) totalComments / PAGE_SIZE);
    if (totalPages == 0) totalPages = 1; 

    comments = dao.getUserCommentsByUserId(userId, offset, PAGE_SIZE); 
    
} catch (Exception e) {	
    e.printStackTrace();
    comments = new ArrayList<>(); 
}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<title>내 댓글 | 마이페이지</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
<link rel="stylesheet" href="./css/mypage_comments.css"> 
</head>
<body>
<%@ include file="../header.jsp" %>

<div class="main-container">
  
  <aside class="sidebar">
    <h5>My Page</h5>
    <a href="<%=cp%>/mypage/mypage_profile.jsp">내 프로필</a>
    <a href="<%=cp%>/mypage/travel_schedule.jsp">여행 일정</a>
    <a href="<%=cp%>/mypage/mypage_comments.jsp" class="active">내 댓글</a>
  </aside>

  <main class="content">
    <div class="container">
      <h3 class="section-title">💬 내 댓글 목록 (<%= totalComments %>개)</h3>

      <div class="table-responsive">
        <table class="table table-bordered align-middle">
        <colgroup>
            <col style="width: 5%;">  
            <col style="width: 450px;"> 
            <col style="width: 15%;"> 
            <col style="width: 20ch;"> 
            <col style="width: 15%;"> 
        </colgroup>
          <thead class="table-info">
            <tr>
              <th>#</th>
              <th>댓글 내용</th>
              <th>작성 일시</th>
              <th>일정 제목</th> <th>바로 가기</th>
            </tr>
          </thead>
          <tbody>
            <% 
            if (comments != null && !comments.isEmpty()) {
                int displayStartNumber = totalComments - offset;
                for (ChatDTO c : comments) {
            %>
            <tr>
              <td><%= displayStartNumber-- %></td>
              <td><%= c.getcomment() %></td>
              <td><%= sdf.format(c.getCreatedAt()) %></td>
              
              <td>
                <%= c.getScheduleTitle() != null ? c.getScheduleTitle() : "일정 정보 없음" %>
              </td>
              
              <td>
                <a href="<%= cp %>/schedule/schedule.jsp?schedule_id=<%= c.getSchedule_id() %>" 
                   class="btn btn-sm btn-outline-primary">
                   일정 보기
                </a>
              </td>
            </tr>
            <% 
                }
            } else { 
            %>
            <tr>
              <td colspan="5" class="text-center text-muted py-4">작성한 댓글이 없습니다.</td>
            </tr>
            <% 
            }
            %>
          </tbody>
        </table>
      </div>
      
      <div style="display: flex; justify-content: center; margin-top: 30px; gap: 8px;">
      <%
          final int PAGE_RANGE = 5;
          int startPage = Math.max(1, pageNo - (PAGE_RANGE / 2));
          int endPage = Math.min(totalPages, startPage + PAGE_RANGE - 1);
          
          if (endPage - startPage + 1 < PAGE_RANGE) {
              startPage = Math.max(1, endPage - PAGE_RANGE + 1);
          }

          if (pageNo > 1) {
              String prevHref = cp + "/mypage/mypage_comments.jsp?page=" + (pageNo - 1);
              %><a href="<%=prevHref%>" style="width: 32px; height: 32px; border-radius: 8px; border: 1px solid #ddd; text-decoration: none; display: flex; justify-content: center; align-items: center; font-weight: bold;">&lt;</a><%
          }

          for (int i = startPage; i <= endPage; i++) {
              String href = cp + "/mypage/mypage_comments.jsp?page=" + i;
              String activeStyle = (i == pageNo) ? "background-color: #0d6efd; color: white; border-color: #0d6efd;" : "";
      %>
          <a href="<%=href%>"
             style="width: 32px; height: 32px; border-radius: 8px; border: 1px solid #ddd; text-decoration: none; display: flex; justify-content: center; align-items: center; font-weight: bold; <%= activeStyle %>">
             <%= i %>
          </a>
      <%
          }

          if (pageNo < totalPages) {
              String nextHref = cp + "/mypage/mypage_comments.jsp?page=" + (pageNo + 1);
              %><a href="<%=nextHref%>" style="width: 32px; height: 32px; border-radius: 8px; border: 1px solid #ddd; text-decoration: none; display: flex; justify-content: center; align-items: center; font-weight: bold;">&gt;</a><%
          }
      %>
      </div>
      
    </div>
  </main>
</div>

<%@ include file="../footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>