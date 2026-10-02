package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ContributionEntity
import com.example.data.model.LoanEntity
import com.example.data.model.MeetingRecordEntity
import com.example.data.model.MemberEntity
import com.example.data.model.PayoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChamaDao {

    // --- Members ---
    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: Long): MemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>)

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    // --- Contributions ---
    @Query("SELECT * FROM contributions ORDER BY timestamp DESC")
    fun getAllContributions(): Flow<List<ContributionEntity>>

    @Query("SELECT * FROM contributions WHERE memberId = :memberId ORDER BY timestamp DESC")
    fun getContributionsForMember(memberId: Long): Flow<List<ContributionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContribution(contribution: ContributionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContributions(contributions: List<ContributionEntity>)

    @Query("DELETE FROM contributions WHERE id = :id")
    suspend fun deleteContribution(id: Long)

    // --- Loans ---
    @Query("SELECT * FROM loans ORDER BY issuedTimestamp DESC")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE status = 'ACTIVE' ORDER BY dueTimestamp ASC")
    fun getActiveLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE memberId = :memberId ORDER BY issuedTimestamp DESC")
    fun getLoansForMember(memberId: Long): Flow<List<LoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoans(loans: List<LoanEntity>)

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteLoan(id: Long)

    // --- Payouts (Merry-Go-Round) ---
    @Query("SELECT * FROM payouts ORDER BY cycleNumber ASC")
    fun getAllPayouts(): Flow<List<PayoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: PayoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayouts(payouts: List<PayoutEntity>)

    @Update
    suspend fun updatePayout(payout: PayoutEntity)

    // --- Meeting Records ---
    @Query("SELECT * FROM meeting_records ORDER BY id DESC")
    fun getAllMeetings(): Flow<List<MeetingRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: MeetingRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeetings(meetings: List<MeetingRecordEntity>)

    @Query("DELETE FROM payouts WHERE id = :id")
    suspend fun deletePayout(id: Long)

    @Query("DELETE FROM meeting_records WHERE id = :id")
    suspend fun deleteMeeting(id: Long)

    // --- Clear All Data ---
    @Query("DELETE FROM members")
    suspend fun clearAllMembers()

    @Query("DELETE FROM contributions")
    suspend fun clearAllContributions()

    @Query("DELETE FROM loans")
    suspend fun clearAllLoans()

    @Query("DELETE FROM payouts")
    suspend fun clearAllPayouts()

    @Query("DELETE FROM meeting_records")
    suspend fun clearAllMeetings()
}
