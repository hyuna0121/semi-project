// mypage_profile.js

document.addEventListener('DOMContentLoaded', function() {
    
    // 🚨🚨🚨 JSP 파일에서 선언된 전역 변수를 사용합니다. (originalSrc, currentUserId, passwordUpdateCount)
    
    const editBtn = document.getElementById('editBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const viewMode = document.getElementById('viewMode');
    const editMode = document.getElementById('editMode');
    const preview = document.getElementById("preview");
    const profileImgInput = document.getElementById("profileImgInput"); 
    
    // 모달 관련 요소
    const passwordCheckModal = new bootstrap.Modal(document.getElementById('passwordCheckModal'));
    const confirmPasswordBtn = document.getElementById('confirmPasswordBtn');
    const currentPasswordInput = document.getElementById('currentPassword'); 
    const passwordFeedback = document.getElementById('passwordFeedback');
    const currentPasswordHidden = document.getElementById('currentPasswordHidden'); 
    
    // 새 비밀번호 검증 요소
    const newPasswordInput = document.getElementById('newPasswordInput');
    const newPasswordConfirm = document.getElementById('newPasswordConfirm');
    const newPasswordMismatch = document.getElementById('newPasswordMismatch');
    const profileForm = document.querySelector('#editMode form');
    
    // 🚨🚨 JSP에서 ID를 부여해야 하는 변수
    const newPasswordGroup = document.getElementById('newPasswordGroup'); 


    // 🚨🚨🚨 비밀번호 수정 횟수 확인 및 필드 차단 로직 (3회부터 차단) 🚨🚨🚨
    if (typeof passwordUpdateCount !== 'undefined' && passwordUpdateCount >= 3) {
        if (newPasswordGroup) {
            // 3회 이상 수정했다면 새 비밀번호 입력 필드를 경고 메시지로 대체
            newPasswordGroup.innerHTML = `
                <div class="alert alert-warning mt-3" role="alert">
                    <strong>비밀번호 수정 제한:</strong> 비밀번호는 이미 ${passwordUpdateCount}회 수정되었습니다. 더 이상 변경할 수 없습니다.
                </div>
            `;
            // 새 비밀번호 필드가 없으므로, 폼 제출 시 해당 필드에 대한 검증을 피할 수 있습니다.
        }
    }


    // 🔹 수정 버튼 클릭 시 → 비밀번호 확인 모달 표시
    editBtn.addEventListener('click', () => {
        currentPasswordInput.value = ''; 
        passwordFeedback.style.display = 'none';
        passwordCheckModal.show();
    });

    // 🔹 모달 '확인' 버튼 클릭 시: DB 연동 로직 (Fetch API)
    confirmPasswordBtn.addEventListener('click', () => {
        const password = currentPasswordInput.value;
        passwordFeedback.style.display = 'none';

        if (password.trim().length === 0) {
            passwordFeedback.textContent = "비밀번호를 입력해주세요.";
            passwordFeedback.style.display = 'block';
            return;
        }

        // 1. 서버 (PasswordCheckServlet)로 현재 비밀번호 검증 요청
        fetch('PasswordCheckServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            // currentUserId 변수를 사용하여 DB에 등록된 ID와 입력된 비밀번호를 전송
            body: `userId=${currentUserId}&password=${encodeURIComponent(password)}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.isMatch) { 
                // 2. 성공: 숨김 필드에 비밀번호 저장 및 모드 전환
                currentPasswordHidden.value = password; 
                
                passwordCheckModal.hide(); 
                viewMode.style.display = 'none';
                editMode.style.display = 'block'; 
            } else {
                // 3. 실패: 메시지 표시
                passwordFeedback.textContent = "비밀번호가 일치하지 않습니다.";
                passwordFeedback.style.display = 'block';
            }
        })
        .catch(error => {
            console.error('AJAX Error:', error);
            passwordFeedback.textContent = "서버 통신 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
            passwordFeedback.style.display = 'block';
        });
    });

    
    // 🚨 폼 제출 이벤트 리스너: 새 비밀번호 일치 검사 및 공백 제거
    profileForm.addEventListener('submit', function(e) {
        let newPwd = newPasswordInput.value;
        const confirmPwd = newPasswordConfirm.value;
        
        // 1. 새 비밀번호 입력 필드의 공백 제거 (서버 전송 전 데이터 정리)
        newPwd = newPwd.trim();
        newPasswordInput.value = newPwd; 


        if (newPwd.length > 0 || confirmPwd.length > 0) {
            // 2. 새 비밀번호 필드가 채워져 있을 경우 일치 검사
            if (newPwd !== confirmPwd) {
                e.preventDefault();
                newPasswordMismatch.style.display = 'block';
                newPasswordInput.focus();
                return;
            }
        }
        
        newPasswordMismatch.style.display = 'none';
    });


    // 🔹 취소 버튼 클릭 시 → 보기 모드로 복귀
    cancelBtn.addEventListener('click', () => {
        editMode.style.display = 'none';
        viewMode.style.display = 'block';
        
        // 필드 초기화
        profileImgInput.value = '';
        if (newPasswordInput) newPasswordInput.value = '';
        if (newPasswordConfirm) newPasswordConfirm.value = '';
        newPasswordMismatch.style.display = 'none';
        
        preview.src = originalSrc; 
    });
    

    // 🔹 프로필 사진 미리보기
    profileImgInput.addEventListener("change", function(e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                preview.src = e.target.result;
            };
            reader.readAsDataURL(file);
        } else {
            preview.src = originalSrc; 
        }
    });
});