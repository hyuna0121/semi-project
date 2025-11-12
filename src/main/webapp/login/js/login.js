window.onload = function() {
	let savedId = getCookie("savedUserId");
	
	if (savedId !== "") {
		document.getElementById("id").value = savedId;
		document.getElementById("rememberId").checked = true;
	}
	
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
	
};

function getCookie(name) {
	let value = "; " + document.cookie;
	let parts = value.split("; " + name + "=");
	if (parts.length === 2) {
		return parts.pop().split(";").shift();
	}
	return "";
}