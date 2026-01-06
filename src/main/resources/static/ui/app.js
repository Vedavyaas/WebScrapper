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
        }
    };

    const el = (id) => {
        const node = document.getElementById(id);
        if (!node) throw new Error(`Missing element: ${id}`);
        return node;
    };

    function setMsg(targetId, text, tone) {
        const node = el(targetId);
        node.hidden = false;
        node.className = `msg ${tone || ''}`.trim();
        node.textContent = text;
    }

    function clearMsg(targetId) {
        const node = el(targetId);
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
        el('sessionState').textContent = hasToken ? 'Signed in' : 'Signed out';
        el('signOutBtn').disabled = !hasToken;
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
            el('apiDot').className = 'dot good';
            el('apiText').textContent = 'Service: online';
        } catch {
            el('apiDot').className = 'dot warn';
            el('apiText').textContent = 'Service: unknown';
        }
    }

    function setBusy(button, isBusy, busyText) {
        if (!button) return;
        if (!button.dataset.origText) button.dataset.origText = button.textContent || '';
        button.disabled = isBusy;
        button.textContent = isBusy ? (busyText || 'Working…') : button.dataset.origText;
    }

    // Tabs + routing
    el('tabSignup').addEventListener('click', () => setRoute('tabSignup'));
    el('tabLogin').addEventListener('click', () => setRoute('tabLogin'));
    el('tabForgot').addEventListener('click', () => setRoute('tabForgot'));

    window.addEventListener('hashchange', () => {
        activateTab(hashToTabId(location.hash));
    });

    // OTP cooldown (client-side hint only)
    const cooldown = { signup: 0, forgot: 0 };
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
    const signupSendOtpBtn = el('signupSendOtpBtn');
    signupSendOtpBtn.addEventListener('click', async () => {
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
    const signupForm = el('signupForm');
    signupForm.addEventListener('submit', async (ev) => {
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
    const loginForm = el('loginForm');
    loginForm.addEventListener('submit', async (ev) => {
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
        } catch (e) {
            setMsg('loginMsg', `Network error: ${e?.message || e}`, 'bad');
        } finally {
            setBusy(submitBtn, false);
        }
    });

    el('signOutBtn').addEventListener('click', () => {
        clearMsg('loginMsg');
        setToken(null);
        setMsg('loginMsg', 'Signed out.', 'good');
        setRoute('tabLogin');
    });

    // Forgot send OTP
    const forgotSendOtpBtn = el('forgotSendOtpBtn');
    forgotSendOtpBtn.addEventListener('click', async () => {
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
    const forgotForm = el('forgotForm');
    forgotForm.addEventListener('submit', async (ev) => {
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
    setRoute(hashToTabId(location.hash), { replace: true });
    apiHealthCheck();
})();
