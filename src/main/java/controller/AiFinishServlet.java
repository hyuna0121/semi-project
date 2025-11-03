package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebServlet("/ai/finish")
public class AiFinishServlet extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    req.setCharacterEncoding("UTF-8");
    String ctx = req.getContextPath();

    String city  = nv(req.getParameter("city"), "seoul");
    String days  = nv(req.getParameter("days"), "2");
    String tempo = nv(req.getParameter("tempo"), "normal");

    String[] with  = req.getParameterValues("with");
    String[] style = req.getParameterValues("style");

    Set<String> interests = new LinkedHashSet<>();

    if (with != null) {
      for (String w : with) {
        switch (w) {
          case "lover":   interests.add("hotplace"); break;
          case "kids":    interests.add("cafe"); interests.add("nature"); break;
          case "parents": interests.add("culture"); interests.add("nature"); break;
          case "friends": interests.add("hotplace"); break;
        }
      }
    }

    if (style != null) {
      for (String s : style) {
        switch (s) {
          case "activity": interests.add("spot"); break;
          case "sns":      interests.add("hotplace"); break;
          case "nature":   interests.add("nature"); break;
          case "culture":  interests.add("spot"); break;
          case "healing":  interests.add("cafe"); break;
          case "shopping": interests.add("spot"); break;
          case "foodie":   interests.add("food"); break;
        }
      }
    }

    if (interests.isEmpty()) interests.add("hotplace");

    String pace = switch (tempo) {
      case "relaxed" -> "relaxed";
      case "fast"    -> "fast";
      default        -> "normal";
    };

    String qs = "city=" + enc(city) +
                "&days=" + enc(days) +
                "&pace=" + enc(pace) +
                "&interests=" + enc(String.join(",", interests));

    resp.sendRedirect(ctx + "/ai/result.jsp?" + qs);
  }

  private static String enc(String s) {
    return URLEncoder.encode(s, StandardCharsets.UTF_8);
  }

  private static String nv(String s, String d) {
    return (s == null || s.trim().isEmpty()) ? d : s.trim();
  }
}
