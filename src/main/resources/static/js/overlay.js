// JavaScript
/*window.addEventListener('DOMContentLoaded', function() {
    // Показать спиннер при загрузке страницы
    showSpinner();

    // Скрыть спиннер через 0.5 секунды
    //setTimeout(hideSpinner, 500);
});*/

/*window.addEventListener('load', function() {
    // Скрыть спиннер, когда страница полностью загрузилась
    hideSpinner();
});*/

// Показать спиннер перед переходом по ссылке
/*document.querySelectorAll('a').forEach(function(link) {
    link.addEventListener('click', function(event) {
        // Предотвратить стандартное действие
        event.preventDefault();

        // Показать спиннер
        showSpinner();
        //Перейти по ссылке
        window.location.href = link.href;

        // Перейти по ссылке через 0.5 секунды
        /!*setTimeout(function() {
            window.location.href = link.href;
        }, 500);*!/
    });
});*/

hideSpinner();

function showSpinner() {
    document.getElementById('overlay').style.display = 'flex';
}

function hideSpinner() {
    document.getElementById('overlay').style.display = 'none';
}