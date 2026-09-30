package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CandidateProfile
import com.example.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {
    @Query("SELECT * FROM candidates WHERE isPassed = 0 AND isLiked = 0 ORDER BY id ASC")
    fun getActiveCandidates(): Flow<List<CandidateProfile>>

    @Query("SELECT * FROM candidates WHERE isMatched = 1")
    fun getMatchedCandidates(): Flow<List<CandidateProfile>>

    @Query("SELECT * FROM candidates WHERE id = :id LIMIT 1")
    suspend fun getCandidateById(id: String): CandidateProfile?

    @Query("SELECT COUNT(*) FROM candidates")
    suspend fun countCandidates(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(candidates: List<CandidateProfile>)

    @Update
    suspend fun update(candidate: CandidateProfile)

    @Query("UPDATE candidates SET isLiked = 1, isMatched = :isMatch WHERE id = :id")
    suspend fun markLiked(id: String, isMatch: Boolean)

    @Query("UPDATE candidates SET isPassed = 1 WHERE id = :id")
    suspend fun markPassed(id: String)

    @Query("UPDATE candidates SET isPhotoBlurred = NOT isPhotoBlurred WHERE id = :id")
    suspend fun toggleBlur(id: String)

    @Query("UPDATE candidates SET rosesReceivedCount = rosesReceivedCount + 1 WHERE id = :id")
    suspend fun incrementRoses(id: String)

    @Query("UPDATE candidates SET isLiked = 0, isPassed = 0, isMatched = 0")
    suspend fun resetAllDiscovery()
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE candidateId = :candidateId ORDER BY timestamp ASC")
    fun getMessagesForCandidate(candidateId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long
}
