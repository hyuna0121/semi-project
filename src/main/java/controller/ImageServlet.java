package controller;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.*;

public class ImageServlet extends HttpServlet {
    private Path basePath;

    @Override
    public void init(ServletConfig config) throws jakarta.servlet.ServletException {
        super.init(config);
        ServletContext ctx = config.getServletContext();
        String baseDir = ctx.getInitParameter("uploadBaseDir");
        if (baseDir == null || baseDir.isBlank()) baseDir = ctx.getRealPath("/upload");
        basePath = Paths.get(baseDir).normalize().toAbsolutePath();
        System.out.println("[ImageServlet] baseDir = " + basePath);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pi = req.getPathInfo();
        if (pi == null || "/".equals(pi)) { resp.sendError(404); return; }

        String name = URLDecoder.decode(pi.substring(1), "UTF-8");
        if (name.contains("..") || name.contains("/") || name.contains("\\")) { resp.sendError(400); return; }

        Path file = basePath.resolve(name).normalize().toAbsolutePath();
        System.out.println("[ImageServlet] want: " + file);

        if (!file.startsWith(basePath))           { resp.sendError(403); return; }
        if (!Files.exists(file) || Files.isDirectory(file)) { resp.sendError(404); return; }

        String mime = getServletContext().getMimeType(file.toString());
        if (mime == null) mime = "application/octet-stream";
        resp.setContentType(mime);
        resp.setHeader("Cache-Control", "public, max-age=86400");
        try (OutputStream out = resp.getOutputStream()) { Files.copy(file, out); }
    }
}
