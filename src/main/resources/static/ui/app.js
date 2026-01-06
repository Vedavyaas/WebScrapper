(() => {
    const api = {
        sendOtp: async (email) => {
            const url = `/get/OTP?email=${encodeURIComponent(email)}`;
            return fetch(url, { method: 'GET' });
        },
        createAccount: async (email, password, otp) => {
            const url = `/create/account?OTP=${encodeURIComponent(otp)}`;
            return fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });
        },
        authenticate: async (email, password) => {
            return fetch('/authenticate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });
        },
        forgotPassword: async (email, otp, newPassword) => {
            const url = `/forget/password?email=${encodeURIComponent(email)}&OTP=${encodeURIComponent(otp)}&newPassword=${encodeURIComponent(newPassword)}`;
            return fetch(url, { method: 'PUT' });
        },

        addScrap: async (token, url, targetPrice) => {
            const endpoint = `/post/url?url=${encodeURIComponent(url)}&targetPrice=${encodeURIComponent(String(targetPrice))}`;
            return fetch(endpoint, {
                method: 'POST',
                headers: {
                    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
                }
            });
        },

        listScraps: async (token) => {
            return fetch('/get/url', {
                method: 'GET',
                headers: {
                    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
                }
            });
        },

        changeScrapUrl: async (token, id, url) => {
            const endpoint = `/change/url?url=${encodeURIComponent(url)}&id=${encodeURIComponent(String(id))}`;
            return fetch(endpoint, {
                method: 'PUT',
                headers: {
                    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
                }
            });
        },

        changeScrapPrice: async (token, id, newPrice) => {
            const endpoint = `/change/price?newPrice=${encodeURIComponent(String(newPrice))}&id=${encodeURIComponent(String(id))}`;
            return fetch(endpoint, {
                method: 'PUT',
                headers: {
                    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
                }
            });
        },

        deleteScrap: async (token, id) => {
            const endpoint = `/delete/scrap?id=${encodeURIComponent(String(id))}`;
            return fetch(endpoint, {
                method: 'DELETE',
                headers: {
                    ...(token ? { 'Authorization': `Bearer ${token}` } : {})
                }
            });
        }
    };

    const el = (id) => {
        const node = document.getElementById(id);
        if (!node) throw new Error(`Missing element: ${id}`);
        return node;
    };

    const maybeEl = (id) => document.getElementById(id);

    function setMsg(targetId, text, tone) {
        const node = maybeEl(targetId);
        if (!node) return;
        node.hidden = false;
        node.className = `msg ${tone || ''}`.trim();
        node.textContent = text;
    }

    function clearMsg(targetId) {
        const node = maybeEl(targetId);
        if (!node) return;
        node.hidden = true;
        node.textContent = '';
        node.className = 'msg';
    }

    function setToken(token) {
        if (token) {
            localStorage.setItem('jwt', token);
        } else {
            localStorage.removeItem('jwt');
        }

        const hasToken = Boolean(localStorage.getItem('jwt'));
        const sessionState = maybeEl('sessionState');
        if (sessionState) sessionState.textContent = hasToken ? 'Signed in' : 'Signed out';

        const signOutBtn = maybeEl('signOutBtn');
        if (signOutBtn) signOutBtn.disabled = !hasToken;

        // Populate signed-in email on dashboard (best-effort)
        const emailInput = document.getElementById('changePwdEmail');
        if (emailInput) {
            emailInput.value = hasToken ? (getJwtSubject(localStorage.getItem('jwt')) || '') : '';
        }
    }

    function currentToken() {
        return localStorage.getItem('jwt');
    }

    function base64UrlToString(input) {
        if (!input) return '';
        const b64 = input.replace(/-/g, '+').replace(/_/g, '/');
        const padLen = (4 - (b64.length % 4)) % 4;
        const padded = b64 + '='.repeat(padLen);
        try {
            return atob(padded);
        } catch {
            return '';
        }
    }

    function getJwtSubject(jwt) {
        if (!jwt || typeof jwt !== 'string') return null;
        const parts = jwt.split('.');
        if (parts.length < 2) return null;
        try {
            const payloadJson = base64UrlToString(parts[1]);
            const payload = JSON.parse(payloadJson);
            return typeof payload?.sub === 'string' ? payload.sub : null;
        } catch {
            return null;
        }
    }

    function normalizeNumber(value) {
        if (value === null || value === undefined) return null;
        const num = Number(value);
        return Number.isFinite(num) ? num : null;
    }

    function renderScrapList(items) {
        const host = document.getElementById('scrapList');
        if (!host) return;
        host.innerHTML = '';

        if (!Array.isArray(items) || items.length === 0) {
            const empty = document.createElement('div');
            empty.className = 'msg';
            empty.textContent = 'No items yet. Add a product URL and target price.';
            host.appendChild(empty);
            return;
        }

        for (const item of items) {
            const card = document.createElement('div');
            card.className = 'item';

            const link = document.createElement('a');
            link.href = item.url || '#';
            link.target = '_blank';
            link.rel = 'noopener noreferrer';
            link.textContent = item.url || '(no url)';
            card.appendChild(link);

            const meta = document.createElement('div');
            meta.className = 'meta';
            const id = document.createElement('span');
            id.textContent = `ID: ${item.id ?? '-'}`;
            const price = document.createElement('span');
            price.textContent = `Target: ${item.targetPrice ?? '-'}`;
            meta.appendChild(id);
            meta.appendChild(price);
            card.appendChild(meta);

            host.appendChild(card);
        }
    }

    async function loadScraps() {
        const token = currentToken();
        if (!token) return;

        const msgId = 'dashMsg';
        try {
            const res = await api.listScraps(token);
            const body = await readTextOrJson(res);

            if (!res.ok) {
                const text = typeof body === 'string' ? body : JSON.stringify(body);
                setMsg(msgId, `Failed to load watchlist (${res.status}).\n${text || ''}`, 'bad');
                renderScrapList([]);
                return;
            }

            clearMsg(msgId);
            renderScrapList(body);
        } catch (e) {
            setMsg(msgId, `Network error: ${e?.message || e}`, 'bad');
            renderScrapList([]);
        }
    }

    function activateTab(tabId) {
        const tabs = [
            { tab: 'tabSignup', pane: 'paneSignup' },
            { tab: 'tabLogin', pane: 'paneLogin' },
            { tab: 'tabForgot', pane: 'paneForgot' }
        ];

        for (const t of tabs) {
            const selected = t.tab === tabId;
            el(t.tab).setAttribute('aria-selected', String(selected));
            el(t.pane).hidden = !selected;
        }

        clearMsg('signupMsg');
        clearMsg('loginMsg');
        clearMsg('forgotMsg');
    }

    function tabIdToHash(tabId) {
        switch (tabId) {
            case 'tabSignup':
                return '#signup';
            case 'tabLogin':
                return '#login';
            case 'tabForgot':
                return '#forgot';
            default:
                return '#signup';
        }
    }

    function hashToTabId(hash) {
        switch ((hash || '').toLowerCase()) {
            case '#login':
                return 'tabLogin';
            case '#forgot':
                return 'tabForgot';
            case '#signup':
            case '':
            case '#':
            default:
                return 'tabSignup';
        }
    }

    function setRoute(tabId, { replace = false } = {}) {
        const desired = tabIdToHash(tabId);
        if (replace) {
            history.replaceState(null, '', desired);
        } else {
            if (location.hash !== desired) location.hash = desired;
        }
        activateTab(tabId);
    }

    async function readTextOrJson(res) {
        const contentType = res.headers.get('content-type') || '';
        if (contentType.includes('application/json')) {
            try { return await res.json(); } catch { return null; }
        }
        return await res.text();
    }

    async function apiHealthCheck() {
        try {
            const res = await fetch('/actuator/health', { method: 'GET' });
            if (!res.ok) throw new Error('not ok');
            const apiDot = maybeEl('apiDot');
            const apiText = maybeEl('apiText');
            if (apiDot) apiDot.className = 'dot good';
            if (apiText) apiText.textContent = 'Service: online';
        } catch {
            const apiDot = maybeEl('apiDot');
            const apiText = maybeEl('apiText');
            if (apiDot) apiDot.className = 'dot warn';
            if (apiText) apiText.textContent = 'Service: unknown';
        }
    }

    function setBusy(button, isBusy, busyText) {
        if (!button) return;
        if (!button.dataset.origText) button.dataset.origText = button.textContent || '';
        button.disabled = isBusy;
        button.textContent = isBusy ? (busyText || 'Working…') : button.dataset.origText;
    }

    // Tabs + routing (login page only)
    const tabSignup = maybeEl('tabSignup');
    const tabLogin = maybeEl('tabLogin');
    const tabForgot = maybeEl('tabForgot');
    if (tabSignup && tabLogin && tabForgot) {
        tabSignup.addEventListener('click', () => setRoute('tabSignup'));
        tabLogin.addEventListener('click', () => setRoute('tabLogin'));
        tabForgot.addEventListener('click', () => setRoute('tabForgot'));

        window.addEventListener('hashchange', () => {
            activateTab(hashToTabId(location.hash));
        });
    }

    // OTP cooldown (client-side hint only)
    const cooldown = { signup: 0, forgot: 0, change: 0 };
    function startCooldown(kind, btnId) {
        cooldown[kind] = 30;
        const btn = el(btnId);
        btn.disabled = true;
        const tick = () => {
            cooldown[kind] -= 1;
            if (cooldown[kind] <= 0) {
                btn.disabled = false;
                btn.textContent = 'Send OTP';
                return;
            }
            btn.textContent = `Send OTP (${cooldown[kind]}s)`;
            setTimeout(tick, 1000);
        };
        btn.textContent = `Send OTP (${cooldown[kind]}s)`;
        setTimeout(tick, 1000);
    }

    // Signup send OTP
    const signupSendOtpBtn = maybeEl('signupSendOtpBtn');
    if (signupSendOtpBtn) signupSendOtpBtn.addEventListener('click', async () => {
        clearMsg('signupMsg');
        const email = el('signupEmail').value.trim();
        if (!email) {
            setMsg('signupMsg', 'Enter an email first.', 'warn');
            return;
        }

        setBusy(signupSendOtpBtn, true, 'Sending…');
        try {
            const res = await api.sendOtp(email);
            const body = await readTextOrJson(res);
            const text = typeof body === 'string' ? body : JSON.stringify(body);
            if (!res.ok) {
                setMsg('signupMsg', `OTP request failed (${res.status}).\n${text || ''}`, 'bad');
                return;
            }
            setMsg('signupMsg', text || 'OTP request sent.', 'good');
            startCooldown('signup', 'signupSendOtpBtn');
            el('signupOtp').focus();
        } catch (e) {
            setMsg('signupMsg', `Network error: ${e?.message || e}`, 'bad');
        } finally {
            if (cooldown.signup <= 0) setBusy(signupSendOtpBtn, false);
        }
    });

    // Signup create account
    const signupForm = maybeEl('signupForm');
    if (signupForm) signupForm.addEventListener('submit', async (ev) => {
        ev.preventDefault();
        clearMsg('signupMsg');

        const email = el('signupEmail').value.trim();
        const password = el('signupPassword').value;
        const otp = el('signupOtp').value.trim();

        if (!email || !password || !otp) {
            setMsg('signupMsg', 'Fill email, password, and OTP.', 'warn');
            return;
        }

        const submitBtn = signupForm.querySelector('button[type="submit"]');
        setBusy(submitBtn, true, 'Creating…');
        try {
            const res = await api.createAccount(email, password, otp);
            const body = await readTextOrJson(res);
            const text = typeof body === 'string' ? body : JSON.stringify(body);

            if (!res.ok) {
                setMsg('signupMsg', `Signup failed (${res.status}).\n${text || ''}`, 'bad');
                return;
            }

            setMsg('signupMsg', text || 'User created.', 'good');
            // Friendly redirect: take the user to Login and prefill email
            el('loginEmail').value = email;
            setRoute('tabLogin');
        } catch (e) {
            setMsg('signupMsg', `Network error: ${e?.message || e}`, 'bad');
        } finally {
            setBusy(submitBtn, false);
        }
    });

    // Login
    const loginForm = maybeEl('loginForm');
    if (loginForm) loginForm.addEventListener('submit', async (ev) => {
        ev.preventDefault();
        clearMsg('loginMsg');

        const email = el('loginEmail').value.trim();
        const password = el('loginPassword').value;

        if (!email || !password) {
            setMsg('loginMsg', 'Enter email and password.', 'warn');
            return;
        }

        const submitBtn = loginForm.querySelector('button[type="submit"]');
        setBusy(submitBtn, true, 'Authenticating…');
        try {
            const res = await api.authenticate(email, password);
            const body = await readTextOrJson(res);

            if (!res.ok) {
                const text = typeof body === 'string' ? body : JSON.stringify(body);
                setMsg('loginMsg', `Auth failed (${res.status}).\n${text || ''}`, 'bad');
                return;
            }

            const token = body && typeof body === 'object' ? body.token : null;
            if (!token) {
                setMsg('loginMsg', 'Authenticated, but no token returned.', 'warn');
                return;
            }

            setToken(token);
            setMsg('loginMsg', 'Signed in successfully.', 'good');
            window.location.href = '/ui/dashboard';
        } catch (e) {
            setMsg('loginMsg', `Network error: ${e?.message || e}`, 'bad');
        } finally {
            setBusy(submitBtn, false);
        }
    });

    const signOutBtn = maybeEl('signOutBtn');
    if (signOutBtn) signOutBtn.addEventListener('click', () => {
        clearMsg('loginMsg');
        clearMsg('dashMsg');
        clearMsg('changePwdMsg');
        setToken(null);
        window.location.href = '/';
    });

    function ensureSignedInOrRedirect() {
        const token = currentToken();
        if (!token) {
            window.location.href = '/';
            return false;
        }
        return true;
    }

    // Dashboard actions
    const addScrapForm = document.getElementById('addScrapForm');
    if (addScrapForm) {
        if (!ensureSignedInOrRedirect()) return;
        addScrapForm.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            clearMsg('dashMsg');
            const token = currentToken();
            if (!token) {
                setMsg('dashMsg', 'Please sign in first.', 'warn');
                return;
            }

            const url = (document.getElementById('scrapUrl')?.value || '').trim();
            const targetPrice = normalizeNumber(document.getElementById('scrapTargetPrice')?.value);
            if (!url || targetPrice === null) {
                setMsg('dashMsg', 'Enter a valid URL and target price.', 'warn');
                return;
            }

            const btn = document.getElementById('addScrapBtn');
            setBusy(btn, true, 'Adding…');
            try {
                const res = await api.addScrap(token, url, targetPrice);
                const body = await readTextOrJson(res);
                const text = typeof body === 'string' ? body : JSON.stringify(body);

                if (!res.ok) {
                    setMsg('dashMsg', `Add failed (${res.status}).\n${text || ''}`, 'bad');
                    return;
                }

                setMsg('dashMsg', text || 'Added.', 'good');
                document.getElementById('scrapUrl').value = '';
                document.getElementById('scrapTargetPrice').value = '';
                await loadScraps();
            } catch (e) {
                setMsg('dashMsg', `Network error: ${e?.message || e}`, 'bad');
            } finally {
                setBusy(btn, false);
            }
        });
    }

    const refreshBtn = document.getElementById('refreshScrapsBtn');
    if (refreshBtn) refreshBtn.addEventListener('click', () => loadScraps());

    const updateUrlForm = document.getElementById('updateUrlForm');
    if (updateUrlForm) {
        if (!ensureSignedInOrRedirect()) return;
        updateUrlForm.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            clearMsg('dashMsg');
            const token = currentToken();
            if (!token) {
                setMsg('dashMsg', 'Please sign in first.', 'warn');
                return;
            }

            const id = normalizeNumber(document.getElementById('updateUrlId')?.value);
            const url = (document.getElementById('updateUrlValue')?.value || '').trim();
            if (!id || !url) {
                setMsg('dashMsg', 'Enter a valid ID and URL.', 'warn');
                return;
            }

            try {
                const res = await api.changeScrapUrl(token, id, url);
                const body = await readTextOrJson(res);
                const text = typeof body === 'string' ? body : JSON.stringify(body);
                if (!res.ok) {
                    setMsg('dashMsg', `Change URL failed (${res.status}).\n${text || ''}`, 'bad');
                    return;
                }
                setMsg('dashMsg', text || 'URL updated.', 'good');
                await loadScraps();
            } catch (e) {
                setMsg('dashMsg', `Network error: ${e?.message || e}`, 'bad');
            }
        });
    }

    const updatePriceForm = document.getElementById('updatePriceForm');
    if (updatePriceForm) {
        if (!ensureSignedInOrRedirect()) return;
        updatePriceForm.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            clearMsg('dashMsg');
            const token = currentToken();
            if (!token) {
                setMsg('dashMsg', 'Please sign in first.', 'warn');
                return;
            }

            const id = normalizeNumber(document.getElementById('updatePriceId')?.value);
            const price = normalizeNumber(document.getElementById('updatePriceValue')?.value);
            if (!id || price === null) {
                setMsg('dashMsg', 'Enter a valid ID and price.', 'warn');
                return;
            }

            try {
                const res = await api.changeScrapPrice(token, id, price);
                const body = await readTextOrJson(res);
                const text = typeof body === 'string' ? body : JSON.stringify(body);
                if (!res.ok) {
                    setMsg('dashMsg', `Change price failed (${res.status}).\n${text || ''}`, 'bad');
                    return;
                }
                setMsg('dashMsg', text || 'Price updated.', 'good');
                await loadScraps();
            } catch (e) {
                setMsg('dashMsg', `Network error: ${e?.message || e}`, 'bad');
            }
        });
    }

    const deleteForm = document.getElementById('deleteScrapForm');
    if (deleteForm) {
        if (!ensureSignedInOrRedirect()) return;
        deleteForm.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            clearMsg('dashMsg');
            const token = currentToken();
            if (!token) {
                setMsg('dashMsg', 'Please sign in first.', 'warn');
                return;
            }

            const id = normalizeNumber(document.getElementById('deleteScrapId')?.value);
            if (!id) {
                setMsg('dashMsg', 'Enter a valid ID.', 'warn');
                return;
            }

            try {
                const res = await api.deleteScrap(token, id);
                const body = await readTextOrJson(res);
                const text = typeof body === 'string' ? body : JSON.stringify(body);
                if (!res.ok) {
                    setMsg('dashMsg', `Delete failed (${res.status}).\n${text || ''}`, 'bad');
                    return;
                }
                setMsg('dashMsg', text || 'Deleted.', 'good');
                await loadScraps();
            } catch (e) {
                setMsg('dashMsg', `Network error: ${e?.message || e}`, 'bad');
            }
        });
    }

    // Change password (OTP-based) inside dashboard
    const changePwdSendOtpBtn = document.getElementById('changePwdSendOtpBtn');
    if (changePwdSendOtpBtn) {
        if (!ensureSignedInOrRedirect()) return;
        changePwdSendOtpBtn.addEventListener('click', async () => {
            clearMsg('changePwdMsg');
            const token = currentToken();
            if (!token) {
                setMsg('changePwdMsg', 'Please sign in first.', 'warn');
                return;
            }

            const email = (document.getElementById('changePwdEmail')?.value || '').trim();
            if (!email) {
                setMsg('changePwdMsg', 'Could not determine your email from the session. Please sign out and sign in again.', 'warn');
                return;
            }

            setBusy(changePwdSendOtpBtn, true, 'Sending…');
            try {
                const res = await api.sendOtp(email);
                const body = await readTextOrJson(res);
                const text = typeof body === 'string' ? body : JSON.stringify(body);
                if (!res.ok) {
                    setMsg('changePwdMsg', `OTP request failed (${res.status}).\n${text || ''}`, 'bad');
                    return;
                }
                setMsg('changePwdMsg', text || 'OTP request sent.', 'good');
                startCooldown('change', 'changePwdSendOtpBtn');
                document.getElementById('changePwdOtp')?.focus();
            } catch (e) {
                setMsg('changePwdMsg', `Network error: ${e?.message || e}`, 'bad');
            } finally {
                if (cooldown.change <= 0) setBusy(changePwdSendOtpBtn, false);
            }
        });
    }

    const changePwdForm = document.getElementById('changePwdForm');
    if (changePwdForm) {
        if (!ensureSignedInOrRedirect()) return;
        changePwdForm.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            clearMsg('changePwdMsg');
            const token = currentToken();
            if (!token) {
                setMsg('changePwdMsg', 'Please sign in first.', 'warn');
                return;
            }

            const email = (document.getElementById('changePwdEmail')?.value || '').trim();
            const otp = (document.getElementById('changePwdOtp')?.value || '').trim();
            const newPassword = document.getElementById('changePwdNewPassword')?.value || '';
            if (!email || !otp || !newPassword) {
                setMsg('changePwdMsg', 'Fill OTP and new password.', 'warn');
                return;
            }

            const submitBtn = document.getElementById('changePwdSubmitBtn');
            setBusy(submitBtn, true, 'Updating…');
            try {
                const res = await api.forgotPassword(email, otp, newPassword);
                const body = await readTextOrJson(res);
                const text = typeof body === 'string' ? body : JSON.stringify(body);

                if (!res.ok) {
                    setMsg('changePwdMsg', `Update failed (${res.status}).\n${text || ''}`, 'bad');
                    return;
                }

                setMsg('changePwdMsg', text || 'Password updated.', 'good');
                document.getElementById('changePwdOtp').value = '';
                document.getElementById('changePwdNewPassword').value = '';
            } catch (e) {
                setMsg('changePwdMsg', `Network error: ${e?.message || e}`, 'bad');
            } finally {
                setBusy(submitBtn, false);
            }
        });
    }

    // Forgot send OTP
    const forgotSendOtpBtn = maybeEl('forgotSendOtpBtn');
    if (forgotSendOtpBtn) forgotSendOtpBtn.addEventListener('click', async () => {
        clearMsg('forgotMsg');
        const email = el('forgotEmail').value.trim();
        if (!email) {
            setMsg('forgotMsg', 'Enter an email first.', 'warn');
            return;
        }

        setBusy(forgotSendOtpBtn, true, 'Sending…');
        try {
            const res = await api.sendOtp(email);
            const body = await readTextOrJson(res);
            const text = typeof body === 'string' ? body : JSON.stringify(body);
            if (!res.ok) {
                setMsg('forgotMsg', `OTP request failed (${res.status}).\n${text || ''}`, 'bad');
                return;
            }
            setMsg('forgotMsg', text || 'OTP request sent.', 'good');
            startCooldown('forgot', 'forgotSendOtpBtn');
            el('forgotOtp').focus();
        } catch (e) {
            setMsg('forgotMsg', `Network error: ${e?.message || e}`, 'bad');
        } finally {
            if (cooldown.forgot <= 0) setBusy(forgotSendOtpBtn, false);
        }
    });

    // Forgot reset password
    const forgotForm = maybeEl('forgotForm');
    if (forgotForm) forgotForm.addEventListener('submit', async (ev) => {
        ev.preventDefault();
        clearMsg('forgotMsg');

        const email = el('forgotEmail').value.trim();
        const otp = el('forgotOtp').value.trim();
        const newPassword = el('forgotNewPassword').value;

        if (!email || !otp || !newPassword) {
            setMsg('forgotMsg', 'Fill email, OTP, and new password.', 'warn');
            return;
        }

        const submitBtn = forgotForm.querySelector('button[type="submit"]');
        setBusy(submitBtn, true, 'Resetting…');
        try {
            const res = await api.forgotPassword(email, otp, newPassword);
            const body = await readTextOrJson(res);
            const text = typeof body === 'string' ? body : JSON.stringify(body);

            if (!res.ok) {
                setMsg('forgotMsg', `Reset failed (${res.status}).\n${text || ''}`, 'bad');
                return;
            }

            setMsg('forgotMsg', text || 'Password reset.', 'good');
            // Friendly redirect: take user to Login and prefill email
            el('loginEmail').value = email;
            setRoute('tabLogin');
        } catch (e) {
            setMsg('forgotMsg', `Network error: ${e?.message || e}`, 'bad');
        } finally {
            setBusy(submitBtn, false);
        }
    });

    // Init
    setToken(localStorage.getItem('jwt'));
    if (tabSignup && tabLogin && tabForgot) {
        setRoute(hashToTabId(location.hash), { replace: true });
    }
    apiHealthCheck();

    // Hard gate: dashboard/account pages should never be usable when signed out
    if (document.getElementById('scrapList') || document.getElementById('changePwdForm')) {
        if (!ensureSignedInOrRedirect()) return;
    }

    if (document.getElementById('scrapList')) {
        loadScraps();
    }
})();
