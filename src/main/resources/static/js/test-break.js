document.getElementById("breakBtn").onclick = () => {

    const header = document.getElementById("header");
    const box = document.getElementById("contentBox");
    const finalText = document.getElementById("finalMessage");

    // 1) 화면 전체 글리치 모드 시작
    document.body.classList.add("glitching");

    setTimeout(() => document.body.classList.remove("glitching"), 1200);

    // 2) 텍스트를 파편으로 쪼갬
    breakIntoFragments(header);
    breakIntoFragments(box);

    // 3) 원본 UI는 투명하게 제거
    header.style.opacity = "0";
    box.style.opacity = "0";

    // 4) 마지막 경고 메시지 표시
    setTimeout(() => {
        finalText.style.display = "block";
    }, 1500);
};

function breakIntoFragments(element) {
    const text = element.innerText.trim();
    const rect = element.getBoundingClientRect();

    // 원본 텍스트 제거
    element.innerText = "";

    [...text].forEach(char => {
        const span = document.createElement("span");
        span.innerText = char;
        span.classList.add("fragment");

        // 랜덤 파편 이동 방향
        span.style.setProperty("--tx", (Math.random() * 100 - 50) + "px");
        span.style.setProperty("--ty", (Math.random() * 180 - 60) + "px");

        // 랜덤 회전값
        span.style.setProperty("--rot1", (Math.random() * 40 - 20) + "deg");
        span.style.setProperty("--rot2", (Math.random() * 90 - 45) + "deg");
        span.style.setProperty("--rot3", (Math.random() * 180 - 90) + "deg");

        // 시작 위치
        span.style.left = rect.left + (Math.random() * rect.width) + "px";
        span.style.top = rect.top + (Math.random() * rect.height) + "px";

        document.body.appendChild(span);
    });
}
