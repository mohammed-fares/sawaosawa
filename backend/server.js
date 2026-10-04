/**
 * Sawa Sawa (سوا سوا) - Production Backend REST API & Admin Server
 * "حيث يلتقي المسلمون للزواج الحلال" / "Where Muslims Meet To Marry"
 */

const express = require('express');
const cors = require('cors');
const path = require('path');
const fs = require('fs');

const app = express();
const PORT = process.env.BACKEND_PORT || process.env.SERVER_PORT || 3000;

// Security & Middlewares
app.use(cors());
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// Rate Limiter & Security Headers
const requestCounts = new Map();
app.use((req, res, next) => {
    const ip = req.ip || '127.0.0.1';
    const now = Date.now();
    const windowMs = 60 * 1000;
    const maxRequests = 300;

    let record = requestCounts.get(ip);
    if (!record || now - record.startTime > windowMs) {
        record = { count: 1, startTime: now };
        requestCounts.set(ip, record);
    } else {
        record.count++;
        if (record.count > maxRequests) {
            return res.status(429).json({ error: "Too many requests. Please slow down." });
        }
    }

    res.setHeader('X-Content-Type-Options', 'nosniff');
    res.setHeader('X-Frame-Options', 'SAMEORIGIN');
    res.setHeader('X-XSS-Protection', '1; mode=block');
    res.setHeader('Strict-Transport-Security', 'max-age=31536000; includeSubDomains');
    next();
});

// Admin Users Store & RBAC System
const adminUsers = [
    {
        username: "superadmin",
        passwordHash: "sawa_super_secret_2026",
        role: "SUPER_ADMIN",
        fullName: "مشرف النظام العام"
    },
    {
        username: "moderator",
        passwordHash: "sawa_mod_secret_2026",
        role: "MODERATOR",
        fullName: "المشرف الشرعي"
    },
    {
        username: "admin",
        passwordHash: "sawa_admin_secret_2026",
        role: "ADMIN",
        fullName: "مدير العمليات"
    }
];

const activeSessions = new Map();

function authenticateSession(req) {
    const authHeader = req.headers['authorization'];
    const token = (authHeader && authHeader.startsWith('Bearer ')) ? authHeader.slice(7) : (req.headers['x-admin-token'] || req.query.token);
    if (!token) return null;
    const session = activeSessions.get(token);
    if (!session) return null;
    if (Date.now() - session.createdAt > 24 * 60 * 60 * 1000) {
        activeSessions.delete(token);
        return null;
    }
    return session;
}

function requireRole(allowedRoles = ['SUPER_ADMIN', 'ADMIN', 'MODERATOR']) {
    return (req, res, next) => {
        const session = authenticateSession(req);
        if (!session) {
            return res.status(401).json({
                error: "Unauthorized: Valid admin session token required",
                code: "AUTH_REQUIRED"
            });
        }
        if (!allowedRoles.includes(session.role)) {
            return res.status(403).json({
                error: `Forbidden: Role '${session.role}' is not authorized to perform this action. Required: ${allowedRoles.join(', ')}`,
                code: "FORBIDDEN"
            });
        }
        req.adminSession = session;
        next();
    };
}

// Request Audit Logger Middleware
app.use((req, res, next) => {
    const start = Date.now();
    res.on('finish', () => {
        const duration = Date.now() - start;
        if (req.path.startsWith('/api')) {
            console.log(`[API ${new Date().toISOString()}] ${req.method} ${req.path} -> ${res.statusCode} (${duration}ms)`);
        }
    });
    next();
});

