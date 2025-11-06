package controller;

import java.io.*;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.travel.dao.MemberDAO;
import com.travel.dto.MemberDTO;

@WebServlet("/ProfileUpdateServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize       = 1024 * 1024 * 30,  // 30MB
    maxRequestSize    = 1024 * 1024 * 40
)
public class ProfileUpdateServlet extends HttpServlet {

    private static final String WEB_PATH_PREFIX = "uploads/profile/"; // DB 저장 경로 접두
    private static final Set<String> ALLOWED_EXT =
            Set.of(".jpg",".jpeg",".png",".gif",".webp",".bmp",".heic");

    private final MemberDAO memberDAO = new MemberDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String id      = trim(req.getParameter("id"));
        String name    = trim(req.getParameter("name"));
        String address = trim(req.getParameter("address"));
        String phone   = trim(req.getParameter("phone"));
        String email   = trim(req.getParameter("email"));
        String gender  = trim(req.getParameter("gender"));
        String oldRel  = normalizeOldPath(trim(req.getParameter("oldImagePath"))); // 과거 경로 호환

        MemberDTO before;
        try {
            before = memberDAO.getMemberById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            flash(req, resp, "사용자 조회 중 오류");
            return;
        }
        if (before == null) { flash(req, resp, "존재하지 않는 사용자"); return; }

        name    = pick(name, before.getName());
        address = pick(address, before.getAddress());
        phone   = pick(phone, before.getPhone());
        email   = pick(email, before.getEmail()); // NOT NULL 보호
        gender  = pick(gender, before.getGender());

        String currentRel = (oldRel!=null && !oldRel.isBlank())
                ? oldRel
                : normalizeOldPath(pick(before.getProfileImage(), "mypage/image/default_profile.png"));

        // 업로드 베이스(물리경로) 결정: JVM 옵션 → 환경변수 → web.xml → 기본값
        Path baseDir = resolveUploadBase(getServletContext());
        Files.createDirectories(baseDir);
        System.out.println("[Upload] baseDir = " + baseDir.toAbsolutePath());

        Part filePart = null;
        try { filePart = req.getPart("profileImg"); } catch (Exception ignore) {}
        String finalRel = currentRel;

        if (filePart != null && filePart.getSize() > 0) {
            String contentType = Optional.ofNullable(filePart.getContentType()).orElse("").toLowerCase();
            if (!contentType.startsWith("image/")) {
                flash(req, resp, "이미지 파일만 업로드 가능합니다.");
                return;
            }

            String ext = extFromFilename(filePart.getSubmittedFileName());
            if (ext.isEmpty()) ext = extFromMime(contentType);
            if (ext.isEmpty() || !ALLOWED_EXT.contains(ext)) {
                flash(req, resp, "허용되지 않는 확장자: " + ext);
                return;
            }

            String newFile = id + "_" + UUID.randomUUID() + ext;
            Path savePath = baseDir.resolve(newFile);
            try (InputStream in = filePart.getInputStream()) {
                Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("[Upload] saved -> " + savePath);

            // DB에는 항상 웹 경로(상대) 저장
            finalRel = WEB_PATH_PREFIX + newFile;

            // 기존 파일 삭제 (기본이미지 제외)
            if (currentRel != null && !currentRel.endsWith("default_profile.png")) {
                try {
                    Path oldPath = baseDir.resolve(Paths.get(currentRel).getFileName());
                    Files.deleteIfExists(oldPath);
                    System.out.println("[Upload] old deleted -> " + oldPath);
                } catch (Exception ignore) {}
            }
        } else {
            System.out.println("[Upload] no file -> keep = " + currentRel);
        }

        boolean ok = memberDAO.updateProfileInfo(id, name, address, phone, email, gender, finalRel);
        req.getSession().setAttribute("msg", ok ? "프로필이 수정되었습니다." : "수정 실패");
        resp.sendRedirect(req.getContextPath() + "/mypage/mypage_profile.jsp");
    }

    /* ===== util ===== */

    private static Path resolveUploadBase(jakarta.servlet.ServletContext ctx) {
        String p = System.getProperty("upload.base");
        if (isBlank(p)) p = System.getenv("UPLOAD_BASE");
        if (isBlank(p)) p = ctx.getInitParameter("uploadBase");
        if (isBlank(p)) {
            String home = System.getProperty("user.home");
            p = home + File.separator + "semi-uploads" + File.separator + "profile";
        }
        return Paths.get(p);
    }

    // 과거 값(mypage/image/...)을 현재 체계(uploads/profile/...)로 치환
    private static String normalizeOldPath(String path) {
        if (isBlank(path)) return null;
        String v = path.trim();
        if (v.startsWith("/")) v = v.substring(1);
        if (v.startsWith("mypage/image/")) {
            v = "uploads/profile/" + Paths.get(v).getFileName().toString();
        }
        return v;
    }

    private static void flash(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        req.getSession().setAttribute("msg", msg);
        resp.sendRedirect(req.getContextPath() + "/mypage/mypage_profile.jsp");
    }

    private static String pick(String v, String fb){ return (v!=null && !v.isBlank()) ? v : fb; }
    private static boolean isBlank(String s){ return s==null || s.trim().isEmpty(); }
    private static String trim(String s){ return s==null? null : s.trim(); }

    private static String extFromFilename(String filename){
        if (filename==null) return "";
        int dot = filename.lastIndexOf('.');
        return (dot<0) ? "" : filename.substring(dot).toLowerCase(Locale.ROOT);
    }
    private static String extFromMime(String mime){
        if (mime==null) return "";
        switch (mime) {
            case "image/jpeg": return ".jpg";
            case "image/png":  return ".png";
            case "image/gif":  return ".gif";
            case "image/webp": return ".webp";
            case "image/bmp":  return ".bmp";
            case "image/heic": return ".heic";
            default: return "";
        }
    }
}
