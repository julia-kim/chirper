$(document).ready(function () {
  $(".unfollow_btn").hover(
    function () {
      $(this).removeClass("btn-chirper")
      $(this).addClass("btn-chirper-danger")
      $(this).html("Unfollow")
    },
    function () {
      $(this).html("Following")
      $(this).removeClass("btn-chirper-danger")
      $(this).addClass("btn-chirper")
    }
  )
})