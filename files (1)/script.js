function showView(id){
  document.querySelectorAll('.view').forEach(function(v){
    v.classList.remove('active');
  });
  document.getElementById(id).classList.add('active');
}

document.getElementById('btn-player').addEventListener('click', function(){
  showView('view-player');
});
document.getElementById('btn-master').addEventListener('click', function(){
  showView('view-master');
});
document.getElementById('btn-back-player').addEventListener('click', function(){
  showView('view-home');
});
document.getElementById('btn-back-master').addEventListener('click', function(){
  showView('view-home');
});
