document.addEventListener('DOMContentLoaded', function() {
    const selectElement = document.getElementById('location-type');
    const customSelect = document.getElementById('location-type-select');

    const selectBox = document.createElement('div');
    selectBox.className = 'select-selected';
    customSelect.appendChild(selectBox);

    const optionsList = document.createElement('div');
    optionsList.className = 'select-items select-hide';
    customSelect.appendChild(optionsList);

    function createCustomOption(option) {
        const customOption = document.createElement('div');
        customOption.className = 'custom-option';
        const img = document.createElement('img');
        img.src = option.getAttribute('data-image');
        const text = document.createTextNode(option.textContent);
        customOption.appendChild(img);
        customOption.appendChild(text);
        customOption.addEventListener('click', function() {
            selectBox.innerHTML = '';
            const selectedImg = document.createElement('img');
            selectedImg.src = img.src;
            selectBox.appendChild(selectedImg);
            selectBox.appendChild(document.createTextNode(option.textContent));
            selectElement.value = option.value;
            closeAllSelect(customOption);
        });
        return customOption;
    }

    Array.from(selectElement.options).forEach(option => {
        const customOption = createCustomOption(option);
        optionsList.appendChild(customOption);
    });

    selectBox.addEventListener('click', function() {
        optionsList.classList.toggle('select-hide');
        selectBox.classList.toggle('select-arrow-active');
    });

    function closeAllSelect(elmnt) {
        const selectItems = document.getElementsByClassName('select-items');
        const selectSelected = document.getElementsByClassName('select-selected');
        for (let i = 0; i < selectSelected.length; i++) {
            if (elmnt !== selectSelected[i]) {
                selectSelected[i].classList.remove('select-arrow-active');
            }
        }
        for (let i = 0; i < selectItems.length; i++) {
            if (elmnt !== selectItems[i]) {
                selectItems[i].classList.add('select-hide');
            }
        }
    }

    document.addEventListener('click', function(e) {
        if (!customSelect.contains(e.target)) {
            closeAllSelect();
        }
    });

    // Set initial selected option
    const initialSelectedOption = selectElement.options[selectElement.selectedIndex];
    const initialImg = document.createElement('img');
    initialImg.src = initialSelectedOption.getAttribute('data-image');
    selectBox.appendChild(initialImg);
    selectBox.appendChild(document.createTextNode(initialSelectedOption.textContent));
});