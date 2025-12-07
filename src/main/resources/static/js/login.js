document.addEventListener("DOMContentLoaded", () => {
    const errorBox = document.getElementById("loginErrorBox");

    if (errorBox && errorBox.dataset.error) {
        showErrorPopup(errorBox.dataset.error);
    }
});

function showErrorPopup(message) {
    const popup = document.createElement("div");
    popup.className = "login-popup";

    popup.innerHTML = `
        <div class="popup-card">
            <p>${message}</p>
            <button id="popupCloseBtn">확인</button>
        </div>
    `;

    document.body.appendChild(popup);

    document.getElementById("popupCloseBtn").addEventListener("click", () => {
        popup.classList.add("fade-out");
        setTimeout(() => popup.remove(), 250);
    });
}
