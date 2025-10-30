package com.example.member;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
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
        out.println("<title>로그인</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<center>");
        out.println("<form method='post' action='LoginProc'>");
        out.println("<h2> 로그인 </h2>");
        out.println("<table width='800'>");

        out.println("<tr>");
        out.println("<td width='300' align='center'>아이디(ID)</td>");
        out.println("<td width='500'><input type='text' name='id'></td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td width='300' align='center'>비밀번호(PWD)</td>");
        out.println("<td width='500'><input type='password' name='pwd'></td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td colspan='2' align='center'><button type='submit'>로그인</button></td>");
        out.println("</tr>");

        out.println("</table>");
        out.println("</form>");
        out.println("</center>");
        out.println("</body>");
        out.println("</html>");
    }
}
