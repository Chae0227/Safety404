const chatArea = document.getElementById("chatArea");
const input = document.getElementById("userInput");
const sendBtn = document.getElementById("sendBtn");

let trust = 0;

function addMessage(text, sender) {
    const div = document.createElement("div");
    div.className = sender === "user" ? "msg-user" : "msg-ai";
    div.innerText = text;
    chatArea.appendChild(div);

    chatArea.scrollTop = chatArea.scrollHeight;
}

function updateTrust() {
    trust = Math.min(trust + 5, 100);
    document.getElementById("trustFill").style.width = trust + "%";
    document.getElementById("trustPercent").innerText = trust + "%";
}

sendBtn.onclick = () => {
    const value = input.value.trim();
    if (!value) return;

    addMessage(value, "user");
    input.value = "";
    updateTrust();

    setTimeout(() => {
        addMessage("AI 응답: " + value, "ai");
        updateTrust();
    }, 700);
};
