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
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize       = 1024 * 1024 * 50, // 50MB
    maxRequestSize    = 1024 * 1024 * 60  // 60MB
)
public class ProfileUpdateServlet extends HttpServlet {

    private final MemberDAO memberDAO = new MemberDAO();

    /** 1차 저장: 배포폴더 기준 웹 경로 */
    private static final String UPLOAD_CTX = "/mypage/image";

    /** 소스폴더 미러링 경로 (본인 프로젝트 경로로 맞춰두었습니다) */
    private static final Path MIRROR_SRC_DIR = Paths.get(
        "D:\\GDJ94\\workspace\\semi-project\\src\\main\\webapp\\mypage\\image"
    );

    /** 확장자/타입 허용 */
    private static final Set<String> ALLOWED_EXT  =
        Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".heic");

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
        String oldImagePath = normalizeRel(trim(req.getParameter("oldImagePath"))); // "mypage/image/..."

        System.out.println("[Upload] id=" + id + ", old=" + oldImagePath);

        // 사용자 조회
        MemberDTO before;
        try {
            before = memberDAO.getMemberById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            fail(req, resp, "사용자 조회 중 오류");
            return;
        }
        if (before == null) { fail(req, resp, "존재하지 않는 사용자"); return; }

        // 비어온 값 기존값 유지
        name    = pick(name,    before.getName());
        address = pick(address, before.getAddress());
        phone   = pick(phone,   before.getPhone());
        email   = pick(email,   before.getEmail()); // NOT NULL 보호
        gender  = pick(gender,  before.getGender());

        String currentRel = (oldImagePath!=null && !oldImagePath.isBlank())
                ? oldImagePath
                : pick(before.getProfileImage(), "mypage/image/default_profile.png");

        Part filePart = null;
        try { filePart = req.getPart("profileImg"); } catch (Exception ignore) {}

        String finalRel = currentRel;

        if (filePart != null) {
            System.out.println("[Upload] part=" + filePart.getName()
                    + ", size=" + filePart.getSize()
                    + ", type=" + filePart.getContentType()
                    + ", filename=" + filePart.getSubmittedFileName());
        }

        if (filePart != null && filePart.getSize() > 0) {
            String contentType = Optional.ofNullable(filePart.getContentType()).orElse("").toLowerCase();
            if (!contentType.startsWith("image/")) {
                fail(req, resp, "이미지 파일만 업로드 가능합니다.");
                return;
            }

            // 배포폴더 실제 경로
            String real = getServletContext().getRealPath(UPLOAD_CTX);
            if (real == null) {
                throw new ServletException("이미지 저장 경로(" + UPLOAD_CTX + ")를 해석할 수 없습니다.");
            }
            Path deployDir = Paths.get(real);
            Files.createDirectories(deployDir);

            // 쓰기 테스트
            writeTest(deployDir);

            // 확장자
            String ext = extFromFilename(filePart.getSubmittedFileName());
            if (ext.isEmpty()) ext = extFromMime(contentType);
            if (ext.isEmpty() || !ALLOWED_EXT.contains(ext)) {
                fail(req, resp, "허용되지 않는 확장자: " + ext);
                return;
            }

            // 저장
            String newFile = id + "_" + UUID.randomUUID() + ext;
            Path deploySavePath = deployDir.resolve(newFile);
            try (InputStream in = filePart.getInputStream()) {
                Files.copy(in, deploySavePath, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("[Upload] DEPLOY saved -> " + deploySavePath);

            // ▶ 소스폴더에도 미러링 (개발 편의를 위한 동기 복사)
            try {
                Files.createDirectories(MIRROR_SRC_DIR);
                Files.copy(deploySavePath, MIRROR_SRC_DIR.resolve(newFile), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[Upload][MIRROR] copied -> " + MIRROR_SRC_DIR.resolve(newFile));
            } catch (Exception e) {
                System.out.println("[Upload][MIRROR] fail: " + e.getMessage());
            }

            // DB에는 상대경로 저장
            finalRel = "mypage/image/" + newFile;

            // 기존 파일 삭제(배포폴더 + 소스폴더, 기본이미지 제외)
            if (currentRel != null && !currentRel.endsWith("default_profile.png")) {
                try {
                    // 배포폴더 파일
                    Path oldDeploy = Paths.get(getServletContext().getRealPath("/"), currentRel);
                    Files.deleteIfExists(oldDeploy);
                    System.out.println("[Upload] DEPLOY old deleted -> " + oldDeploy);
                } catch (Exception ignore) {}

                try {
                    // 소스폴더 파일 (파일명만 추출)
                    String oldName = Paths.get(currentRel).getFileName().toString();
                    Path oldMirror = MIRROR_SRC_DIR.resolve(oldName);
                    Files.deleteIfExists(oldMirror);
                    System.out.println("[Upload][MIRROR] old deleted -> " + oldMirror);
                } catch (Exception ignore) {}
            }
        } else {
            System.out.println("[Upload] no file -> keep = " + currentRel);
        }

        // DB 업데이트
        boolean ok = memberDAO.updateProfileInfo(id, name, address, phone, email, gender, finalRel);
        System.out.println("[Upload] DB update " + (ok ? "OK" : "FAIL"));
        req.getSession().setAttribute("msg", ok ? "프로필이 수정되었습니다." : "수정 실패");
        resp.sendRedirect(req.getContextPath() + "/mypage/mypage_profile.jsp");
    }

    /* ---------- util ---------- */
    private static void fail(HttpServletRequest req, HttpServletResponse resp, String msg) throws IOException {
        req.getSession().setAttribute("msg", msg);
        resp.sendRedirect(req.getContextPath() + "/mypage/mypage_profile.jsp");
    }
    private static String trim(String s){ return s==null? null : s.trim(); }
    private static String pick(String v, String fb){ return (v!=null && !v.isBlank()) ? v : fb; }

    // "/mypage/image/..." -> "mypage/image/..."
    private static String normalizeRel(String p){
        if (p==null || p.isBlank()) return null;
        String v = p.trim();
        if (v.startsWith("/")) v = v.substring(1);
        return v;
    }

    private static void writeTest(Path dir) throws IOException {
        Path probe = dir.resolve(".w");
        Files.writeString(probe, "ok", StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        Files.deleteIfExists(probe);
        System.out.println("[Upload] write test OK at " + dir);
    }

    private static String extFromFilename(String filename){
        if (filename==null) return "";
        int dot = filename.lastIndexOf('.');
        return (dot<0) ? "" : filename.substring(dot).toLowerCase(Locale.ROOT);
    }
    private static String extFromMime(String mime){
        if (mime == null) return "";
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
