package controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.*;

public class ImageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Path basePath;

    @Override
    public void init(ServletConfig config) throws jakarta.servlet.ServletException {
        super.init(config);
        ServletContext ctx = config.getServletContext();

        String baseDir = ctx.getInitParameter("uploadBaseDir"); // \\192.168.x.x\share
        if (baseDir == null || baseDir.isBlank()) {
            baseDir = ctx.getRealPath("/upload");               // fallback
        }
        basePath = Paths.get(baseDir).normalize().toAbsolutePath();
        System.out.println("[ImageServlet] baseDir = " + basePath);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pi = req.getPathInfo(); // "/파일명"
            if (pi == null || "/".equals(pi)) { resp.sendError(404); return; }

            String name = URLDecoder.decode(pi.substring(1), "UTF-8");
            // 보안: 경로 탈출/구분자 차단
            if (name.contains("..") || name.contains("/") || name.contains("\\")) {
                resp.sendError(400, "invalid filename"); return;
            }

            Path file = basePath.resolve(name).normalize().toAbsolutePath();
            System.out.println("[ImageServlet] want: " + file);

            if (!file.startsWith(basePath))                    { resp.sendError(403, "forbidden path"); return; }
            if (!Files.exists(file) || Files.isDirectory(file)) { resp.sendError(404); return; }

            String mime = getServletContext().getMimeType(file.toString());
            if (mime == null) mime = "application/octet-stream";

            resp.setContentType(mime);
            resp.setHeader("Cache-Control", "public, max-age=86400");

            try (OutputStream out = resp.getOutputStream()) {
                Files.copy(file, out);
            }
        } catch (Exception e) {
            System.err.println("[ImageServlet] ERROR: " + e);
            e.printStackTrace();
            resp.setStatus(500);
            resp.setContentType("text/plain; charset=UTF-8");
            resp.getWriter().println("Image load failed: " + e.getClass().getSimpleName());
        }
    }
}
