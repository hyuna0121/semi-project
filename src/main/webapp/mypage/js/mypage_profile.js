// mypage_profile.js

document.addEventListener('DOMContentLoaded', function() {
    
    // 🚨🚨🚨 JSP 하단 스크립트에서 선언된 변수들을 사용합니다.
    
    const editBtn = document.getElementById('editBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const viewMode = document.getElementById('viewMode');
    const editMode = document.getElementById('editMode');
    const preview = document.getElementById("preview");
    const profileImgInput = document.getElementById("profileImgInput"); 
    
    // 모달 및 새 비밀번호 검증 요소
    const passwordCheckModal = new bootstrap.Modal(document.getElementById('passwordCheckModal'));
    const confirmPasswordBtn = document.getElementById('confirmPasswordBtn');
    const currentPasswordInput = document.getElementById('currentPassword'); 
    const passwordFeedback = document.getElementById('passwordFeedback');
    
    // 수정 모드 폼 내 숨겨진 필드 (모달 값을 서버로 전달)
    const currentPasswordHidden = document.getElementById('currentPasswordHidden'); 
    
    // 새 비밀번호 검증 요소
    const newPasswordInput = document.getElementById('newPasswordInput');
    const newPasswordConfirm = document.getElementById('newPasswordConfirm');
    const newPasswordMismatch = document.getElementById('newPasswordMismatch');
    
    const profileForm = document.querySelector('#editMode form');
    
    // 🚨🚨 추가: 새 비밀번호 입력 그룹 (JSP에서 ID 부여 필요)
    const newPasswordGroup = document.getElementById('newPasswordGroup'); 


    // 🚨🚨 수정: 비밀번호 수정 횟수 확인 및 필드 차단 (3회부터 차단)
    if (typeof passwordUpdateCount !== 'undefined' && passwordUpdateCount >= 3) {
        if (newPasswordGroup) {
            // 3회 이상 수정했다면 새 비밀번호 입력 필드를 경고 메시지로 대체
            newPasswordGroup.innerHTML = `
                <div class="alert alert-warning mt-3">
                    <strong>비밀번호 수정 제한:</strong> 비밀번호는 이미 3회 수정되었습니다. 더 이상 수정할 수 없습니다.
                </div>
            `;
            // 폼 제출 시 유효성 검사 로직이 실행되지 않도록 처리
            if (newPasswordInput) newPasswordInput.required = false; 
        }
    }


    // 🔹 수정 버튼 클릭 시 → 비밀번호 확인 모달 표시
    editBtn.addEventListener('click', () => {
        currentPasswordInput.value = ''; // 모달 열 때 입력 필드 초기화
        passwordFeedback.style.display = 'none';
        passwordCheckModal.show();
    });

    // 🔹 모달 '확인' 버튼 클릭 시: DB 연동 로직 (Fetch API)
    confirmPasswordBtn.addEventListener('click', () => {
        const password = currentPasswordInput.value;
        if (password.trim().length === 0) {
            passwordFeedback.textContent = "비밀번호를 입력해주세요.";
            passwordFeedback.style.display = 'block';
            return;
        }

        // 1. 서버 (PasswordCheckServlet)로 현재 비밀번호 검증 요청
        fetch('PasswordCheckServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `userId=${currentUserId}&password=${encodeURIComponent(password)}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.isMatch) {
                // 2. 성공: 수정 모드로 전환
                currentPasswordHidden.value = password; // 모달에서 입력된 이전 비밀번호를 숨김 필드에 저장
                
                passwordCheckModal.hide(); 
                viewMode.style.display = 'none';
                editMode.style.display = 'block'; 
            } else {
                // 3. 실패: 메시지 표시 (DB 비밀번호 불일치)
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

    // 🔹 취소 버튼 클릭 시 → 보기 모드로 복귀
    cancelBtn.addEventListener('click', () => {
        editMode.style.display = 'none';
        viewMode.style.display = 'block';
        
        // 파일 입력 필드 초기화 및 새 비밀번호 필드 초기화
        profileImgInput.value = '';
        newPasswordInput.value = '';
        newPasswordConfirm.value = '';
        newPasswordMismatch.style.display = 'none';
        
        preview.src = originalSrc; 
    });
    
    // 🚨 폼 제출 이벤트 리스너: 새 비밀번호 일치 검사 및 공백 제거
    profileForm.addEventListener('submit', function(e) {
        let newPwd = newPasswordInput.value;
        const confirmPwd = newPasswordConfirm.value;
        
        // 🚨🚨 추가: 폼 제출 시에도 클라이언트 측에서 공백 제거
        newPwd = newPwd.trim();
        newPasswordInput.value = newPwd; // 공백 제거된 값으로 필드 업데이트 (서버로 전송될 값)


        if (newPwd || confirmPwd) {
            if (newPwd !== confirmPwd || newPwd.length === 0) {
                e.preventDefault();
                newPasswordMismatch.style.display = 'block';
                newPasswordInput.focus();
                return;
            }
        }
        
        newPasswordMismatch.style.display = 'none';
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
            preview.src = originalSrc; // 파일 선택 취소 시 원래 이미지 복구
        }
    });
});