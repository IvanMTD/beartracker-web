// JavaScript
/*window.addEventListener('DOMContentLoaded', function() {
    // �������� ������� ��� �������� ��������
    showSpinner();

    // ������ ������� ����� 0.5 �������
    //setTimeout(hideSpinner, 500);
});*/

/*window.addEventListener('load', function() {
    // ������ �������, ����� �������� ��������� �����������
    hideSpinner();
});*/

// �������� ������� ����� ��������� �� ������
/*document.querySelectorAll('a').forEach(function(link) {
    link.addEventListener('click', function(event) {
        // ������������� ����������� ��������
        event.preventDefault();

        // �������� �������
        showSpinner();
        //������� �� ������
        window.location.href = link.href;

        // ������� �� ������ ����� 0.5 �������
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