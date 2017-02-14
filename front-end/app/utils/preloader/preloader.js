document.addEventListener("DOMContentLoaded", function () {
    var element = document.getElementById("preloader");
    setTimeout(function () {
        element.parentNode.removeChild(element);
    }, 3000)
});