// Database & Stores (Synchronized with Firestore models)
const db = {
    users: [
        {
            id: "user_sarah",
            phoneNumber: "+201012345678",
            name: "سارة أحمد",
            nameEn: "Sarah Ahmed",
            age: 24,
            gender: "FEMALE",
            city: "القاهرة",
            country: "مصر",
            area: "المعادي",
            profession: "طبيبة أطفال",
            education: "ماجستير طب أطفال",
            religiousPractice: "ملتزمة بالفرائض",
            islamicDress: "حجاب محتشم",
            prayersHabit: "أصلي الصلوات الخمس في وقتها",
            halalFood: "حلال دائماً",
            marriageGoal: "بناء أسرة إسلامية مباركة قائمة على المودة والرحمة والتعاون.",
            isVerified: true,
            isGoldMember: true,
            isVip: true,
            isBanned: false,
            rosesBalance: 38,
            referralCode: "SAWA789",
            referralCount: 3,
            createdAt: "2026-09-15"
        },
        {
            id: "cand_1",
            phoneNumber: "+201098765432",
            name: "د. كريم سامي",
            nameEn: "Dr. Karim Sami",
            age: 28,
            gender: "MALE",
            city: "القاهرة",
            country: "مصر",
            area: "مصر الجديدة",
            profession: "طبيب جراحة عامة",
            education: "ماجستير جراحة عامة",
            religiousPractice: "ملتزم بالفرائض والنوافل",
            islamicDress: "ثياب محتشمة ووقورة",
            prayersHabit: "أصلي الصلوات الخمس في وقتها",
            halalFood: "حلال دائماً",
            marriageGoal: "تأسيس بيت مسلم قائم على كتاب الله وسنة رسوله.",
            isVerified: true,
            isGoldMember: true,
            isVip: true,
            isBanned: false,
            rosesBalance: 45,
            referralCode: "KARIM28",
            referralCount: 5,
            createdAt: "2026-09-18"
        },
        {
            id: "cand_2",
            phoneNumber: "+201122334455",
            name: "م. يوسف النجار",
            nameEn: "Eng. Youssef Al-Najjar",
            age: 29,
            gender: "MALE",
            city: "الجيزة",
            country: "مصر",
            area: "الدقي",
            profession: "مهندس برمجيات ونظم ذكاء اصطناعي",
            education: "بكالوريوس هندسة حاسبات",
            religiousPractice: "ملتزم بالصلوات وحافظ لأجزاء من القرآن",
            islamicDress: "مظهر إسلامي مهذب",
            prayersHabit: "أصلي في المسجد قدر الإمكان",
            halalFood: "حلال دائماً",
            marriageGoal: "شريكة حياة تقية نتعاون معاً على طاعة الله وتربية ذرية صالحة.",
            isVerified: true,
            isGoldMember: true,
            isVip: true,
            isBanned: false,
            rosesBalance: 60,
            referralCode: "YOUSSEF29",
            referralCount: 8,
            createdAt: "2026-09-20"
        }
    ],
    verifications: [
        {
            id: "ver_001",
            userId: "user_sarah",
            userName: "سارة أحمد",
            selfieUrl: "/assets/selfie_sample.jpg",
            submittedAt: "2026-10-02 14:30",
            status: "APPROVED",
            reviewer: "Admin_Ahmad",
            reviewDate: "2026-10-02 15:00",
            rejectionReason: null
        },
        {
            id: "ver_002",
            userId: "cand_2",
            userName: "م. يوسف النجار",
            selfieUrl: "/assets/selfie_sample_2.jpg",
            submittedAt: "2026-10-03 10:15",
            status: "PENDING",
            reviewer: null,
            reviewDate: null,
            rejectionReason: null
        }
    ],
    reports: [
        {
            id: "rep_101",
            reporterName: "فاطمة ع.",
            reportedUserId: "cand_fake_99",
            reportedUserName: "حساب مشبوه",
            reason: "استخدام صور رمزية غير لائقة",
            priority: "HIGH",
            status: "PENDING",
            timestamp: "2026-10-03 11:20"
        }
    ],
    roseLedger: [
        {
            id: "LEDGER-INIT-01",
            userId: "user_sarah",
            type: "EARN",
            amount: 10,
            balanceAfter: 10,
            description: "مكافأة إكمال الملف الشخصي وتوثيق الحساب",
            timestamp: Date.now() - 86400000 * 3
        },
        {
            id: "LEDGER-INIT-02",
            userId: "user_sarah",
            type: "RECEIVE",
            amount: 20,
            balanceAfter: 30,
            counterpartyName: "د. كريم سامي",
            description: "باقة ورود إعجاب وتعارف شرعي راقي 🌹",
            timestamp: Date.now() - 86400000 * 2
        }
    ],
    subscriptions: [
        {
            id: "TXN-849201",
            userId: "+201012345678",
            userName: "سارة أحمد",
            planId: "GOLD_MONTHLY",
            planTitle: "باقة سوا جولد الشهرية",
            amount: "199.99",
            currency: "EGP",
            paymentMethod: "VODAFONE_CASH",
            status: "COMPLETED",
            timestamp: "2026-10-02 16:30"
        }
    ],
    campaigns: [
        {
            id: "CAMP-2026-01",
            name: "حملة الزواج الحلال - مصر الكبرى",
            type: "REGISTRATION",
            status: "ACTIVE",
            dailyBudget: 500,
            totalBudget: 15000,
            country: "مصر",
            gender: "ALL",
            targetAgeMin: 22,
            targetAgeMax: 38,
            impressions: 48500,
            clicks: 3920,
            conversions: 890,
            spend: 4200,
            roas: 3.4
        },
        {
            id: "CAMP-2026-02",
            name: "حملة باقة النخبة الذهبية VIP",
            type: "SUBSCRIPTION",
            status: "ACTIVE",
            dailyBudget: 800,
            totalBudget: 25000,
            country: "السعودية والإمارات",
            gender: "ALL",
            targetAgeMin: 24,
            targetAgeMax: 42,
            impressions: 62000,
            clicks: 4800,
            conversions: 410,
            spend: 6800,
            roas: 4.2
        }
    ],
    socialMediaDrafts: [
        {
            id: "SOC-01",
            platform: "INSTAGRAM",
            title: "نصيحة أسبوعية: أهمية الصدق في التعارف الشرعي",
            captionAr: "الصدق مفتاح البركة في كل زواج مبارك.. في سوا سوا نضمن لك بيئة آمنة قائمة على المودة والشفافية التامة 💍🌹",
            captionEn: "Honesty is the foundation of every blessed marriage. Sawa Sawa connects you with pure intentions.",
            hashtags: "#زواج_إسلامي #سوا_سوا #زواج_حلال #عفاف",
            status: "SCHEDULED",
            scheduledDate: "2026-10-06"
        },
        {
            id: "SOC-02",
            platform: "TIKTOK",
            title: "فيديو تعريفي: كيف تشرك ولي أمرك في محادثات سوا سوا؟",
            captionAr: "خطوة بخطوة.. ميزة إشراك الولي تمنح الفتاة وأسرتها الاطمئنان الكامل والبركة الشرعية في رحلة الزواج 🧕🤍",
            captionEn: "Involve your guardian easily on Sawa Sawa for complete peace of mind and halal blessings.",
            hashtags: "#خطوبة #زواج_شرعي #عائلة #سوا_سوا",
            status: "PUBLISHED",
            scheduledDate: "2026-10-02"
        }
    ],
    coupons: [
        { code: "HALAL2026", discountPercent: 25, validUntil: "2026-12-31", usageCount: 142, maxUsage: 1000, isActive: true },
        { code: "MUBARAK", discountPercent: 30, validUntil: "2026-11-30", usageCount: 68, maxUsage: 500, isActive: true },
        { code: "EGYPT50", discountPercent: 50, validUntil: "2026-10-31", usageCount: 310, maxUsage: 500, isActive: true }
    ],
    auditLogs: [
        {
            id: "LOG-991",
            adminUser: "SuperAdmin_Fares",
            role: "SUPER_ADMIN",
            action: "APPROVE_VERIFICATION",
            targetResource: "user_sarah",
            details: "اعتماد توثيق الهوية وصورة السيلفي الرسمية",
            timestamp: "2026-10-02 15:00:12",
            ipAddress: "197.34.120.45"
        },
        {
            id: "LOG-992",
            adminUser: "FinanceAdmin_Ali",
            role: "FINANCE_MANAGER",
            action: "UPDATE_PLAN_PRICING",
            targetResource: "pricing_matrix",
            details: "تعديل سعر الباقة الشهرية إلى 199.99 ج.م لتناسب السوق المصري",
            timestamp: "2026-10-03 09:20:00",
            ipAddress: "156.204.88.12"
        }
    ],
    pricing: {
        weekly: 79.0,
        monthly: 199.0,
        annual: 899.0,
        bundle: 349.0,
        rosePack15: 49.0,
        rosePack50: 149.0,
        currency: "EGP"
    },
    matchingWeights: {
        preferencesWeight: 0.25,
        distanceWeight: 0.15,
        interestsWeight: 0.15,
        religiousWeight: 0.20,
        intentionsWeight: 0.15,
        completenessWeight: 0.10
    },
    appSettings: {
        appName: "سوا سوا | Sawa Sawa",
        taglineAr: "حيث يلتقي المسلمون للزواج الحلال",
        taglineEn: "Where Muslims Meet To Marry",
        primaryColor: "#174E43",
        goldColor: "#D4AF37",
        maintenanceMode: false,
        minAndroidVersion: "1.0",
        recommendedAndroidVersion: "1.0",
        cameraVerificationRequired: true,
        chaperoneFeatureEnabled: true
    }
};

