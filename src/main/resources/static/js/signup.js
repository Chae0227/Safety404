// 요소 가져오기
const pw = document.getElementById("password");
const pwCheck = document.getElementById("passwordCheck");

const pwStrengthMsg = document.getElementById("pwStrengthMsg");
const pwMatchMsg = document.getElementById("pwMatchMsg");


// ----------------------
// 비밀번호 강도 체크
// ----------------------
pw.addEventListener("input", () => {
    const value = pw.value;

    pwStrengthMsg.className = "msg"; // 초기화

    // 0 ~ 5글자 : 10글자 이상 입력해주세요
    if (value.length <= 5) {
        pwStrengthMsg.textContent = "비밀번호는 10글자 이상 입력해주세요.";
        pwStrengthMsg.classList.add("red");
        return;
    }

    // 6 ~ 9글자 : 약함
    if (value.length >= 6 && value.length <= 9) {
        pwStrengthMsg.textContent = "비밀번호가 약합니다.";
        pwStrengthMsg.classList.add("orange");
        return;
    }

    // 10글자 이상 : 강함
    if (value.length >= 10) {
        pwStrengthMsg.textContent = "비밀번호가 강합니다.";
        pwStrengthMsg.classList.add("green");
        return;
    }
});


// ----------------------
// 비밀번호 일치 여부 체크
// ----------------------
function checkPwMatch() {
    pwMatchMsg.className = "msg"; // 초기화

    if (pw.value === "" && pwCheck.value === "") {
        pwMatchMsg.textContent = "";
        return;
    }

    if (pw.value !== pwCheck.value) {
        pwMatchMsg.textContent = "비밀번호가 일치하지 않습니다.";
        pwMatchMsg.classList.add("red");
    } else {
        pwMatchMsg.textContent = "비밀번호가 일치합니다.";
        pwMatchMsg.classList.add("green");
    }
}

pw.addEventListener("input", checkPwMatch);
pwCheck.addEventListener("input", checkPwMatch);


// ----------------------
// 전화번호 자동 하이픈
// ----------------------
const phoneInput = document.getElementById("phone");

phoneInput.addEventListener("input", function(e) {
    let value = e.target.value.replace(/[^0-9]/g, "");

    if (value.length < 4) {
        e.target.value = value;
    } 
    else if (value.length < 7) {
        e.target.value = value.replace(/(\d{3})(\d+)/, "$1-$2");
    } 
    else if (value.length < 11) {
        e.target.value = value.replace(/(\d{3})(\d{3})(\d+)/, "$1-$2-$3");
    } 
    else {
        e.target.value = value.replace(/(\d{3})(\d{4})(\d{4}).*/, "$1-$2-$3");
    }
});


// ----------------------
// 주소찾기 (카카오 API)
// ----------------------
function findAddress() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById("postcode").value = data.zonecode;
            document.getElementById("address").value = data.roadAddress;
            document.getElementById("detailAddress").focus();
        },
        popupName: "postcodePopup"
    }).open();
}

// 🚨 HTML에서 onclick="findAddress()"로 접근하려면 반드시 필요함
window.findAddress = findAddress;

// ----------------------
// 생일: 오늘 이후 날짜 선택 금지
// ----------------------
const birthInput = document.getElementById("birth");

if (birthInput) {
    const today = new Date().toISOString().split("T")[0];
    birthInput.max = today;
}
