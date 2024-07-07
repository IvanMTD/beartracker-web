const params = new URLSearchParams(window.location.search);
if(params.has('error')){
    showToast('error','Неверный логин или пароль!');
}
function showToast(type, message){
    let infoToast = $('#info-toast');
    infoToast.empty();
    if(type === 'error'){
        infoToast.append(
            '<div class="toast align-items-center border-danger bg-danger-subtle" role="alert" aria-live="assertive" aria-atomic="true">\n' +
            '   <div class="d-flex">\n' +
            '       <div class="toast-body">\n' +
            '           <p class="text-danger">' + message + '</p>\n' +
            '       </div>\n' +
            '       <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>\n' +
            '   </div>\n' +
            '</div>'
        );
    }else if(type === 'warning'){
        infoToast.append(
            '<div class="toast align-items-center border-warning bg-warning-subtle" role="alert" aria-live="assertive" aria-atomic="true">\n' +
            '   <div class="d-flex">\n' +
            '       <div class="toast-body">\n' +
            '           <p class="text-warning">' + message + '</p>\n' +
            '       </div>\n' +
            '       <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>\n' +
            '   </div>\n' +
            '</div>'
        );
    }else{
        infoToast.append(
            '<div class="toast align-items-center border-success bg-success-subtle" role="alert" aria-live="assertive" aria-atomic="true">\n' +
            '   <div class="d-flex">\n' +
            '       <div class="toast-body">\n' +
            '           <p class="text-success">' + message + '</p>\n' +
            '       </div>\n' +
            '       <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>\n' +
            '   </div>\n' +
            '</div>'
        );
    }

    let toastElement = infoToast.find('.toast');
    let completedToast = new bootstrap.Toast(toastElement[0]);
    completedToast.show();
}