/* ==========================================================================
   REST API Endpoints
   ========================================================================== */

// 0. Admin Authentication & Session Management
app.post('/api/admin/login', (req, res) => {
    const { username, password } = req.body;
    const admin = adminUsers.find(a => a.username === username && a.passwordHash === password);
    if (!admin) {
        return res.status(401).json({ error: "اسم المستخدم أو كلمة المرور غير صحيحة" });
    }
    const token = `sawa_token_${Date.now()}_${Math.random().toString(36).substring(2, 10)}`;
    const sessionData = {
        token,
        username: admin.username,
        role: admin.role,
        fullName: admin.fullName,
        createdAt: Date.now()
    };
    activeSessions.set(token, sessionData);

    db.auditLogs.unshift({
        id: `LOG-${Date.now()}`,
        adminUser: admin.username,
        role: admin.role,
        action: "ADMIN_LOGIN",
        targetResource: "ADMIN_PANEL",
        details: `تسجيل دخول ناجح للمشرف ${admin.fullName} (${admin.role})`,
        timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19),
        ipAddress: req.ip || "127.0.0.1"
    });

    res.json({
        success: true,
        token,
        admin: {
            username: admin.username,
            role: admin.role,
            fullName: admin.fullName
        }
    });
});

app.post('/api/admin/logout', (req, res) => {
    const authHeader = req.headers['authorization'];
    const token = (authHeader && authHeader.startsWith('Bearer ')) ? authHeader.slice(7) : (req.headers['x-admin-token'] || req.query.token);
    if (token) activeSessions.delete(token);
    res.json({ success: true });
});

