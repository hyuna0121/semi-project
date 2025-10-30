/**
 * 
 */
document.addEventListener('DOMContentLoaded', function () {
  const slides = Array.from(document.querySelectorAll('.main__bg'));
  if (slides.length < 2) return;

  let idx = slides.findIndex(el => el.classList.contains('active'));
  if (idx < 0) { idx = 0; slides[0].classList.add('active'); }

  setInterval(() => {
    slides[idx].classList.remove('active');
    idx = (idx + 1) % slides.length;
    slides[idx].classList.add('active');
  }, 5000);
});
