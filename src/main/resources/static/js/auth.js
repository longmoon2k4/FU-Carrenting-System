document.addEventListener('DOMContentLoaded', function () {
  try {
    console.log('[auth.js] loaded');
  } catch (e) { }
  const modal = document.getElementById('auth-modal');
  const loginBtn = document.getElementById('login');
  const signupBtn = document.getElementById('signup');
  const closeBtn = modal && modal.querySelector('.auth-close');
  const backdrop = modal && modal.querySelector('.auth-modal-backdrop');
  // containers (divs) that hold the forms
  const loginContainer = document.getElementById('login-form');
  const registerContainer = document.getElementById('register-form');
  // actual <form> elements inside the containers
  const loginForm = loginContainer && loginContainer.querySelector('form');
  const registerForm = registerContainer && registerContainer.querySelector('form');

  // initialize header from sessionStorage (if user already logged in this session)
  try {
    const stored = sessionStorage.getItem('furs_user');
    if (stored) { const u = JSON.parse(stored); if (u && u.email) updateHeaderUser(u); }
  } catch (e) { }

  // helper: update header UI when user is logged in
  function updateHeaderUser(user) {
    try {
      const authContainer = document.querySelector('.auth-container');
      if (!authContainer) return;
      const textDndk = authContainer.querySelector('.text-dndk');
      const textTk = authContainer.querySelector('.text-tk');
      // set email in the small slot where 'Đăng nhập / Đăng ký' was
      if (textDndk) textDndk.textContent = user && user.email ? user.email : 'Đăng nhập / Đăng ký';
      // set customer name in the 'Tài khoản' area (keep caret icon)
      if (textTk) textTk.innerHTML = (user && user.customerName ? escapeHtml(user.customerName) : 'Tài khoản') + ' <i class="fa-sharp fa-solid fa-caret-down"></i>';

      // replace menu items (login/signup) with profile/logout links when logged in
      const menu = document.querySelector('.header-middle-right-menu');
      if (menu) {
        if (user && user.email) {
          menu.innerHTML = '\n                            <li><a id="profile" href="/profile"><i class="fa-light fa-user"></i> Hồ sơ</a></li>\n                            <li><a id="transactions" href="/transactions"><i class="fa-light fa-list"></i> Lịch sử GD</a></li>\n                            <li><a id="logout" href="#"><i class="fa-light fa-right-from-bracket"></i> Đăng xuất</a></li>\n                        ';
        } else {
          menu.innerHTML = '\n                            <li><a id="login" href="#" data-auth="login"><i class="fa-light fa-right-to-bracket"></i> Đăng nhập</a></li>\n                            <li><a id="signup" href="#" data-auth="register"><i class="fa-light fa-user-plus"></i> Đăng ký</a></li>\n                        ';
        }
      }
    } catch (e) { console.error('[auth.js] updateHeaderUser error', e); }
  }

  function setCurrentUser(user) {
    try { sessionStorage.setItem('furs_user', JSON.stringify(user || null)); } catch (e) { }
    updateHeaderUser(user);
  }

  function clearCurrentUser() {
    try { sessionStorage.removeItem('furs_user'); } catch (e) { }
    updateHeaderUser(null);
  }

  // expose small helpers so other inline scripts (profile page etc.) can reuse them
  try { window.showToast = showToast; window.setCurrentUser = setCurrentUser; window.clearCurrentUser = clearCurrentUser; } catch (e) { /* noop */ }

  function escapeHtml(str) {
    return String(str).replace(/[&<>"'`]/g, function (m) { return ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": "&#39;", "`": "&#96;" })[m]; });
  }

  // small toast helper
  function ensureToastContainer() {
    let c = document.getElementById('furs-toast-container');
    if (!c) { c = document.createElement('div'); c.id = 'furs-toast-container'; c.style.position = 'fixed'; c.style.top = '1rem'; c.style.right = '1rem'; c.style.zIndex = '9999'; c.style.display = 'flex'; c.style.flexDirection = 'column'; c.style.gap = '0.5rem'; document.body.appendChild(c); }
    return c;
  }
  function showToast(message, type) {
    const c = ensureToastContainer();
    const el = document.createElement('div');
    el.textContent = message;
    el.style.padding = '0.6rem 1rem';
    el.style.borderRadius = '6px';
    el.style.color = '#000';
    el.style.boxShadow = '0 2px 8px rgba(0,0,0,0.15)';
    el.style.fontSize = '0.95rem';
    el.style.background = '#ff9900ff';
    c.appendChild(el);
    setTimeout(() => { el.style.transition = 'opacity 0.4s'; el.style.opacity = '0'; setTimeout(() => el.remove(), 450); }, 3500);
  }

  function openModal(show) {
    if (!modal) return;
    try { 
      // prepare for animated open
      modal.classList.add('is-opening');
      modal.setAttribute('aria-hidden', 'false'); 
    } catch (e) { console.error(e); }
    showForm(show || 'login');
    try { console.log('[auth.js] openModal ->', show); } catch (e) { }
    // ensure display (in case CSS fallback needed)
    try { modal.style.display = 'flex'; } catch (e) { }
    // finalize opening after a tick so CSS transitions apply
    window.requestAnimationFrame(() => { modal.classList.add('is-open'); modal.classList.remove('is-opening'); });
  }
  function closeModal() {
    if (!modal) return;
    try {
      // play close animation
      modal.classList.remove('is-open');
      modal.classList.add('is-closing');
      // after transition, hide
      const cleanup = () => {
        try { modal.setAttribute('aria-hidden', 'true'); modal.style.display = 'none'; } catch (e) { }
        modal.classList.remove('is-closing');
        modal.removeEventListener('transitionend', cleanup);
      };
      modal.addEventListener('transitionend', cleanup);
      // safety fallback: if transitionend doesn't fire, force hide
      setTimeout(cleanup, 380);
    } catch (e) { console.error(e); }
  }
  function showForm(name) {
    // toggle visibility on the container elements (not the <form> elements)
    if (!modal) return;
    try {
      // add switching class to the content to give a tiny tilt effect
      const content = modal.querySelector('.auth-modal-content');
      if (content) content.classList.add('is-switching');
      // perform the toggle
      if (name === 'login') {
        loginContainer && loginContainer.setAttribute('aria-hidden', 'false');
        registerContainer && registerContainer.setAttribute('aria-hidden', 'true');
      } else {
        loginContainer && loginContainer.setAttribute('aria-hidden', 'true');
        registerContainer && registerContainer.setAttribute('aria-hidden', 'false');
      }
      // remove switching after animation frame + small timeout
      setTimeout(() => { if (content) content.classList.remove('is-switching'); }, 220);
    } catch (e) { console.error(e); }
  }

  if (loginBtn) { loginBtn.addEventListener('click', (e) => { e.preventDefault(); openModal('login'); }); console.log('[auth.js] attached #login'); }
  if (signupBtn) { signupBtn.addEventListener('click', (e) => { e.preventDefault(); openModal('register'); }); console.log('[auth.js] attached #signup'); }

  // open modal when clicking the header account area or any element marked with data-auth
  document.addEventListener('click', function (e) {
    try {
      const closestData = e.target.closest && e.target.closest('[data-auth]');
      if (closestData) { const action = closestData.getAttribute('data-auth') || 'login'; openModal(action); e.preventDefault(); return; }
      // legacy id handling
      if (e.target.id === 'login' || (e.target.closest && e.target.closest('#login'))) { e.preventDefault(); openModal('login'); return; }
      if (e.target.id === 'signup' || (e.target.closest && e.target.closest('#signup'))) { e.preventDefault(); openModal('register'); return; }
      if (e.target.closest && e.target.closest('.auth-container')) { openModal('login'); return; }
    } catch (err) { console.error('[auth.js] click handler error', err); }
  });

  // handle client-side logout button (call server to invalidate session, clear stored user and update UI)
  document.addEventListener('click', function (e) {
    try {
      if (e.target.closest && e.target.closest('#logout')) {
        e.preventDefault();
  fetch('/api/auth/logout', { method: 'POST', headers: { 'Content-Type': 'application/json' }, credentials: 'same-origin' })
          .then(() => {
            clearCurrentUser();
            try{ showToast('Đã đăng xuất', 'success'); }catch(e){}
            // redirect to home
            try{ window.location.href = 'http://localhost:8080/'; }catch(e){}
          })
          .catch(err => {
            // still clear local state
            clearCurrentUser();
            try{ showToast('Đã đăng xuất', 'success'); }catch(e){}
            // redirect to home even on error
            try{ window.location.href = 'http://localhost:8080/'; }catch(e){}
          });
      }
    } catch (err) { console.error('[auth.js] logout handler error', err); }
  });

  // close modal with Escape
  document.addEventListener('keydown', function (e) { if (e.key === 'Escape') closeModal(); });
  if (closeBtn) closeBtn.addEventListener('click', closeModal);
  if (backdrop) backdrop.addEventListener('click', closeModal);

  modal && modal.querySelectorAll('.auth-switch').forEach(a => {
    a.addEventListener('click', function (e) { e.preventDefault(); showForm(this.dataset.target); });
  });

  // AJAX login
  // loginForm should be the actual <form> element; guard against missing form
  if (!loginForm) { console.warn('[auth.js] login form not found (expected inside #login-form container)'); }
  if (!registerForm) { console.warn('[auth.js] register form not found (expected inside #register-form container)'); }

  loginForm && loginForm.addEventListener('submit', function (e) {
    e.preventDefault();
    const data = {
      // access inputs via named elements on the form (if present) or querySelector fallback
      email: (loginForm.email && loginForm.email.value) || (loginForm.querySelector('[name="email"]') && loginForm.querySelector('[name="email"]').value) || '',
      password: (loginForm.password && loginForm.password.value) || (loginForm.querySelector('[name="password"]') && loginForm.querySelector('[name="password"]').value) || ''
    };
    fetch('/api/auth/login', {
      method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data), credentials: 'same-origin'
    }).then(r => {
      if (r.ok) return r.json().then(u => ({ status: r.status, body: u }));
      return r.text().then(t => ({ status: r.status, body: t }));
    }).then(res => {
      if (res.status === 200) {
        const user = res.body;
        try { setCurrentUser(user); } catch (e) { }
        closeModal();
        showToast('Đăng nhập thành công', 'success');
        // reload page so server-side session is used and header/templates reflect login
        setTimeout(() => { window.location.reload(); }, 350);
      } else if (res.status === 404) {
        showToast('Email không tồn tại', 'error');
      } else if (res.status === 401) {
        // API returns 'invalid_password'
        showToast('Tên đăng nhập hoặc mật khẩu không đúng', 'error');
      } else {
        showToast('Đăng nhập thất bại', 'error');
      }
    }).catch(err => {
      showToast('Đăng nhập thất bại: ' + err.message, 'error');
    });
  });

  // AJAX register
  registerForm && registerForm.addEventListener('submit', function (e) {
    e.preventDefault();
    const payload = {
      customerName: registerForm.querySelector('[name="customerName"]').value,
      email: registerForm.querySelector('[name="email"]').value,
      password: registerForm.querySelector('[name="password"]').value,
      mobile: registerForm.querySelector('[name="mobile"]').value || '',
      identityCard: registerForm.querySelector('[name="identityCard"]').value || '',
      birthday: registerForm.querySelector('[name="birthday"]').value || null,
      licenceNumber: registerForm.querySelector('[name="licenceNumber"]').value || '',
      licenceDate: registerForm.querySelector('[name="licenceDate"]').value || null
    };
    fetch('/api/auth/register', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload), credentials: 'same-origin' })
      .then(r => { if (r.ok) return r.json(); return r.text().then(t => { throw new Error(t) }); })
      .then(res => {
        if (res.ok) return res.json().then(u => ({ status: res.status, body: u }));
        return res.text().then(t => ({ status: res.status, body: t }));
      }).then(r => {
        if (r.status === 201) {
          try { setCurrentUser(r.body); } catch (e) { }
          closeModal();
          showToast('Đăng ký thành công', 'success');
          // reload to pick up server-side session and header/menu differences
          setTimeout(() => { window.location.reload(); }, 350);
        } else if (r.status === 409) {
          // response body is comma-separated duplicated fields
          const fields = String(r.body).split(',').filter(Boolean);
          const fieldMap = { email: 'Email', mobile: 'Số điện thoại', identityCard: 'CMND/CCCD', licenceNumber: 'Số GPLX' };
          const human = fields.map(f => fieldMap[f] || f).join(', ');
          showToast('Trùng: ' + human, 'error');
        } else {
          showToast('Đăng ký thất bại', 'error');
        }
      }).catch(err => showToast('Đăng ký thất bại: ' + err.message, 'error'));
  });
});
