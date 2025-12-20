document.addEventListener("DOMContentLoaded", () => {
  // =========================
  // 1) 내 정보 수정 토글
  // =========================
  const editBtn = document.getElementById("editBtn");
  const cancelBtn = document.getElementById("cancelBtn");
  const form = document.querySelector(".profile-form");
  const actions = document.querySelector(".form-actions");

  if (form && actions && editBtn && cancelBtn) {
    const inputs = form.querySelectorAll("input");

    // 초기엔 저장/취소 숨김
    actions.style.display = "none";

    // 원래값 저장(취소 시 되돌릴용)
    const original = {};
    inputs.forEach((i) => {
      if (i.name) original[i.name] = i.value;
    });

    editBtn.addEventListener("click", () => {
      inputs.forEach((i) => {
        // name 있는 것만(수정 가능한 필드들)
        if (i.name) i.disabled = false;
      });
      actions.style.display = "flex";
      editBtn.style.display = "none";
    });

    cancelBtn.addEventListener("click", () => {
      inputs.forEach((i) => {
        // 값 되돌리기
        if (i.name && original[i.name] !== undefined) {
          i.value = original[i.name];
        }
        i.disabled = true;
      });

      actions.style.display = "none";
      editBtn.style.display = "inline-block";
    });
  }

  // =========================
  // 2) 내 검증 요청 필터
  // =========================
  const filterBtns = document.querySelectorAll(".filter-btn");
  const rows = document.querySelectorAll(".post-row");

  if (filterBtns.length && rows.length) {
    filterBtns.forEach((btn) => {
      btn.addEventListener("click", () => {
        filterBtns.forEach((b) => b.classList.remove("active"));
        btn.classList.add("active");

        const status = btn.dataset.status;

        rows.forEach((row) => {
          const rowStatus = row.dataset.status;

          if (status === "ALL") {
            row.style.display = "";
          } else {
            row.style.display = rowStatus === status ? "" : "none";
          }
        });
      });
    });
  }
});
