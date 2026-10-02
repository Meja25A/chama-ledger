package com.example.data.repository

import com.example.data.dao.ChamaDao
import com.example.data.model.ContributionEntity
import com.example.data.model.LoanEntity
import com.example.data.model.MeetingRecordEntity
import com.example.data.model.MemberEntity
import com.example.data.model.PayoutEntity
import kotlinx.coroutines.flow.Flow

class ChamaRepository(private val dao: ChamaDao) {
    val allMembers: Flow<List<MemberEntity>> = dao.getAllMembers()
    val allContributions: Flow<List<ContributionEntity>> = dao.getAllContributions()
    val allLoans: Flow<List<LoanEntity>> = dao.getAllLoans()
    val activeLoans: Flow<List<LoanEntity>> = dao.getActiveLoans()
    val allPayouts: Flow<List<PayoutEntity>> = dao.getAllPayouts()
    val allMeetings: Flow<List<MeetingRecordEntity>> = dao.getAllMeetings()

    suspend fun getMemberById(id: Long): MemberEntity? = dao.getMemberById(id)
    suspend fun addMember(member: MemberEntity): Long = dao.insertMember(member)
    suspend fun updateMember(member: MemberEntity) = dao.updateMember(member)
    suspend fun deleteMember(member: MemberEntity) = dao.deleteMember(member)

    suspend fun addContribution(contribution: ContributionEntity): Long =
        dao.insertContribution(contribution)
    suspend fun deleteContribution(id: Long) = dao.deleteContribution(id)

    suspend fun issueLoan(loan: LoanEntity): Long = dao.insertLoan(loan)
    suspend fun updateLoan(loan: LoanEntity) = dao.updateLoan(loan)
    suspend fun deleteLoan(id: Long) = dao.deleteLoan(id)

    suspend fun recordLoanRepayment(
        loan: LoanEntity,
        repaymentAmount: Double,
        paymentMethod: String,
        referenceCode: String,
        notes: String
    ) {
        val newAmountRepaid = loan.amountRepaid + repaymentAmount
        val isFullyRepaid = newAmountRepaid >= (loan.totalDue - 0.01)
        val updatedLoan = loan.copy(
            amountRepaid = newAmountRepaid,
            status = if (isFullyRepaid) "REPAID" else "ACTIVE"
        )
        dao.updateLoan(updatedLoan)

        val now = System.currentTimeMillis()
        dao.insertContribution(
            ContributionEntity(
                memberId = loan.memberId,
                memberName = loan.memberName,
                amount = repaymentAmount,
                contributionType = "Loan Repayment",
                paymentMethod = paymentMethod,
                referenceCode = referenceCode.ifBlank { "REP-${now % 100000}" },
                timestamp = now,
                notes = "Loan #${loan.id} repayment: $notes"
            )
        )
    }

    suspend fun updatePayout(payout: PayoutEntity) = dao.updatePayout(payout)
    suspend fun addPayout(payout: PayoutEntity): Long = dao.insertPayout(payout)
    suspend fun deletePayout(id: Long) = dao.deletePayout(id)

    suspend fun addMeeting(meeting: MeetingRecordEntity): Long = dao.insertMeeting(meeting)
    suspend fun addMeetingRecord(meeting: MeetingRecordEntity): Long = dao.insertMeeting(meeting)
    suspend fun deleteMeeting(id: Long) = dao.deleteMeeting(id)

    suspend fun clearAllData() {
        dao.clearAllContributions()
        dao.clearAllLoans()
        dao.clearAllMembers()
        dao.clearAllPayouts()
        dao.clearAllMeetings()
    }
}
