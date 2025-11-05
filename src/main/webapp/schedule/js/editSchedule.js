imageInput.addEventListener('change', function(event) {
	const file = event.target.files[0];
    if (file) {
	    const reader = new FileReader();	        
        reader.onload = function(e) {
	        imagePreview.src = e.target.result;
    }
    	reader.readAsDataURL(file);
    } else {
		imagePreview.src = "/upload/<%= schedule.getMainImage() %>";
    }
});
	
const checkbox = document.getElementById("visibility");
const labelText = document.getElementById("visibilityText");
          
checkbox.addEventListener("change", function() {
	if (this.checked) {
		labelText.textContent = "lock";
        this.value = "N"; 
    } else {
        labelText.textContent = "lock_open_right";
        this.value = "Y"; 
    }
});