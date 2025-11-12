package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/MemberJoin")
public class MemberJoinServelet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>회원가입</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<center>");
        out.println("<h2>회원가입</h2>");
        out.println("<form action='MemberJoinProc' method='post'>");
        out.println("<table width='600' border='1'>");

        out.println("<tr height='50'>");
        out.println("<td width='150' align='center'>이름</td>");
        out.println("<td width='350' align='center'><input type='text' name='name' size='40' placeholder='이름을 입력하세요'></td>");
        out.println("</tr>");

        out.println("<tr height='50'>");
        out.println("<td align='center'>아이디</td>");
        out.println("<td align='center'>");
        out.println("<input type='text' id='id' name='id' size='25' placeholder='아이디를 입력하세요'>");
        out.println("<input type='button' value='중복확인' onclick='checkId()'>");
        out.println("<span id='idResult' style='margin-left:10px;'></span>");
        out.println("</td>");
        out.println("</tr>");

 
        out.println("<script>");
        out.println("function checkId() {");
        out.println("  const id = document.getElementById('id').value;");
        out.println("  const resultSpan = document.getElementById('idResult');");
        out.println("  if(!id) { alert('아이디를 입력해주세요!'); return; }");
        out.println("  const pattern = /^[a-zA-Z0-9_\\-@.]+$/;");
        out.println("  if (!pattern.test(id)) { alert('아이디는 영문자, 숫자, _, -, @, .만 사용할 수 있습니다!'); return; }");
        out.println("  fetch('IdCheck?id=' + encodeURIComponent(id))");
        out.println("    .then(response => response.text())");
        out.println("    .then(data => { resultSpan.innerHTML = data; })");
        out.println("    .catch(error => console.error('오류 발생:', error));");
        out.println("}");
        out.println("</script>");

 
        out.println("<tr height='50'>");
        out.println("<td align='center'>닉네임</td>");
        out.println("<td align='center'>");
        out.println("<input type='text' id='nickname' name='nickname' size='25' placeholder='닉네임을 입력하세요'>");
        out.println("<input type='button' value='중복확인' onclick='checkNickname()'>");
        out.println("<span id='nickResult' style='margin-left:10px;'></span>");
        out.println("</td>");
        out.println("</tr>");

 
        out.println("<script>");
        out.println("function checkNickname() {");
        out.println("  const nick = document.getElementById('nickname').value;");
        out.println("  const resultSpan = document.getElementById('nickResult');");
        out.println("  if (!nick) { alert('닉네임을 입력해주세요!'); return; }");
        out.println("  fetch('NickCheck?nickname=' + encodeURIComponent(nick))");
        out.println("    .then(response => response.text())");
        out.println("    .then(data => { resultSpan.innerHTML = data; })");
        out.println("    .catch(error => console.error('오류 발생:', error));");
        out.println("}");
        out.println("</script>");


        out.println("<tr height='50'><td align='center'>생년월일</td>");
        out.println("<td align='center'><input type='text' name='birth' size='40' placeholder='ex)19870908'></td></tr>");

        out.println("<tr height='50'><td align='center'>패스워드</td>");
        out.println("<td align='center'><input type='password' name='pass1' size='40' placeholder='pw를 입력하세요'></td></tr>");

        out.println("<tr height='50'><td align='center'>패스워드 확인</td>");
        out.println("<td align='center'><input type='password' name='pass2' size='40' placeholder='pw를 다시 한 번 입력하세요'></td></tr>");

        out.println("<tr height='50'><td align='center'>주소</td>");
        out.println("<td align='center'><input type='text' name='address' size='40' placeholder='주소를 입력하세요'></td></tr>");

        out.println("<tr height='50'>");
        out.println("<td align='center' colspan='2'>");
        out.println("<input type='submit' value='가입'> &nbsp;&nbsp;");
        out.println("<input type='reset' value='취소'>");
        out.println("</td>");
        out.println("</tr>");

        out.println("</table>");
        out.println("</form>");
        out.println("</center>");
        out.println("</body>");
        out.println("</html>");
    }
}