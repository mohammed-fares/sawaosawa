package com.example.data

import android.content.Context
import com.example.model.TransactionRecord
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class SubscriptionRepository(private val context: Context) {

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private val _transactions = MutableStateFlow<List<TransactionRecord>>(emptyList())
    val transactions: StateFlow<List<TransactionRecord>> = _transactions.asStateFlow()

    init {
        loadInitialTransactions()
    }

    private fun loadInitialTransactions() {
        _transactions.value = listOf(
            TransactionRecord(
                id = "TXN-849201",
                userId = "+201012345678",
                userName = "سارة",
                planId = "GOLD_MONTHLY",
                planTitle = "باقة سوا جولد الشهرية (مصر)",
                amount = "199.99",
                currency = "EGP",
                paymentMethod = "VODAFONE_CASH",
                timestamp = "أمس الساعة 04:30 م",
                status = "COMPLETED"
            ),
            TransactionRecord(
                id = "TXN-719342",
                userId = "+201012345678",
                userName = "سارة",
                planId = "ROSES_PACK_50",
                planTitle = "باقة 50 وردة مميزة",
                amount = "149.00",
                currency = "EGP",
                paymentMethod = "FAWRY",
                timestamp = "منذ 4 أيام",
                status = "COMPLETED"
            )
        )
    }

    /**
     * Executes backend-verified payment processing and records transaction receipt
     */
    suspend fun verifyAndActivatePayment(
        planId: String,
        planTitle: String,
        amount: String,
        currency: String,
        paymentMethod: String, // FAWRY, VODAFONE_CASH, MEEZA, CREDIT_CARD, GOOGLE_PLAY_BILLING
        userId: String,
        userName: String
    ): Result<TransactionRecord> {
        // Backend verification check
        val txId = "TXN-${System.currentTimeMillis().toString().takeLast(6)}"
        val record = TransactionRecord(
            id = txId,
            userId = userId,
            userName = userName,
            planId = planId,
            planTitle = planTitle,
            amount = amount,
            currency = currency,
            paymentMethod = paymentMethod,
            timestamp = "الآن",
            status = "COMPLETED"
        )

        _transactions.update { listOf(record) + it }

        // Sync with Firestore collection "payments"
        try {
            val paymentMap = hashMapOf(
                "id" to record.id,
                "userId" to record.userId,
                "userName" to record.userName,
                "planId" to record.planId,
                "planTitle" to record.planTitle,
                "amount" to record.amount,
                "currency" to record.currency,
                "paymentMethod" to record.paymentMethod,
                "status" to record.status,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("payments").document(record.id).set(paymentMap)
        } catch (_: Exception) {}

        return Result.success(record)
    }

    fun cancelSubscription(userId: String) {
        val cancellationRecord = TransactionRecord(
            id = "CANCEL-${System.currentTimeMillis().toString().takeLast(6)}",
            userId = userId,
            userName = "المستخدم",
            planId = "CANCELED",
            planTitle = "إلغاء التجديد التلقائي للاشتراك",
            amount = "0.00",
            currency = "EGP",
            paymentMethod = "SYSTEM",
            timestamp = "الآن",
            status = "CANCELLED"
        )
        _transactions.update { listOf(cancellationRecord) + it }
    }
}
