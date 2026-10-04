const http = require('http');

process.env.BACKEND_PORT = '3001';
const app = require('./server.js');

setTimeout(async () => {
  console.log('\n=== RUNNING SAWA SAWA PRODUCTION BACKEND TEST SUITE ===\n');
  let failures = 0;

  async function test(name, fn) {
    try {
      await fn();
      console.log(`✓ PASS: ${name}`);
    } catch (e) {
      console.error(`✗ FAIL: ${name} ->`, e.message);
      failures++;
    }
  }

  const BASE = 'http://localhost:3001';

  // Test 1: Health Check
  await test('GET /api/health returns status UP', async () => {
    const res = await fetch(`${BASE}/api/health`);
    const data = await res.json();
    if (res.status !== 200 || data.status !== 'UP') throw new Error(`Status ${res.status}`);
  });

  // Test 2: Anonymous call to protected endpoint must return 401
  await test('GET /api/audit-logs without token returns 401 Unauthorized', async () => {
    const res = await fetch(`${BASE}/api/audit-logs`);
    if (res.status !== 401) throw new Error(`Expected 401, got ${res.status}`);
  });

  // Test 3: Anonymous ban call must return 401
  await test('POST /api/users/cand_1/ban without token returns 401', async () => {
    const res = await fetch(`${BASE}/api/users/cand_1/ban`, { method: 'POST' });
    if (res.status !== 401) throw new Error(`Expected 401, got ${res.status}`);
  });

  // Test 4: Invalid login credentials
  await test('POST /api/admin/login with invalid password returns 401', async () => {
    const res = await fetch(`${BASE}/api/admin/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: 'superadmin', password: 'wrong_password' })
    });
    if (res.status !== 401) throw new Error(`Expected 401, got ${res.status}`);
  });

  // Test 5: Moderator login
  let modToken = '';
  await test('POST /api/admin/login with moderator credentials returns token', async () => {
    const res = await fetch(`${BASE}/api/admin/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: 'moderator', password: 'sawa_mod_secret_2026' })
    });
    const data = await res.json();
    if (res.status !== 200 || !data.token) throw new Error(`Failed to login as moderator`);
    modToken = data.token;
  });

  // Test 6: Moderator RBAC check (moderator cannot update pricing)
  await test('POST /api/subscriptions/pricing by moderator returns 403 Forbidden', async () => {
    const res = await fetch(`${BASE}/api/subscriptions/pricing`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${modToken}`
      },
      body: JSON.stringify({ weekly: 99.0 })
    });
    if (res.status !== 403) throw new Error(`Expected 403, got ${res.status}`);
  });

  // Test 7: SuperAdmin login & pricing update
  let adminToken = '';
  await test('POST /api/admin/login as superadmin succeeds', async () => {
    const res = await fetch(`${BASE}/api/admin/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: 'superadmin', password: 'sawa_super_secret_2026' })
    });
    const data = await res.json();
    if (res.status !== 200 || !data.token) throw new Error(`Failed to login as superadmin`);
    adminToken = data.token;
  });

  await test('POST /api/subscriptions/pricing by superadmin returns 200', async () => {
    const res = await fetch(`${BASE}/api/subscriptions/pricing`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${adminToken}`
      },
      body: JSON.stringify({ weekly: 79.0, monthly: 199.0 })
    });
    if (res.status !== 200) throw new Error(`Expected 200, got ${res.status}`);
  });

  // Test 8: Public Marketing Website returns 200
  await test('GET / returns 200 OK', async () => {
    const res = await fetch(`${BASE}/`);
    if (res.status !== 200) throw new Error(`Expected 200, got ${res.status}`);
  });

  // Test 9: Web Admin Dashboard returns 200
  await test('GET /admin/ returns 200 OK', async () => {
    const res = await fetch(`${BASE}/admin/`);
    if (res.status !== 200) throw new Error(`Expected 200, got ${res.status}`);
  });

  console.log(`\n======================================================`);
  if (failures === 0) {
    console.log(`ALL BACKEND TESTS PASSED (9/9)`);
    process.exit(0);
  } else {
    console.error(`TESTS FAILED: ${failures} errors`);
    process.exit(1);
  }
}, 500);
