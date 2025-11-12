document.addEventListener('DOMContentLoaded', function() {
    
    const editBtn = document.getElementById('editBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const viewMode = document.getElementById('viewMode');
    const editMode = document.getElementById('editMode');
    const preview = document.getElementById("preview");
    const profileImgInput = document.getElementById("profileImgInput"); 
    
    const passwordCheckModal = new bootstrap.Modal(document.getElementById('passwordCheckModal'));
    const confirmPasswordBtn = document.getElementById('confirmPasswordBtn');
    const currentPasswordInput = document.getElementById('currentPassword'); 
    const passwordFeedback = document.getElementById('passwordFeedback');
    
    const currentPasswordHidden = document.getElementById('currentPasswordHidden'); 
    
    const newPasswordInput = document.getElementById('newPasswordInput');
    const newPasswordConfirm = document.getElementById('newPasswordConfirm');
    const newPasswordMismatch = document.getElementById('newPasswordMismatch');
    
    const profileForm = document.querySelector('#editMode form');
    
    const newPasswordGroup = document.getElementById('newPasswordGroup'); 


    if (typeof passwordUpdateCount !== 'undefined' && passwordUpdateCount >= 3) {
        if (newPasswordGroup) {
            newPasswordGroup.innerHTML = `
                <div class="alert alert-warning mt-3">
                    <strong>비밀번호 수정 제한:</strong> 비밀번호는 이미 3회 수정되었습니다. 더 이상 수정할 수 없습니다.
                </div>
            `;
            if (newPasswordInput) newPasswordInput.required = false; 
        }
    }


    editBtn.addEventListener('click', () => {
        currentPasswordInput.value = '';
        passwordFeedback.style.display = 'none';
        passwordCheckModal.show();
    });

    confirmPasswordBtn.addEventListener('click', () => {
        const password = currentPasswordInput.value;
        if (password.trim().length === 0) {
            passwordFeedback.textContent = "비밀번호를 입력해주세요.";
            passwordFeedback.style.display = 'block';
            return;
        }

        fetch(`${contextPath}/mypage/PasswordCheckServlet`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `userId=${currentUserId}&password=${encodeURIComponent(password)}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.isMatch) {
                currentPasswordHidden.value = password;
                
                passwordCheckModal.hide(); 
                viewMode.style.display = 'none';
                editMode.style.display = 'block'; 
            } else {
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

    cancelBtn.addEventListener('click', () => {
        editMode.style.display = 'none';
        viewMode.style.display = 'block';
        
        profileImgInput.value = '';
        newPasswordInput.value = '';
        newPasswordConfirm.value = '';
        newPasswordMismatch.style.display = 'none';
        
        preview.src = originalSrc; 
    });
    
    profileForm.addEventListener('submit', function(e) {
        let newPwd = newPasswordInput.value;
        const confirmPwd = newPasswordConfirm.value;
        
        newPwd = newPwd.trim();
        newPasswordInput.value = newPwd;


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