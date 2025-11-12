window.onload = function() {
	// 아이디 저장하기
	let savedId = getCookie("savedUserId");
	
	if (savedId !== "") {
		document.getElementById("id").value = savedId;
		document.getElementById("rememberId").checked = true;
	}
	
	
	// 아이디 찾기 모달
	const openModalLink = document.getElementById("openFindIdModal");
    const findIdModal = document.querySelector(".findIdModal");
    const closeModalBtn = document.querySelector(".closeModalBtn");
  
    if (openModalLink) {
    	openModalLink.addEventListener("click", function(event) {
      		event.preventDefault(); // a 태그의 기본 링크 이동을 막음
      		findIdModal.classList.add("show");
		});
  	}

  	if (closeModalBtn) {
    	closeModalBtn.addEventListener("click", function() {
      		findIdModal.classList.remove("show");
    	});
  	}
	
	
	// 비밀번호 재설정 모달
	const openResetModalLink = document.getElementById("openResetPwModal");
    const resetPwModal = document.querySelector(".resetPwModal");
    const closeResetModalBtn = document.querySelector(".closeResetModalBtn");
  
    if (openResetModalLink) {
    	openResetModalLink.addEventListener("click", function(event) {
      		event.preventDefault(); // a 태그의 기본 링크 이동을 막음
      		resetPwModal.classList.add("show");
		});
  	}

  	if (closeResetModalBtn) {
    	closeResetModalBtn.addEventListener("click", function() {
      		resetPwModal.classList.remove("show");
    	});
  	}
	
	
	// 아이디 찾기
	const findIdForm = document.getElementById('findIdForm');
	
	if (findIdForm) {
		findIdForm.addEventListener('submit', function(event) {
			event.preventDefault();
			
			const email = findIdForm.querySelector('input[name="email"]').value;
			const name = findIdForm.querySelector('input[name="name"]').value;
			
			const data = {
				email: email,
				name: name
			};
			
			fetch(`${contextPath}/findId`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify(data),
			}).then(response => response.json())
			  .then(result => {
				if (result.status === 'success') {
					alert('회원님의 아이디는 ' + result.userId + '입니다.');
					findIdModal.classList.remove('show');
				} else {
					alert(result.message + '일치하는 정보를 찾을 수 없습니다.');
				}
			  }) .catch(error => {
				console.error('아이디 찾기 오류 : ', error);
				alert('아이디를 찾는 중 오류가 발생했습니다. 다시 시도해주세요.');
			  });
		});
	}
	
	
	// 비밀번호 재설정
	const resetPwForm = document.getElementById('resetPwForm');
	
	if (resetPwForm) {
		resetPwForm.addEventListener('submit', function(event) {
			event.preventDefault();
			
			const id = resetPwForm.querySelector('input[name="resetId"]').value;
			const email = resetPwForm.querySelector('input[name="resetEmail"]').value;
			
			const data = {
				id: id,
				email: email
			};
			
			fetch(`${contextPath}/resetPassword`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify(data),
			}).then(response => response.json())
			  .then(result => {
				if (result.status === 'success') {
					alert(result.message + "\n임시 비밀번호: " + result.tempPassword);
					resetPwModal.classList.remove('show');
				} else {
					alert(result.message);
				}
			  }).catch(error => {
				console.error('비밀번호 재설정 오류 : ', error);
				alert('비밀번호 재설정 중 오류가 발생했습니다. 다시 시도해주세요.');
			  });
		});
	}
};

function getCookie(name) {
	let value = "; " + document.cookie;
	let parts = value.split("; " + name + "=");
	if (parts.length === 2) {
		return parts.pop().split(";").shift();
	}
	return "";
}