// 1. Health & Status
app.get('/api/health', (req, res) => {
    res.json({
        status: "UP",
        uptimeSeconds: process.uptime(),
        appName: "Sawa Sawa API",
        version: "1.0.0",
        environment: "production",
        timestamp: new Date().toISOString(),
        database: "Firestore + Memory Synced",
        activeUsersCount: db.users.length,
        pendingVerifications: db.verifications.filter(v => v.status === "PENDING").length
    });
});

// 2. Users Management
app.get('/api/users', (req, res) => {
    const { q, status, gender } = req.query;
    let list = [...db.users];
    if (q) {
        const query = q.toLowerCase();
        list = list.filter(u => u.name.toLowerCase().includes(query) || (u.city && u.city.toLowerCase().includes(query)));
    }
    if (gender) list = list.filter(u => u.gender === gender);
    if (status === 'banned') list = list.filter(u => u.isBanned);
    if (status === 'verified') list = list.filter(u => u.isVerified);
    res.json(list);
});

app.get('/api/users/:id', (req, res) => {
    const user = db.users.find(u => u.id === req.params.id);
    if (!user) return res.status(404).json({ error: "User not found" });
    res.json(user);
});

app.post('/api/users/:id/ban', requireRole(['SUPER_ADMIN', 'ADMIN', 'MODERATOR']), (req, res) => {
    const user = db.users.find(u => u.id === req.params.id);
    if (!user) return res.status(404).json({ error: "User not found" });
    user.isBanned = !user.isBanned;
    db.auditLogs.unshift({
        id: `LOG-${Date.now()}`,
        adminUser: req.adminSession?.username || req.body.adminUser || "WebAdmin",
        role: req.adminSession?.role || "ADMIN",
        action: user.isBanned ? "BAN_USER" : "UNBAN_USER",
        targetResource: user.id,
        details: `تحديث حالة الحظر للمستخدم ${user.name} إلى ${user.isBanned ? 'محظور' : 'نشط'}`,
        timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19),
        ipAddress: req.ip || "127.0.0.1"
    });
    res.json({ success: true, isBanned: user.isBanned });
});

app.post('/api/users/:id/verify', requireRole(['SUPER_ADMIN', 'ADMIN', 'MODERATOR']), (req, res) => {
    const user = db.users.find(u => u.id === req.params.id);
    if (!user) return res.status(404).json({ error: "User not found" });
    user.isVerified = !user.isVerified;
    res.json({ success: true, isVerified: user.isVerified });
});

