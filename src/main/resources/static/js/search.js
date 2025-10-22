document.addEventListener('DOMContentLoaded', function(){
  var toggle = document.querySelector('.filter-toggle');
  var panel = document.querySelector('.search-filter-panel');
  var closeBtn = document.querySelector('.filter-close');
  var searchInput = document.querySelector('.search-input');
  var filterHiddenQ = document.querySelector('.filter-form input[name="q"]');
  if(toggle && panel){
    toggle.addEventListener('click', function(){
      var visible = panel.getAttribute('aria-hidden') === 'false';
      panel.setAttribute('aria-hidden', visible ? 'true' : 'false');
    });
  }
  if(closeBtn && panel){
    closeBtn.addEventListener('click', function(){ panel.setAttribute('aria-hidden','true'); });
  }
  // copy q from main search to filter hidden input so submit keeps query
  if(searchInput && filterHiddenQ){
    searchInput.addEventListener('input', function(){ filterHiddenQ.value = this.value; });
  }
});
