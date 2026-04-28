// ─── STAR PICKER ────────────────────────────────────────────
(function () {
  const picker = document.getElementById('starPicker');
  const ratingInput = document.getElementById('ratingInput');
  if (!picker) return;

  const stars = picker.querySelectorAll('.star-pick');

  function setStars(val) {
    stars.forEach((s, i) => {
      s.textContent = i < val ? '★' : '☆';
      s.classList.toggle('selected', i < val);
    });
    if (ratingInput) ratingInput.value = val;
  }

  // default = 5
  setStars(5);

  stars.forEach((star, idx) => {
    star.addEventListener('mouseenter', () => setStars(idx + 1));
    star.addEventListener('click', () => setStars(idx + 1));
  });
  picker.addEventListener('mouseleave', () => {
    const cur = parseInt(ratingInput?.value || '5');
    setStars(cur);
  });
})();

// ─── CART BADGE ──────────────────────────────────────────────
(function () {
  const badge = document.getElementById('cartBadge');
  if (!badge) return;

  // Read cart count from session via a tiny endpoint if available
  // For now count items via DOM when on cart page
  const cartRows = document.querySelectorAll('.cart-row');
  if (cartRows.length > 0) {
    badge.textContent = cartRows.length;
    badge.style.display = 'inline-flex';
  }
})();

// ─── AUTO-DISMISS ALERTS ─────────────────────────────────────
(function () {
  const alerts = document.querySelectorAll('.alert');
  alerts.forEach(alert => {
    setTimeout(() => {
      alert.style.transition = 'opacity .5s ease';
      alert.style.opacity = '0';
      setTimeout(() => alert.remove(), 500);
    }, 4000);
  });
})();

// ─── PAYMENT OPTION HIGHLIGHT ─────────────────────────────────
(function () {
  const options = document.querySelectorAll('.payment-option');
  options.forEach(opt => {
    const radio = opt.querySelector('input[type=radio]');
    const body = opt.querySelector('.payment-option-body');
    if (!radio || !body) return;
    opt.addEventListener('click', () => {
      radio.checked = true;
      // remove highlight from all
      document.querySelectorAll('.payment-option-body').forEach(b => {
        b.style.borderColor = '';
        b.style.background = '';
      });
      // highlight selected
      body.style.borderColor = 'var(--accent)';
      body.style.background = 'rgba(224,123,57,.06)';
    });
  });
})();

// ─── CONFIRM DELETES ─────────────────────────────────────────
(function () {
  document.querySelectorAll('form[onsubmit]').forEach(form => {
    // already handled via onsubmit attr, just ensure propagation
  });
})();

// ─── ACTIVE NAV LINK ─────────────────────────────────────────
(function () {
  const path = window.location.pathname;
  document.querySelectorAll('.nav-link').forEach(link => {
    const href = link.getAttribute('href');
    if (href && path.startsWith(href) && href !== '/') {
      link.classList.add('active');
    }
  });
})();

// ─── SMOOTH SCROLL FOR ANCHOR LINKS ──────────────────────────
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
  anchor.addEventListener('click', function (e) {
    const target = document.querySelector(this.getAttribute('href'));
    if (target) {
      e.preventDefault();
      target.scrollIntoView({ behavior: 'smooth' });
    }
  });
});

// ─── QUANTITY INPUT GUARD ────────────────────────────────────
document.querySelectorAll('input[type=number]').forEach(input => {
  input.addEventListener('change', function () {
    const min = parseInt(this.min) || 0;
    const max = parseInt(this.max) || 9999;
    if (this.value < min) this.value = min;
    if (this.value > max) this.value = max;
  });
});