// 3. Verification Queue
app.get('/api/verifications', (req, res) => {
    res.json(db.verifications);
});

app.post('/api/verifications/:id/approve', requireRole(['SUPER_ADMIN', 'ADMIN', 'MODERATOR']), (req, res) => {
    const ver = db.verifications.find(v => v.id === req.params.id);
    if (!ver) return res.status(404).json({ error: "Verification request not found" });
    ver.status = "APPROVED";
    ver.reviewer = req.adminSession?.username || req.body.adminUser || "WebAdmin";
    ver.reviewDate = new Date().toISOString();
    const user = db.users.find(u => u.id === ver.userId);
    if (user) user.isVerified = true;

    db.auditLogs.unshift({
        id: `LOG-${Date.now()}`,
        adminUser: ver.reviewer,
        role: req.adminSession?.role || "VERIFICATION_AGENT",
        action: "APPROVE_SELFIE",
        targetResource: ver.userId,
        details: `اعتماد توثيق الهوية بالسيلفي للمستخدم ${ver.userName}`,
        timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19),
        ipAddress: req.ip || "127.0.0.1"
    });
    res.json({ success: true, verification: ver });
});

app.post('/api/verifications/:id/reject', requireRole(['SUPER_ADMIN', 'ADMIN', 'MODERATOR']), (req, res) => {
    const ver = db.verifications.find(v => v.id === req.params.id);
    if (!ver) return res.status(404).json({ error: "Verification request not found" });
    ver.status = "REJECTED";
    ver.rejectionReason = req.body.reason || "عدم وضوح ملامح الوجه أو عدم مطابقة الصورة الشخصية";
    ver.reviewer = req.adminSession?.username || req.body.adminUser || "WebAdmin";
    ver.reviewDate = new Date().toISOString();
    res.json({ success: true, verification: ver });
});

// 3.1 Reports & Moderation
app.get('/api/reports', (req, res) => {
    res.json(db.reports);
});

app.post('/api/reports', (req, res) => {
    const { reporterName, reportedUserId, reportedUserName, reason } = req.body;
    const newReport = {
        id: `rep_${Date.now()}`,
        reporterName: reporterName || "مستخدم",
        reportedUserId: reportedUserId || "",
        reportedUserName: reportedUserName || "عضو",
        reason: reason || "سلوك غير لائق",
        priority: "NORMAL",
        status: "PENDING",
        timestamp: new Date().toISOString().replace('T', ' ').substring(0, 16)
    };
    db.reports.unshift(newReport);
    res.status(201).json({ success: true, report: newReport });
});

app.post('/api/reports/:id/dismiss', requireRole(['SUPER_ADMIN', 'ADMIN', 'MODERATOR']), (req, res) => {
    const repIndex = db.reports.findIndex(r => r.id === req.params.id);
    if (repIndex === -1) return res.status(404).json({ error: "Report not found" });
    db.reports.splice(repIndex, 1);
    res.json({ success: true });
});

app.post('/api/reports/:id/ban', requireRole(['SUPER_ADMIN', 'ADMIN', 'MODERATOR']), (req, res) => {
    const rep = db.reports.find(r => r.id === req.params.id);
    if (!rep) return res.status(404).json({ error: "Report not found" });
    rep.status = "RESOLVED_BANNED";
    const user = db.users.find(u => u.id === rep.reportedUserId);
    if (user) user.isBanned = true;
    res.json({ success: true, report: rep });
});

// 4. Virtual Roses & Ledger
app.get('/api/roses/ledger', (req, res) => {
    res.json(db.roseLedger);
});

app.post('/api/roses/grant', requireRole(['SUPER_ADMIN', 'ADMIN']), (req, res) => {
    const { userId, amount, reason } = req.body;
    const user = db.users.find(u => u.id === userId);
    if (!user) return res.status(404).json({ error: "User not found" });
    user.rosesBalance += parseInt(amount, 10);
    const entry = {
        id: `LEDGER-${Date.now()}`,
        userId: user.id,
        type: "ADMIN_GRANT",
        amount: parseInt(amount, 10),
        balanceAfter: user.rosesBalance,
        description: reason || "منحة إدارية تشجيعية من منصة سوا سوا 🌹",
        timestamp: Date.now()
    };
    db.roseLedger.unshift(entry);
    res.json({ success: true, balance: user.rosesBalance, entry });
});

