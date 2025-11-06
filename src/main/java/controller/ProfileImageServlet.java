package controller;

import java.io.*;
import java.nio.file.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

@WebServlet("/uploads/profile/*")
public class ProfileImageServlet extends HttpServlet {

    private Path baseDir;

    @Override
    public void init() throws ServletException {
        String p = System.getProperty("upload.base");
        if (p == null || p.isBlank()) p = System.getenv("UPLOAD_BASE");
        if (p == null || p.isBlank()) p = getServletContext().getInitParameter("uploadBase");
        if (p == null || p.isBlank()) {
            String home = System.getProperty("user.home");
            p = home + File.separator + "semi-uploads" + File.separator + "profile";
        }
        baseDir = Paths.get(p);
        try { Files.createDirectories(baseDir); } catch (IOException e) {}
        System.out.println("[Serve] baseDir = " + baseDir.toAbsolutePath());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String rest = req.getPathInfo(); // "/파일명"
        if (rest == null || "/".equals(rest)) { resp.sendError(404); return; }
        String fileName = rest.substring(1);

        // 경로 탈출 방지
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            resp.sendError(400); return;
        }

        Path file = baseDir.resolve(fileName);
        if (!Files.exists(file)) { resp.sendError(404); return; }

        String mime = Files.probeContentType(file);
        if (mime == null) mime = "application/octet-stream";
        resp.setContentType(mime);
        resp.setHeader("Cache-Control", "public, max-age=604800"); // 7일 캐시

        try (OutputStream out = resp.getOutputStream()) {
            Files.copy(file, out);
        }
    }
}
