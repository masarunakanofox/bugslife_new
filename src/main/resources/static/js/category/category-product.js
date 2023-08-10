$(document).ready(function () {
  let categoryId = document.getElementById("categoryId").getAttribute("val");
  let action = document.getElementById("action").getAttribute("val");

  function restoreCheckedItems() {
    var savedCheckedIds = JSON.parse(localStorage.getItem('checkedIds')) || [];
    savedCheckedIds.forEach(function (productId) {
      $("#checkbox-" + productId).prop("checked", true);
    });
  }

  // ページロード時にLocalStorageからチェックボックスの状態を復元
  restoreCheckedItems();

  $.ajax({
    url: "/api/category/" + categoryId,
    type: "GET",
    dataType: "json",
  })
    .done(function (data) {
      var categoryProducts = data;
      // チェックボックスにチェックを入れる処理
      categoryProducts.forEach(function (categoryProduct) {
        $("#checkbox-" + categoryProduct.productId).prop("checked", true);
      });
    })
    .fail(function () {
      // APIコールが失敗した場合の処理
      console.log("APIコールが失敗しました。");
      alert("APIコールが失敗しました。");
    });

// 既存のコードを保持したまま、以下に新しいボタンのクリックイベントを追加します
$("#update-button").click(function () {
  var checkedIds = $(".form-check-input:checked")
    .map(function () {
      return this.value;
    })
    .get();

  // 作成更新時に紐付けが存在しない場合はスキップ
  if (action == "true") {
    if (!validation(checkedIds)) {
      return false;
    }
  }

  let postData = {
    productIds: checkedIds,
  };

  $.ajax({
    url: "/api/categories/" + categoryId + "/updateCategoryProduct",
    type: "POST",
    dataType: "text",
    contentType: "application/json",
    data: JSON.stringify(postData), // 注意: データをJSON文字列に変換する必要があります
  }).done(function (data) {
    // 紐づけ更新後にチェックされた商品のIDをLocalStorageに保存
    localStorage.setItem('checkedIds', JSON.stringify(checkedIds));

    $("#success-message").text(data).show().fadeOut(3000);
  });
});

// 既存のコードを保持したまま、以下に既存の validation 関数を追加します
validation = function (checkedIds) {
  if (checkedIds.length == 0) {
    $("#error-message")
      .text(
        "商品を選択して更新か、不要な場合はカテゴリー一覧を選択して下さい。"
      )
      .show()
      .fadeOut(3000);
    return false;
  }
  return true;
};
});