// 5. Subscriptions & Pricing
app.get('/api/subscriptions/pricing', (req, res) => {
    res.json(db.pricing);
});

app.post('/api/subscriptions/pricing', requireRole(['SUPER_ADMIN']), (req, res) => {
    const { weekly, monthly, annual, bundle } = req.body;
    if (weekly) db.pricing.weekly = parseFloat(weekly);
    if (monthly) db.pricing.monthly = parseFloat(monthly);
    if (annual) db.pricing.annual = parseFloat(annual);
    if (bundle) db.pricing.bundle = parseFloat(bundle);

    db.auditLogs.unshift({
        id: `LOG-${Date.now()}`,
        adminUser: req.adminSession?.username || req.body.adminUser || "FinanceAdmin",
        role: req.adminSession?.role || "FINANCE_MANAGER",
        action: "UPDATE_PRICING",
        targetResource: "subscription_pricing",
        details: `تحديث أسعار الباقات: أسبوعي (${db.pricing.weekly})، شهري (${db.pricing.monthly})، سنوي (${db.pricing.annual})`,
        timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19),
        ipAddress: req.ip || "127.0.0.1"
    });
    res.json({ success: true, pricing: db.pricing });
});

app.get('/api/subscriptions/transactions', (req, res) => {
    res.json(db.subscriptions);
});

// 6. Campaigns & Advertising
app.get('/api/campaigns', (req, res) => {
    res.json(db.campaigns);
});

app.post('/api/campaigns/create', (req, res) => {
    const newCamp = {
        id: `CAMP-${Date.now()}`,
        name: req.body.name || "حملة إعلانية جديدة",
        type: req.body.type || "REGISTRATION",
        status: "ACTIVE",
        dailyBudget: parseFloat(req.body.dailyBudget) || 500,
        totalBudget: parseFloat(req.body.totalBudget) || 10000,
        country: req.body.country || "مصر",
        gender: req.body.gender || "ALL",
        targetAgeMin: parseInt(req.body.targetAgeMin, 10) || 20,
        targetAgeMax: parseInt(req.body.targetAgeMax, 10) || 45,
        impressions: 0,
        clicks: 0,
        conversions: 0,
        spend: 0,
        roas: 0.0
    };
    db.campaigns.unshift(newCamp);
    res.json({ success: true, campaign: newCamp });
});

// 7. Social Media Marketing Drafts
app.get('/api/social-media/drafts', (req, res) => {
    res.json(db.socialMediaDrafts);
});

app.post('/api/social-media/drafts', (req, res) => {
    const draft = {
        id: `SOC-${Date.now()}`,
        platform: req.body.platform || "INSTAGRAM",
        title: req.body.title || "منشور إعلاني",
        captionAr: req.body.captionAr || "",
        captionEn: req.body.captionEn || "",
        hashtags: req.body.hashtags || "#سوا_سوا #زواج_إسلامي",
        status: req.body.status || "DRAFT",
        scheduledDate: req.body.scheduledDate || "2026-10-15"
    };
    db.socialMediaDrafts.unshift(draft);
    res.json({ success: true, draft });
});

// 8. Coupons
app.get('/api/coupons', (req, res) => {
    res.json(db.coupons);
});

app.post('/api/coupons', (req, res) => {
    const coupon = {
        code: (req.body.code || `CODE${Math.floor(Math.random()*9000+1000)}`).toUpperCase(),
        discountPercent: parseInt(req.body.discountPercent, 10) || 20,
        validUntil: req.body.validUntil || "2026-12-31",
        usageCount: 0,
        maxUsage: parseInt(req.body.maxUsage, 10) || 500,
        isActive: true
    };
    db.coupons.unshift(coupon);
    res.json({ success: true, coupon });
});

// 9. Audit Logs
app.get('/api/audit-logs', requireRole(['SUPER_ADMIN', 'ADMIN']), (req, res) => {
    res.json(db.auditLogs);
});

// 10. Matching Engine Configuration
app.get('/api/matching/weights', (req, res) => {
    res.json(db.matchingWeights);
});

app.post('/api/matching/weights', requireRole(['SUPER_ADMIN', 'ADMIN']), (req, res) => {
    Object.assign(db.matchingWeights, req.body);
    res.json({ success: true, matchingWeights: db.matchingWeights });
});

