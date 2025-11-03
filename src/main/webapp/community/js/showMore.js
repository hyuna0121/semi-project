const modal = document.querySelector('#modal');
const modalBtn = document.querySelector('.showMore');
const closeBtn = document.querySelector('#modal .btn-close');

const imageModal = document.querySelector('#imageModal');
const imageModalBtnList = document.querySelectorAll('.project .btn-modal-image');
const imageCloseBtn = document.querySelector('#imageModal .btn-close');
const imageEl = document.querySelector('#imageModal img');

modalBtn.addEventListener('click', function () {
  modal.style.display = 'flex';
});
closeBtn.addEventListener('click', function () {
  modal.style.display = 'none';
});

imageModalBtnList.forEach(function (imageModalBtn) {
  imageModalBtn.addEventListener('click', function () {
    imageEl.src = imageModalBtn.dataset.imageSrc;
    imageModal.style.display = 'flex';
  });
});
imageCloseBtn.addEventListener('click', function () {
  imageModal.style.display = 'none';
});


modal.addEventListener('click', function(e){ 
  console.log(e.target);
  console.log(e.currentTarget); 

  if(e.target === e.currentTarget){ 
    modal.style.display = 'none'
  }
})


document.addEventListener('keydown', function(e){
  if(e.key === 'Escape'){
    modal.style.display = 'none'
    imageModal.style.display = 'none'
  }
});



const hamburgerBtn = document.querySelector('.btn-hamburger');
const nevEl = document.querySelector('header nav');

hamburgerBtn.addEventListener('click', function(){
  nevEl.classList.toggle('active');
});

const muneli = document.querySelectorAll('header li');

muneli.forEach(function(e){
  e.addEventListener('click', function(){
    nevEl.classList.remove('active');
  });
});