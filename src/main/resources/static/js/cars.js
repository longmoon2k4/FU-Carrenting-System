document.addEventListener('DOMContentLoaded', function(){
  const container = document.getElementById('car-cards');
  if(!container) return;

  function cardFor(car){
    const div = document.createElement('div');
    div.className = 'card car-card';
    div.innerHTML = `
      <img src="${car.imageUrl || '/img/car-1.jpg'}" alt="${escapeHtml(car.carName||'Xe')}" class="banner-image" style="height:160px;border-radius:10px;object-fit:cover" />
      <div class="card-body">
        <h3>${escapeHtml(car.carName||'Xe')}</h3>
        <div class="price">${car.rentPrice ? car.rentPrice + ' VND/ngày' : 'Liên hệ'}</div>
        <p>${escapeHtml(car.description||'Không có mô tả')}</p>
        <div style="display:flex;gap:8px; margin-top:12px">
          <a class="btn btn-primary" href="/rent?carId=${car.carId}">Thuê</a>
          <a class="btn btn-secondary" href="/cars/${car.carId}">Chi tiết</a>
        </div>
      </div>
    `;
    return div;
  }

  function escapeHtml(s){return String(s||'').replace(/[&<>'"]/g, function(m){return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":"&#39;"})[m];});}

  fetch('/api/cars').then(r=>r.json()).then(list=>{
    container.innerHTML='';
    if(!Array.isArray(list) || list.length===0){ container.innerHTML = '<div class="card">Chưa có xe nào.</div>'; return; }
    list.forEach(c=> container.appendChild(cardFor(c)));
  }).catch(err=>{ container.innerHTML = '<div class="card">Lỗi tải xe: '+ (err.message||err) +'</div>'; });
});