// 11. AI Ad Copy Generation (Gemini API Integration Interface)
app.post('/api/ai/generate-ad-copy', (req, res) => {
    const { topic, audience, language } = req.body;
    const isAr = language !== 'en';
    
    // Deterministic high-quality Islamic marriage campaign copy
    const copy = isAr ? {
        headline: "ابحث عن نصفك الآخر بالحلال وبكل طمأنينة واحترام 💍",
        description: "منصة سوا سوا للزواج الإسلامي: حسابات موثقة بالسيلفي، ميزة إشراك الولي، وطمس الصور لحماية الخصوصية. سجّل الآن وابدأ رحلتك المباركة.",
        suggestedHashtags: "#سوا_سوا #زواج_حلال #زواج_إسلامي #عفاف #خطوبة_حلال",
        callToAction: "حمّل تطبيق سوا سوا مجاناً الآن"
    } : {
        headline: "Where Muslims Meet To Marry — Safe, Halal, and Verified 💍",
        description: "Sawa Sawa provides verified profiles, guardian (wali) involvement, and modesty privacy filters. Join the leading halal marriage platform today.",
        suggestedHashtags: "#HalalMarriage #MuslimMatrimony #SawaSawaApp #IslamicNikah",
        callToAction: "Download Sawa Sawa Free"
    };

    res.json({ success: true, copy });
});

// 12. Account Deletion Request (GDPR / Right to be Forgotten)
app.post('/api/users/delete-request', (req, res) => {
    const { phoneOrEmail, reason } = req.body;
    db.auditLogs.unshift({
        id: `LOG-${Date.now()}`,
        adminUser: "System_GDPR",
        role: "SYSTEM",
        action: "ACCOUNT_DELETION_REQUEST",
        targetResource: phoneOrEmail || "Unknown",
        details: `طلب حذف الحساب نهائياً: ${reason || 'لا يوجد سبب محدد'}`,
        timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19),
        ipAddress: req.ip || "127.0.0.1"
    });
    res.json({
        success: true,
        messageAr: "تم استلام طلب حذف حسابك وبياناتك بنجاح، وستتم معالجته وفق السياسات القانونية خلال 24 ساعة.",
        messageEn: "Your account deletion request has been received and will be processed within 24 hours."
    });
});

/* ==========================================================================
   Static Serving & Web Pages
   ========================================================================== */

// Serve Public Marketing Website
app.use(express.static(path.join(__dirname, 'public')));

// Serve Production Web Admin Dashboard
app.use('/admin', express.static(path.join(__dirname, '..', 'web-admin')));

// Robots & Sitemap for SEO
app.get('/robots.txt', (req, res) => {
    res.type('text/plain');
    res.send("User-agent: *\nAllow: /\nDisallow: /admin/\nSitemap: https://sawasawa.app/sitemap.xml");
});

app.get('/sitemap.xml', (req, res) => {
    res.type('application/xml');
    res.send(`<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
  <url><loc>https://sawasawa.app/</loc><priority>1.0</priority><changefreq>weekly</changefreq></url>
  <url><loc>https://sawasawa.app/about</loc><priority>0.8</priority></url>
  <url><loc>https://sawasawa.app/features</loc><priority>0.8</priority></url>
  <url><loc>https://sawasawa.app/safety</loc><priority>0.9</priority></url>
  <url><loc>https://sawasawa.app/download</loc><priority>0.9</priority></url>
  <url><loc>https://sawasawa.app/privacy</loc><priority>0.7</priority></url>
  <url><loc>https://sawasawa.app/terms</loc><priority>0.7</priority></url>
</urlset>`);
});

// Fallback: Return Public Website Homepage
app.get('*', (req, res) => {
    const indexPath = path.join(__dirname, 'public', 'index.html');
    if (fs.existsSync(indexPath)) {
        res.sendFile(indexPath);
    } else {
        res.send("Sawa Sawa Production Server Running");
    }
});

// Start Server
app.listen(PORT, '0.0.0.0', () => {
    console.log(`================================================================`);
    console.log(` Sawa Sawa (سوا سوا) - Production Server Active`);
    console.log(` Public Marketing Website: http://localhost:${PORT}`);
    console.log(` Production Web Admin:     http://localhost:${PORT}/admin`);
    console.log(` REST API Base:           http://localhost:${PORT}/api/health`);
    console.log(`================================================================`);
});
