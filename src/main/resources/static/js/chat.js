console.log("🔥 chat.js loaded");

const chatArea = document.getElementById("chatArea");
const input = document.getElementById("userInput");
const sendBtn = document.getElementById("sendBtn");

let trust = 0;
let isComposing = false;
let isCollapsing = false;

/* ---------------------------
   메시지 추가
---------------------------- */
function addMessage(text, sender) {
    const div = document.createElement("div");
    div.className = sender === "user" ? "msg-user" : "msg-ai";
    div.innerText = text;
    chatArea.appendChild(div);
    chatArea.scrollTop = chatArea.scrollHeight;
}

/* ---------------------------
   의존도 업데이트
   - 기본 20씩
   - 60: shake
   - 80: glitch
   - 100: collapse
---------------------------- */
function updateTrust(delta = 20) {
    trust = Math.min(trust + delta, 100);

    const fill = document.getElementById("trustFill");
    const percent = document.getElementById("trustPercent");

    if (fill) fill.style.width = trust + "%";
    if (percent) percent.innerText = trust + "%";

    if (trust >= 60) document.body.classList.add("shake");
    if (trust >= 80) document.body.classList.add("glitch");

    if (trust >= 100 && !isCollapsing) {
        isCollapsing = true;
        collapseScreen();
    }
}

/* ---------------------------
   메시지 전송
---------------------------- */
async function sendMessage() {
    const value = input.value.trim();
    if (!value) return;

    addMessage(value, "user");
    input.value = "";
    updateTrust(10);

    try {
        const res = await fetch("/api/chat", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ message: value })
        });

        if (!res.ok) throw new Error("HTTP " + res.status);

        const data = await res.json();
        addMessage(data.reply ?? "응답을 불러오지 못했습니다.", "ai");
        updateTrust(10);

    } catch (e) {
        console.error("❌ fetch error", e);
        addMessage("AI 서버 오류 발생", "ai");
    }
}

/* ---------------------------
   버튼 클릭
---------------------------- */
sendBtn.onclick = sendMessage;

/* ---------------------------
   IME 처리 (한글 중복 방지)
---------------------------- */
input.addEventListener("compositionstart", () => isComposing = true);
input.addEventListener("compositionend", () => isComposing = false);

input.addEventListener("keydown", (e) => {
    if (e.key === "Enter" && !isComposing) {
        e.preventDefault();
        sendMessage();
    }
});

/* =========================================================
   💥 유리 와장창 붕괴 (삼각 파편)
========================================================= */

/* 삼각 파편 생성 */
function shatterElement(el, pieces = 14) {
    const rect = el.getBoundingClientRect();
    if (rect.width < 2 || rect.height < 2) return;

    for (let i = 0; i < pieces; i++) {
        const shard = el.cloneNode(true);

        shard.style.position = "fixed";
        shard.style.left = rect.left + "px";
        shard.style.top = rect.top + "px";
        shard.style.width = rect.width + "px";
        shard.style.height = rect.height + "px";
        shard.style.margin = "0";
        shard.style.pointerEvents = "none";
        shard.style.zIndex = "9999";
        shard.style.transformOrigin = "center center";
        shard.style.willChange = "transform, opacity";

        /* 🔺 삼각 유리 조각 */
        const p1x = Math.random() * 100;
        const p1y = Math.random() * 100;

        const p2x = p1x + (Math.random() * 30 + 20);
        const p2y = p1y + (Math.random() * 30 + 20);

        const p3x = p1x + (Math.random() * 40 - 20);
        const p3y = p1y + (Math.random() * 40 - 20);

        shard.style.clipPath = `polygon(
            ${p1x}% ${p1y}%,
            ${p2x}% ${p2y}%,
            ${p3x}% ${p3y}%
        )`;

        document.body.appendChild(shard);

        /* 💥 파편 물리 */
        const angle = Math.random() * Math.PI * 2;
        const distance = 600 + Math.random() * 900;

        const x = Math.cos(angle) * distance;
        const y = Math.sin(angle) * distance;

        const rotate = (Math.random() - 0.5) * 2200;
        const scale = 0.2 + Math.random() * 0.4;
        const delay = Math.random() * 120;

        shard.style.transition = `
            transform 1200ms cubic-bezier(.15,.85,.2,1) ${delay}ms,
            opacity 900ms ease ${delay}ms,
            filter 1200ms ease ${delay}ms
        `;

        requestAnimationFrame(() => {
            shard.style.transform = `
                translate(${x}px, ${y}px)
                rotate(${rotate}deg)
                scale(${scale})
            `;
            shard.style.opacity = "0";
            shard.style.filter = "blur(0.8px)";
        });

        setTimeout(() => shard.remove(), 1500 + delay);
    }
}

/* 전체 붕괴 실행 */
function collapseScreen() {
    // 큰 덩어리만 파편화 (렉 방지)
    const targets = document.querySelectorAll(
        "header, .trust-card, .chat-card, .chat-input"
    );

    targets.forEach(el => {
        shatterElement(el, 16);
        el.style.visibility = "hidden";
    });

    setTimeout(() => {
        window.location.href = "/chat/404";
    }, 1300);
}
