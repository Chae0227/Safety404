function toggleMobileNav() {
    const nav = document.getElementById('mobileNav');
    if (!nav) return;
    nav.style.display = nav.style.display === 'flex' ? 'none' : 'flex';
}
