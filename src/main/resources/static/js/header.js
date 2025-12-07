function toggleMobileNav() {
    const nav = document.getElementById('mobileNav');
    if (!nav) return;
    nav.style.display = nav.style.display === 'flex' ? 'none' : 'flex';
}

function toggleUserDropdown() {
    const menu = document.getElementById("userDropdown");
    menu.style.display = menu.style.display === "block" ? "none" : "block";
}

// 메뉴 외를 클릭하면 닫기
document.addEventListener("click", function(e) {
    const menu = document.getElementById("userDropdown");
    const userMenu = document.querySelector(".user-menu");

    if (!userMenu.contains(e.target)) {
        menu.style.display = "none";
    }
});