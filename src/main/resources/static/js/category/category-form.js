$(document).ready(function () {
  $("#category-form").on("submit", function (event) {
    if (!validateForm()) {
      event.preventDefault();
      console.log("form validation failed");
    }
  });

  function validateForm() {
    let isCodeValid = true;
    let isNameValid = true;
    let isDisplayOrderValid = true;

    // codeのバリデーション
    const codeInput = $("#code");
    const codeValue = codeInput.val().trim();
    const codePattern = /^[A-Z]{3}-\d{3}$/;

    if (!codePattern.test(codeValue)) {
      codeInput.addClass("is-invalid");
      isCodeValid = false;
    } else {
      codeInput.removeClass("is-invalid");
    }

    // nameのバリデーション
    const nameInput = $("#name");
    const nameValue = nameInput.val();

    if (nameValue === "" || nameValue.length > 20) {
      nameInput.addClass("is-invalid");
      isNameValid = false;
    } else {
      nameInput.removeClass("is-invalid");
    }

    // display_orderのバリデーション
    const displayOrderInput = $("#display_order");
    const displayOrderValue = displayOrderInput.val();

    if (displayOrderValue === "" || isNaN(displayOrderValue) || displayOrderValue < 0 || displayOrderValue > 999) {
      displayOrderInput.addClass("is-invalid");
      isDisplayOrderValid = false;
    } else {
      displayOrderInput.removeClass("is-invalid");
    }

    // 最終的なバリデーション結果をまとめる
    const isValid = isCodeValid && isNameValid && isDisplayOrderValid;
    return isValid;
  }
});