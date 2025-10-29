const checkbox = document.getElementById("visibility");
const labelText = document.getElementById("visibilityText");
      
checkbox.addEventListener("change", function() {
   if (checkbox.checked) {
       labelText.textContent = "비공개";
    } else {
      labelText.textContent = "공개";
        }
});