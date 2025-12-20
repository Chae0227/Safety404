document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("verifyForm");
    if (!form) return;

    const categorySelect = form.querySelector("select[name='category']");
    const submitButton = document.querySelector(
        "button[type='submit'][form='verifyForm']"
    );

    // 초기 상태: 선택 안 되어 있으면 비활성화
    const updateSubmitState = () => {
        if (!categorySelect.value) {
            submitButton.disabled = true;
            submitButton.style.opacity = "0.6";
            submitButton.style.cursor = "not-allowed";
        } else {
            submitButton.disabled = false;
            submitButton.style.opacity = "1";
            submitButton.style.cursor = "pointer";
        }
    };

    // 최초 실행
    updateSubmitState();

    // 카테고리 변경 시
    categorySelect.addEventListener("change", updateSubmitState);

    // 혹시 모를 submit 방어
    form.addEventListener("submit", (e) => {
        if (!categorySelect.value) {
            e.preventDefault();
            alert("카테고리를 선택해야 자료를 등록할 수 있습니다.");
            categorySelect.focus();
        }
    });
});
