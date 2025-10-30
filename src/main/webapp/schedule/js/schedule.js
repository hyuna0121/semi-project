const checkbox = document.getElementById("visibility");
const labelText = document.getElementById("visibilityText");
      
checkbox.addEventListener("change", function() {
   if (checkbox.checked) {
       labelText.textContent = "비공개";
    } else {
      labelText.textContent = "공개";
        }
});	

$('#demo').daterangepicker({
	"locale": {
		"format": "YYYY-MM-DD",
		"separator": " ~ ",
		"applyLabel": "확인",
		"cancelLabel": "취소",
		"fromLabel": "From",
		"toLabel": "To",
		"customRangeLabel": "Custom",
		"weekLabel": "W",
		"daysOfWeek": ["일", "월", "화", "수", "목", "금", "토"],
		"monthNames": ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
		},
	"startDate": new Date(),
	"endDate": new Date(),
	"drops": "auto"
}, function (start, end, label) {
	console.log('New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')');
});
