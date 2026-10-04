package com.example.data

import android.content.Context
import com.example.model.RoseLedgerEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class RoseLedgerRepository(private val context: Context) {

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private val _ledgerEntries = MutableStateFlow<List<RoseLedgerEntry>>(emptyList())
    val ledgerEntries: StateFlow<List<RoseLedgerEntry>> = _ledgerEntries.asStateFlow()

    private val _currentBalance = MutableStateFlow(38)
    val currentBalance: StateFlow<Int> = _currentBalance.asStateFlow()

    init {
        loadInitialLedger()
    }

    private fun loadInitialLedger() {
        val initialEntries = listOf(
            RoseLedgerEntry(
                id = "LEDGER-INIT-01",
                userId = "current_user",
                type = "EARN",
                amount = 10,
                balanceAfter = 10,
                description = "مكافأة إكمال الملف الشخصي وتوثيق الحساب",
                timestamp = System.currentTimeMillis() - 86400000L * 3
            ),
            RoseLedgerEntry(
                id = "LEDGER-INIT-02",
                userId = "current_user",
                type = "RECEIVE",
                amount = 20,
                balanceAfter = 30,
                counterpartyId = "cand_1",
                counterpartyName = "د. كريم سامي",
                description = "باقة ورود إعجاب وتعارف شرعي راقي 🌹",
                timestamp = System.currentTimeMillis() - 86400000L * 2
            ),
            RoseLedgerEntry(
                id = "LEDGER-INIT-03",
                userId = "current_user",
                type = "BUY",
                amount = 15,
                balanceAfter = 45,
                description = "شراء باقة 15 وردة عبر فودافون كاش",
                timestamp = System.currentTimeMillis() - 86400000L
            ),
            RoseLedgerEntry(
                id = "LEDGER-INIT-04",
                userId = "current_user",
                type = "SPEND",
                amount = 7,
                balanceAfter = 38,
                counterpartyId = "cand_3",
                counterpartyName = "م. يوسف النجار",
                description = "إرسال باقة ورد مع كسر جمود المحادثة",
                timestamp = System.currentTimeMillis() - 3600000L * 4
            )
        )
        _ledgerEntries.value = initialEntries
        _currentBalance.value = initialEntries.lastOrNull()?.balanceAfter ?: 38
    }

    /**
     * Atomically executes a rose balance transaction and records in immutable ledger
     */
    @Synchronized
    fun recordTransaction(
        type: String, // EARN, BUY, SEND, RECEIVE, SPEND, REFUND, ADMIN_GRANT, ADMIN_DEDUCT
        amount: Int,
        counterpartyId: String? = null,
        counterpartyName: String? = null,
        description: String
    ): Result<RoseLedgerEntry> {
        val current = _currentBalance.value

        val newBalance = when (type) {
            "EARN", "BUY", "RECEIVE", "REFUND", "ADMIN_GRANT" -> current + amount
            "SEND", "SPEND", "ADMIN_DEDUCT" -> {
                if (current < amount && type != "ADMIN_DEDUCT") {
                    return Result.failure(IllegalStateException("رصيد الورد غير كافٍ لإتمام العملية (الرصيد الحالي: $current وردة)"))
                }
                maxOf(0, current - amount)
            }
            else -> current
        }

        val entry = RoseLedgerEntry(
            id = "LEDGER-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(4)}",
            userId = "current_user",
            type = type,
            amount = amount,
            balanceAfter = newBalance,
            counterpartyId = counterpartyId,
            counterpartyName = counterpartyName,
            description = description,
            timestamp = System.currentTimeMillis()
        )

        _currentBalance.value = newBalance
        _ledgerEntries.update { listOf(entry) + it }

        // Sync with Firestore asynchronously
        try {
            val docData = hashMapOf(
                "id" to entry.id,
                "userId" to entry.userId,
                "type" to entry.type,
                "amount" to entry.amount,
                "balanceAfter" to entry.balanceAfter,
                "counterpartyId" to (entry.counterpartyId ?: ""),
                "counterpartyName" to (entry.counterpartyName ?: ""),
                "description" to entry.description,
                "timestamp" to entry.timestamp
            )
            firestore.collection("rose_ledger").document(entry.id).set(docData)
        } catch (_: Exception) {}

        return Result.success(entry)
    }
}
