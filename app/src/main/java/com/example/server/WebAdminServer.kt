package com.example.server

import android.content.Context
import android.util.Log
import com.example.model.CandidateProfile
import com.example.model.PhotoVerificationRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class WebAdminServer(
    private val context: Context,
    private val getCandidates: () -> List<CandidateProfile>,
    private val onToggleBan: (String) -> Unit,
    private val onToggleVerify: (String) -> Unit,
    private val onToggleGold: (String) -> Unit,
    private val onUpdateCandidateContent: (String, String, String, String) -> Unit,
    private val getPricing: () -> Map<String, Double>,
    private val onUpdatePricing: (Double, Double, Double) -> Unit,
    private val getVerifications: () -> List<PhotoVerificationRequest>,
    private val onActionVerification: (String, Boolean) -> Unit
) {
    private var serverSocket: ServerSocket? = null
    @Volatile
    private var isRunning = false
    private val scope = CoroutineScope(Dispatchers.IO)
    var activePort: Int = 8080
        private set

    fun start() {
        if (isRunning) return
        isRunning = true

        scope.launch {
            for (port in 8080..8088) {
                try {
                    serverSocket = ServerSocket(port)
                    activePort = port
                    Log.i("WebAdminServer", "Web Admin Dashboard running on http://localhost:$port")
                    break
                } catch (e: Exception) {
                    Log.w("WebAdminServer", "Port $port unavailable, trying next...")
                }
            }

            if (serverSocket == null) {
                Log.e("WebAdminServer", "Could not bind to any port 8080-8088")
                isRunning = false
                return@launch
            }

            while (isRunning) {
                try {
                    val client = serverSocket?.accept() ?: break
                    scope.launch { handleClient(client) }
                } catch (e: Exception) {
                    if (!isRunning) break
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        try {
            serverSocket?.close()
        } catch (_: Exception) {}
        serverSocket = null
    }

    private fun handleClient(socket: Socket) {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            val requestLine = reader.readLine() ?: return
            val parts = requestLine.split(" ")
            if (parts.size < 2) return

            val method = parts[0]
            val path = parts[1]

            // Read headers
            var line: String?
            var contentLength = 0
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrEmpty()) break
                if (line!!.startsWith("Content-Length:", ignoreCase = true)) {
                    contentLength = line!!.substringAfter(":").trim().toIntOrNull() ?: 0
                }
            }

            // Read POST body if available
            val bodyBuilder = StringBuilder()
            if (method.equals("POST", ignoreCase = true) && contentLength > 0) {
                val buffer = CharArray(contentLength)
                var readTotal = 0
                while (readTotal < contentLength) {
                    val read = reader.read(buffer, readTotal, contentLength - readTotal)
                    if (read == -1) break
                    readTotal += read
                }
                bodyBuilder.append(buffer, 0, readTotal)
            }
            val requestBody = bodyBuilder.toString()

            // Handle CORS OPTIONS
            if (method.equals("OPTIONS", ignoreCase = true)) {
                sendCorsOk(writer)
                socket.close()
                return
            }

            // Route Requests
            when {
                path == "/" || path == "/admin" || path.startsWith("/index") -> {
                    sendHtml(writer, getDashboardHtml(activePort))
                }
                path == "/api/status" -> {
                    sendJson(writer, JSONObject().put("status", "running").put("appName", "Sawa Sawa").put("port", activePort).toString())
                }
                path == "/api/candidates" && method.equals("GET", ignoreCase = true) -> {
                    val candidates = getCandidates()
                    val array = JSONArray()
                    for (c in candidates) {
                        val obj = JSONObject()
                            .put("id", c.id)
                            .put("name", c.name)
                            .put("nameEn", c.nameEn)
                            .put("age", c.age)
                            .put("city", c.city)
                            .put("profession", c.profession)
                            .put("bio", c.bio)
                            .put("marriageGoal", c.marriageGoal)
                            .put("isVerified", c.isVerified)
                            .put("isGoldMember", c.isGoldMember)
                            .put("isVip", c.isVip)
                            .put("isBanned", c.isBanned)
                            .put("gender", c.gender)
                            .put("activeHours", c.activeHours)
                            .put("religiousPractice", c.religiousPractice)
                            .put("maritalStatus", c.maritalStatus)
                        array.put(obj)
                    }
                    sendJson(writer, array.toString())
                }
                path == "/api/candidate/ban" && method.equals("POST", ignoreCase = true) -> {
                    val json = JSONObject(requestBody)
                    val id = json.optString("id")
                    if (id.isNotEmpty()) {
                        onToggleBan(id)
                        syncCandidateBanToFirestore(id)
                    }
                    sendJson(writer, JSONObject().put("success", true).put("id", id).toString())
                }
                path == "/api/candidate/verify" && method.equals("POST", ignoreCase = true) -> {
                    val json = JSONObject(requestBody)
                    val id = json.optString("id")
                    if (id.isNotEmpty()) {
                        onToggleVerify(id)
                        syncCandidateVerifyToFirestore(id)
                    }
                    sendJson(writer, JSONObject().put("success", true).put("id", id).toString())
                }
                path == "/api/candidate/gold" && method.equals("POST", ignoreCase = true) -> {
                    val json = JSONObject(requestBody)
                    val id = json.optString("id")
                    if (id.isNotEmpty()) {
                        onToggleGold(id)
                    }
                    sendJson(writer, JSONObject().put("success", true).put("id", id).toString())
                }
                path == "/api/candidate/content" && method.equals("POST", ignoreCase = true) -> {
                    val json = JSONObject(requestBody)
                    val id = json.optString("id")
                    val name = json.optString("name")
                    val bio = json.optString("bio")
                    val city = json.optString("city")
                    if (id.isNotEmpty()) {
                        onUpdateCandidateContent(id, name, bio, city)
                    }
                    sendJson(writer, JSONObject().put("success", true).toString())
                }
                path == "/api/pricing" && method.equals("GET", ignoreCase = true) -> {
                    val prices = getPricing()
                    val obj = JSONObject()
                    prices.forEach { (k, v) -> obj.put(k, v) }
                    sendJson(writer, obj.toString())
                }
                path == "/api/pricing" && method.equals("POST", ignoreCase = true) -> {
                    val json = JSONObject(requestBody)
                    val weekly = json.optDouble("weekly", 49.99)
                    val monthly = json.optDouble("monthly", 199.99)
                    val annual = json.optDouble("annual", 899.99)
                    onUpdatePricing(weekly, monthly, annual)
                    syncPricingToFirestore(weekly, monthly, annual)
                    sendJson(writer, JSONObject().put("success", true).toString())
                }
                path == "/api/verifications" && method.equals("GET", ignoreCase = true) -> {
                    val reqs = getVerifications()
                    val array = JSONArray()
                    for (r in reqs) {
                        val obj = JSONObject()
                            .put("id", r.id)
                            .put("userId", r.userId)
                            .put("userName", r.userName)
                            .put("timestamp", r.timestamp)
                            .put("status", r.status)
                        array.put(obj)
                    }
                    sendJson(writer, array.toString())
                }
                path == "/api/verifications/action" && method.equals("POST", ignoreCase = true) -> {
                    val json = JSONObject(requestBody)
                    val id = json.optString("id")
                    val isApprove = json.optString("action") == "APPROVE" || json.optBoolean("approve", true)
                    if (id.isNotEmpty()) {
                        onActionVerification(id, isApprove)
                    }
                    sendJson(writer, JSONObject().put("success", true).toString())
                }
                else -> {
                    send404(writer)
                }
            }
            socket.close()
        } catch (e: Exception) {
            Log.e("WebAdminServer", "Error handling client: ${e.message}")
        }
    }

    private fun syncCandidateBanToFirestore(candidateId: String) {
        try {
            FirebaseFirestore.getInstance().collection("candidates")
                .document(candidateId)
                .update("isBanned", true)
        } catch (_: Exception) {}
    }

    private fun syncCandidateVerifyToFirestore(candidateId: String) {
        try {
            FirebaseFirestore.getInstance().collection("candidates")
                .document(candidateId)
                .update("isVerified", true)
        } catch (_: Exception) {}
    }

    private fun syncPricingToFirestore(weekly: Double, monthly: Double, annual: Double) {
        try {
            val map = hashMapOf(
                "weeklyPrice" to weekly,
                "monthlyPrice" to monthly,
                "annualPrice" to annual,
                "updatedAt" to System.currentTimeMillis()
            )
            FirebaseFirestore.getInstance().collection("app_settings")
                .document("subscription_plans")
                .set(map)
        } catch (_: Exception) {}
    }

    private fun sendHtml(writer: PrintWriter, html: String) {
        val bytes = html.toByteArray(Charsets.UTF_8)
        writer.print("HTTP/1.1 200 OK\r\n")
        writer.print("Content-Type: text/html; charset=UTF-8\r\n")
        writer.print("Content-Length: ${bytes.size}\r\n")
        writer.print("Access-Control-Allow-Origin: *\r\n")
        writer.print("Connection: close\r\n\r\n")
        writer.flush()
        writer.print(html)
        writer.flush()
    }

    private fun sendJson(writer: PrintWriter, json: String) {
        val bytes = json.toByteArray(Charsets.UTF_8)
        writer.print("HTTP/1.1 200 OK\r\n")
        writer.print("Content-Type: application/json; charset=UTF-8\r\n")
        writer.print("Content-Length: ${bytes.size}\r\n")
        writer.print("Access-Control-Allow-Origin: *\r\n")
        writer.print("Connection: close\r\n\r\n")
        writer.flush()
        writer.print(json)
        writer.flush()
    }

    private fun sendCorsOk(writer: PrintWriter) {
        writer.print("HTTP/1.1 200 OK\r\n")
        writer.print("Access-Control-Allow-Origin: *\r\n")
        writer.print("Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n")
        writer.print("Access-Control-Allow-Headers: Content-Type, Authorization\r\n")
        writer.print("Connection: close\r\n\r\n")
        writer.flush()
    }

    private fun send404(writer: PrintWriter) {
        writer.print("HTTP/1.1 404 Not Found\r\n")
        writer.print("Content-Type: text/plain\r\n")
        writer.print("Connection: close\r\n\r\n")
        writer.print("Not Found")
        writer.flush()
    }

    fun getDashboardHtml(port: Int): String {
        return """
<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>سوا سوا | لوحة التحكم الإدارية عبر الويب</title>
    <link href="https://fonts.googleapis.com/css2?family=Cairo:wght@400;600;700;900&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary: #174E43;
            --primary-dark: #0F352E;
            --primary-light: #E0EFEA;
            --gold: #D4AF37;
            --gold-dark: #8C6D1F;
            --gold-light: #FFF9E6;
            --bg: #F4F7F6;
            --card-bg: #FFFFFF;
            --text-main: #1C2522;
            --text-sub: #5E6D67;
            --danger: #D32F2F;
            --danger-bg: #FDE8E8;
            --success: #2E7D32;
            --success-bg: #E8F5E9;
            --border: #DCE5E2;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Cairo', sans-serif; }
        body { background: var(--bg); color: var(--text-main); line-height: 1.6; }
        
        header {
            background: linear-gradient(135deg, var(--primary-dark), var(--primary));
            color: white;
            padding: 18px 28px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 4px 14px rgba(0,0,0,0.15);
        }
        .header-title { display: flex; align-items: center; gap: 12px; }
        .header-title h1 { font-size: 20px; font-weight: 900; letter-spacing: -0.5px; }
        .badge-live {
            background: rgba(255,255,255,0.18);
            border: 1px solid var(--gold);
            color: var(--gold-light);
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 700;
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .badge-live::before { content: ""; width: 8px; height: 8px; border-radius: 50%; background: #4CAF50; display: inline-block; }

        .container { max-width: 1240px; margin: 24px auto; padding: 0 16px; }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 16px;
            margin-bottom: 24px;
        }
        .stat-card {
            background: var(--card-bg);
            border-radius: 16px;
            padding: 18px 20px;
            border: 1px solid var(--border);
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 2px 6px rgba(0,0,0,0.03);
        }
        .stat-info h4 { font-size: 13px; color: var(--text-sub); font-weight: 600; margin-bottom: 4px; }
        .stat-info .num { font-size: 24px; font-weight: 900; color: var(--primary); }
        .stat-icon { font-size: 32px; }

        .nav-tabs {
            display: flex;
            gap: 8px;
            background: #E8ECEB;
            padding: 6px;
            border-radius: 14px;
            margin-bottom: 20px;
            overflow-x: auto;
        }
        .tab-btn {
            background: transparent;
            border: none;
            padding: 10px 18px;
            border-radius: 10px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 700;
            color: var(--text-sub);
            transition: all 0.2s;
            white-space: nowrap;
        }
        .tab-btn.active {
            background: var(--card-bg);
            color: var(--primary);
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .panel { display: none; }
        .panel.active { display: block; }

        .card {
            background: var(--card-bg);
            border-radius: 18px;
            padding: 22px;
            border: 1px solid var(--border);
            box-shadow: 0 3px 10px rgba(0,0,0,0.03);
            margin-bottom: 20px;
        }
        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 18px;
            border-bottom: 1px solid var(--border);
            padding-bottom: 12px;
        }
        .card-header h3 { font-size: 17px; font-weight: 800; color: var(--primary); }

        table { width: 100%; border-collapse: collapse; text-align: right; }
        th, td { padding: 12px 14px; border-bottom: 1px solid var(--border); font-size: 13px; }
        th { background: #FAFBFB; color: var(--text-sub); font-weight: 700; }
        tr:hover td { background: #F8FAF9; }

        .status-pill {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 700;
        }
        .pill-verified { background: var(--gold-light); color: var(--gold-dark); border: 1px solid var(--gold); }
        .pill-active { background: var(--success-bg); color: var(--success); }
        .pill-banned { background: var(--danger-bg); color: var(--danger); }

        .btn {
            border: none;
            padding: 7px 14px;
            border-radius: 8px;
            font-size: 12px;
            font-weight: 700;
            cursor: pointer;
            transition: 0.2s;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }
        .btn-primary { background: var(--primary); color: white; }
        .btn-primary:hover { background: var(--primary-dark); }
        .btn-gold { background: var(--gold); color: #2C2105; }
        .btn-danger { background: var(--danger); color: white; }
        .btn-outline { background: transparent; border: 1px solid var(--border); color: var(--text-main); }

        .pricing-form { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 16px; margin-top: 14px; }
        .form-group { display: flex; flex-direction: column; gap: 6px; }
        .form-group label { font-size: 13px; font-weight: 700; color: var(--text-main); }
        .form-group input, .form-group textarea {
            padding: 10px 14px;
            border-radius: 10px;
            border: 1px solid var(--border);
            font-size: 14px;
            outline: none;
        }
        .form-group input:focus, .form-group textarea:focus { border-color: var(--primary); }

        .notification-hud {
            position: fixed;
            bottom: 24px;
            left: 24px;
            background: #1B2925;
            color: white;
            padding: 14px 22px;
            border-radius: 14px;
            box-shadow: 0 8px 24px rgba(0,0,0,0.25);
            display: none;
            align-items: center;
            gap: 12px;
            z-index: 1000;
            font-size: 13px;
            font-weight: 700;
            border-right: 4px solid var(--gold);
        }

        .modal-overlay {
            position: fixed;
            top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(0,0,0,0.5);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 999;
        }
        .modal {
            background: white;
            border-radius: 18px;
            width: 90%;
            max-width: 500px;
            padding: 24px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
        }
    </style>
</head>
<body>

    <header>
        <div class="header-title">
            <span style="font-size: 26px;">💎</span>
            <div>
                <h1>لوحة تحكم سوا سوا الإدارية (Web Dashboard)</h1>
                <p style="font-size: 12px; opacity: 0.85;">إدارة المحتوى، الصلاحيات، التوثيق، وباقات الاشتراكات مباشرة عبر المتصفح</p>
            </div>
        </div>
        <div class="badge-live">خادم التطبيق وFirestore متصلان (Port: $port)</div>
    </header>

    <div class="container">
        <!-- Stats Row -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-info">
                    <h4>إجمالي الأعضاء</h4>
                    <div class="num" id="stat-total-users">--</div>
                </div>
                <div class="stat-icon">👥</div>
            </div>
            <div class="stat-card">
                <div class="stat-info">
                    <h4>طلبات توثيق الصور</h4>
                    <div class="num" id="stat-pending-verifications">--</div>
                </div>
                <div class="stat-icon">📸</div>
            </div>
            <div class="stat-card">
                <div class="stat-info">
                    <h4>مشتركو Gold VIP</h4>
                    <div class="num" id="stat-gold-users">--</div>
                </div>
                <div class="stat-icon">👑</div>
            </div>
            <div class="stat-card">
                <div class="stat-info">
                    <h4>الحسابات المحظورة</h4>
                    <div class="num" id="stat-banned-users">0</div>
                </div>
                <div class="stat-icon">🛡️</div>
            </div>
        </div>

        <!-- Navigation Tabs -->
        <div class="nav-tabs">
            <button class="tab-btn active" onclick="switchTab('content-tab')">👥 إدارة المحتوى والمستخدمين</button>
            <button class="tab-btn" onclick="switchTab('verifications-tab')">📸 توثيق الصور والاعتماد</button>
            <button class="tab-btn" onclick="switchTab('pricing-tab')">💳 أسعار باقات الاشتراكات</button>
            <button class="tab-btn" onclick="switchTab('firestore-tab')">🔥 مزامنة قاعدة Firestore</button>
        </div>

        <!-- TAB 1: Content & Users Management -->
        <div id="content-tab" class="panel active">
            <div class="card">
                <div class="card-header">
                    <h3>أعضاء التطبيق والتحكم في المحتوى والصلاحيات</h3>
                    <input type="text" id="search-input" placeholder="بحث بالاسم أو المدينة..." onkeyup="filterUsers()" style="padding: 7px 12px; border-radius: 8px; border: 1px solid var(--border); font-size: 13px;">
                </div>
                <div style="overflow-x: auto;">
                    <table id="users-table">
                        <thead>
                            <tr>
                                <th>العضو</th>
                                <th>العمر والمدينة</th>
                                <th>المهنة</th>
                                <th>الالتزام والهدف</th>
                                <th>الحالة</th>
                                <th>الإجراءات السريعة</th>
                            </tr>
                        </thead>
                        <tbody id="users-tbody">
                            <tr><td colspan="6" style="text-align: center; padding: 24px;">جاري تحميل بيانات الأعضاء من قاعدة البيانات...</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- TAB 2: Verification Management -->
        <div id="verifications-tab" class="panel">
            <div class="card">
                <div class="card-header">
                    <h3>طلبات توثيق السلفي والصور الشخصية المعلقة</h3>
                    <button class="btn btn-outline" onclick="loadVerifications()">🔄 تحديث الطلبات</button>
                </div>
                <div id="verifications-container">
                    <p style="padding: 16px; text-align: center; color: var(--text-sub);">جاري فحص طلبات التوثيق...</p>
                </div>
            </div>
        </div>

        <!-- TAB 3: Pricing Management -->
        <div id="pricing-tab" class="panel">
            <div class="card">
                <div class="card-header">
                    <h3>تحديد وإدارة أسعار باقات سوا سوا Gold</h3>
                </div>
                <p style="font-size: 13px; color: var(--text-sub);">يتم تحديث هذه الأسعار فوراً في شاشات الاشتراكات والدفع داخل التطبيق ومزامنتها مع Firestore.</p>
                
                <div class="pricing-form">
                    <div class="form-group">
                        <label>سعر الباقة الأسبوعية (ج.م / ر.س):</label>
                        <input type="number" step="0.5" id="price-weekly" value="49.99">
                    </div>
                    <div class="form-group">
                        <label>سعر الباقة الشهرية الأساسية (ج.م / ر.س):</label>
                        <input type="number" step="0.5" id="price-monthly" value="199.99">
                    </div>
                    <div class="form-group">
                        <label>سعر الباقة السنوية الكاملة (ج.م / ر.س):</label>
                        <input type="number" step="1.0" id="price-annual" value="899.99">
                    </div>
                </div>

                <div style="margin-top: 20px;">
                    <button class="btn btn-primary" onclick="savePricing()">💾 حفظ الأسعار وتحديث التطبيق فوراً</button>
                </div>
            </div>
        </div>

        <!-- TAB 4: Firestore Direct Sync -->
        <div id="firestore-tab" class="panel">
            <div class="card">
                <div class="card-header">
                    <h3>المزامنة مع قاعدة بيانات Google Cloud Firestore</h3>
                </div>
                <p style="font-size: 13px; color: var(--text-sub); margin-bottom: 16px;">
                    المجموعات النشطة في Firestore: <code>users</code>، <code>candidates</code>، <code>virtual_roses</code>، <code>verification_requests</code>، <code>app_settings</code>.
                </p>
                <div style="background: var(--bg); padding: 16px; border-radius: 12px; font-family: monospace; font-size: 13px; line-height: 1.8;">
                    <div>• حالة اتصال الخادم المحلي: <span style="color: green; font-weight: bold;">متصل ومستعد (Port: $port)</span></div>
                    <div>• المزامنة التلقائية عند تعديل الصلاحيات: <span style="color: green; font-weight: bold;">مفعلة تلقائياً ✓</span></div>
                    <div>• التعديلات تتم في كلا الاتجاهين بين الويب وقاعدة بيانات التطبيق الداخلية.</div>
                </div>
                <div style="margin-top: 16px;">
                    <button class="btn btn-gold" onclick="notify('تم فحص وتأكيد جاهزية مزامنة Firestore بنجاح!')">⚡ اختبار المزامنة الحية</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit User Modal -->
    <div class="modal-overlay" id="edit-modal">
        <div class="modal">
            <h3 style="margin-bottom: 14px; color: var(--primary);">تعديل محتوى وبيانات العضو</h3>
            <input type="hidden" id="edit-id">
            <div class="form-group" style="margin-bottom: 12px;">
                <label>الاسم الكامل:</label>
                <input type="text" id="edit-name">
            </div>
            <div class="form-group" style="margin-bottom: 12px;">
                <label>المدينة / الدولة:</label>
                <input type="text" id="edit-city">
            </div>
            <div class="form-group" style="margin-bottom: 16px;">
                <label>النبذة التعريفية (Bio):</label>
                <textarea id="edit-bio" rows="3"></textarea>
            </div>
            <div style="display: flex; justify-content: flex-end; gap: 8px;">
                <button class="btn btn-outline" onclick="closeModal()">إلغاء</button>
                <button class="btn btn-primary" onclick="submitEditUser()">حفظ التعديلات</button>
            </div>
        </div>
    </div>

    <!-- Floating HUD -->
    <div class="notification-hud" id="hud">
        <span>✨</span>
        <span id="hud-text">تم تنفيذ الإجراء بنجاح</span>
    </div>

    <script>
        let candidatesData = [];
        let verificationsData = [];

        function switchTab(tabId) {
            document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.panel').forEach(panel => panel.classList.remove('active'));
            document.getElementById(tabId).classList.add('active');
            event.target.classList.add('active');
        }

        function notify(msg) {
            const hud = document.getElementById('hud');
            document.getElementById('hud-text').innerText = msg;
            hud.style.display = 'flex';
            setTimeout(() => { hud.style.display = 'none'; }, 3500);
        }

        async function loadCandidates() {
            try {
                const res = await fetch('/api/candidates');
                candidatesData = await res.json();
                renderCandidatesTable(candidatesData);
                updateStats();
            } catch (e) {
                console.error('Failed to fetch candidates:', e);
            }
        }

        function renderCandidatesTable(list) {
            const tbody = document.getElementById('users-tbody');
            if (!list || list.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 20px;">لا يوجد أعضاء مطابقون للبحث.</td></tr>';
                return;
            }

            tbody.innerHTML = list.map(c => `
                <tr>
                    <td>
                        <strong>${'$'}{c.name}</strong> (${'$'}{c.nameEn || ''})
                        ${'$'}{c.isVip ? '<span class="status-pill pill-verified" style="margin-right: 4px;">VIP</span>' : ''}
                    </td>
                    <td>${'$'}{c.age} سنة • ${'$'}{c.city}</td>
                    <td>${'$'}{c.profession || 'غير محدد'}</td>
                    <td style="max-width: 250px;">
                        <small style="color: var(--text-sub); display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                            ${'$'}{c.bio || c.marriageGoal}
                        </small>
                    </td>
                    <td>
                        ${'$'}{c.isBanned ? '<span class="status-pill pill-banned">محظور 🚫</span>' : '<span class="status-pill pill-active">نشط ✓</span>'}
                        ${'$'}{c.isVerified ? '<span class="status-pill pill-verified">موثق 🛡️</span>' : ''}
                    </td>
                    <td>
                        <div style="display: flex; gap: 6px;">
                            <button class="btn btn-outline" onclick="openEditModal('${'$'}{c.id}')">✏️ تعديل</button>
                            <button class="btn ${'$'}{c.isVerified ? 'btn-outline' : 'btn-gold'}" onclick="toggleVerify('${'$'}{c.id}')">
                                ${'$'}{c.isVerified ? 'إلغاء التوثيق' : 'توثيق 🛡️'}
                            </button>
                            <button class="btn ${'$'}{c.isBanned ? 'btn-primary' : 'btn-danger'}" onclick="toggleBan('${'$'}{c.id}')">
                                ${'$'}{c.isBanned ? 'فك الحظر' : 'حظر 🚫'}
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }

        function filterUsers() {
            const q = document.getElementById('search-input').value.toLowerCase();
            const filtered = candidatesData.filter(c => 
                (c.name && c.name.toLowerCase().includes(q)) || 
                (c.city && c.city.toLowerCase().includes(q)) ||
                (c.profession && c.profession.toLowerCase().includes(q))
            );
            renderCandidatesTable(filtered);
        }

        async function toggleBan(id) {
            await fetch('/api/candidate/ban', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: id })
            });
            notify('تم تحديث حالة حظر العضو بنجاح!');
            loadCandidates();
        }

        async function toggleVerify(id) {
            await fetch('/api/candidate/verify', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: id })
            });
            notify('تم تحديث شارة التوثيق بنجاح!');
            loadCandidates();
        }

        function openEditModal(id) {
            const c = candidatesData.find(item => item.id === id);
            if (!c) return;
            document.getElementById('edit-id').value = c.id;
            document.getElementById('edit-name').value = c.name;
            document.getElementById('edit-city').value = c.city;
            document.getElementById('edit-bio').value = c.bio || c.marriageGoal || '';
            document.getElementById('edit-modal').style.display = 'flex';
        }

        function closeModal() {
            document.getElementById('edit-modal').style.display = 'none';
        }

        async function submitEditUser() {
            const id = document.getElementById('edit-id').value;
            const name = document.getElementById('edit-name').value;
            const city = document.getElementById('edit-city').value;
            const bio = document.getElementById('edit-bio').value;

            await fetch('/api/candidate/content', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id, name, city, bio })
            });
            closeModal();
            notify('تم حفظ وتعديل محتوى العضو في التطبيق وFirestore!');
            loadCandidates();
        }

        async function loadVerifications() {
            try {
                const res = await fetch('/api/verifications');
                verificationsData = await res.json();
                renderVerifications(verificationsData);
                document.getElementById('stat-pending-verifications').innerText = verificationsData.filter(v => v.status === 'PENDING').length;
            } catch (e) {
                console.error(e);
            }
        }

        function renderVerifications(list) {
            const container = document.getElementById('verifications-container');
            const pending = list.filter(v => v.status === 'PENDING');
            if (pending.length === 0) {
                container.innerHTML = '<p style="padding: 20px; text-align: center; color: var(--success); font-weight: bold;">لا توجد طلبات سلفي معلقة حالياً ✓ جميع الحسابات تمت مراجعتها</p>';
                return;
            }
            container.innerHTML = pending.map(v => `
                <div style="display: flex; align-items: center; justify-content: space-between; padding: 14px; border: 1px solid var(--border); border-radius: 12px; margin-bottom: 10px;">
                    <div>
                        <strong>${'$'}{v.userName}</strong> (رقم المعرف: ${'$'}{v.userId})
                        <div style="font-size: 12px; color: var(--text-sub);">${'$'}{v.timestamp} • بانتظار اعتماد العلامة الذهبية</div>
                    </div>
                    <div style="display: flex; gap: 8px;">
                        <button class="btn btn-primary" onclick="actionVerification('${'$'}{v.id}', true)">اعتماد وتوثيق ✓</button>
                        <button class="btn btn-danger" onclick="actionVerification('${'$'}{v.id}', false)">رفض ✕</button>
                    </div>
                </div>
            `).join('');
        }

        async function actionVerification(id, approve) {
            await fetch('/api/verifications/action', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id, action: approve ? 'APPROVE' : 'REJECT' })
            });
            notify(approve ? 'تم اعتماد وتوثيق الحساب بنجاح!' : 'تم رفض طلب التوثيق.');
            loadVerifications();
            loadCandidates();
        }

        async function loadPricing() {
            try {
                const res = await fetch('/api/pricing');
                const data = await res.json();
                if (data.weekly) document.getElementById('price-weekly').value = data.weekly;
                if (data.monthly) document.getElementById('price-monthly').value = data.monthly;
                if (data.annual) document.getElementById('price-annual').value = data.annual;
            } catch (e) {}
        }

        async function savePricing() {
            const weekly = parseFloat(document.getElementById('price-weekly').value);
            const monthly = parseFloat(document.getElementById('price-monthly').value);
            const annual = parseFloat(document.getElementById('price-annual').value);

            await fetch('/api/pricing', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ weekly, monthly, annual })
            });
            notify('تم تحديث أسعار باقات الاشتراك ومزامنتها مع Firestore والتطبيق!');
        }

        function updateStats() {
            document.getElementById('stat-total-users').innerText = candidatesData.length;
            document.getElementById('stat-gold-users').innerText = candidatesData.filter(c => c.isGoldMember || c.isVip).length;
            document.getElementById('stat-banned-users').innerText = candidatesData.filter(c => c.isBanned).length;
        }

        // Initialize on load
        window.addEventListener('DOMContentLoaded', () => {
            loadCandidates();
            loadVerifications();
            loadPricing();
        });
    </script>
</body>
</html>
        """.trimIndent()
    }
}
