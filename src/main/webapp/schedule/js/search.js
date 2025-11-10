const member = document.getElementById('member');
const suggestionsBox = document.getElementById('suggestionsBox');
const addButton = document.getElementById('addButton');
const userListContainer = document.getElementById('userList');

member.addEventListener('keyup', async () => {
    const query = member.value.trim();

    if (query.length < 1) {
        suggestionsBox.innerHTML = ''; 
        suggestionsBox.style.display = 'none';
        return;
    }

    try {
        const response = await fetch(`searchUser.jsp?query=${query}`);
        const userIds = await response.json(); // 리턴값 예시 : ['admin', 'admin123', 'admintest'] 

        suggestionsBox.innerHTML = '';
        if (userIds.length > 0) {
            userIds.forEach(userId => {
                const suggestionItem = document.createElement('div');
                suggestionItem.textContent = userId;
                
                suggestionItem.addEventListener('click', () => {
                    member.value = userId; 
                    suggestionsBox.innerHTML = ''; 
                    suggestionsBox.style.display = 'none';
                });
                
                suggestionsBox.appendChild(suggestionItem);
            });
			
            suggestionsBox.style.display = 'block'; 
        } else {
            suggestionsBox.style.display = 'none'; 
        }
    } catch (error) {
        console.error('검색 중 오류:', error);
    }
});

addButton.addEventListener('click', async () => {
    const userId = member.value.trim();

    if (userId.length < 1) {
        alert('추가할 아이디를 입력하거나 선택하세요.');
        return;
    }

    const existingInput = userListContainer.querySelector(`input[name="travelBudies"][value="${userId}"]`);
    if (existingInput) {
        alert('이미 추가된 친구입니다.');
        member.value = '';
        suggestionsBox.style.display = 'none';
        return;
    }

    try {
        const response = await fetch(`getUserDetails.jsp?userId=${encodeURIComponent(userId)}`);
        const user = await response.json(); 

        if (user && user.userId) {
            const userCard = document.createElement('div');
            userCard.className = 'user-card'; 
            userCard.style.padding = '10px';
            userCard.style.borderBottom = '1px solid #eee';
            
            userCard.innerHTML = `
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <strong>아이디:</strong> ${user.userId}<br>
                        <strong>이름:</strong> ${user.userName}<br>
                        <strong>이메일:</strong> ${user.email || 'N/A'}
                    </div>
                    <button type="button" class="btn btn-danger btn-sm btn-remove-buddy">삭제</button>
                </div>
            `;
            
            const hiddenInput = document.createElement('input');
            hiddenInput.type = 'hidden';
            hiddenInput.name = 'travelBudies'; 
            hiddenInput.value = user.userId;  

            userListContainer.appendChild(userCard);
            userListContainer.appendChild(hiddenInput);

            userCard.querySelector('.btn-remove-buddy').addEventListener('click', () => {
                userListContainer.removeChild(userCard);
                userListContainer.removeChild(hiddenInput); 
            });

            member.value = '';
            suggestionsBox.innerHTML = '';
            suggestionsBox.style.display = 'none';

        } else {
            alert('해당 아이디의 사용자 정보를 찾을 수 없습니다.');
        }
    } catch (error) {
        console.error('사용자 정보 추가 중 오류:', error);
        alert('사용자 정보를 가져오는 중 오류가 발생했습니다.');
    }
});