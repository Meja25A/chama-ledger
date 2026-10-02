package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val idNumber: String = "",
    val role: String = "Member", // "Chairperson", "Treasurer", "Secretary", "Member"
    val joinDate: String = "",
    val sharesCount: Int = 1,
    val isGoodStanding: Boolean = true,
    val status: String = "Active" // "Active", "Pending", "Suspended", "Dormant"
)

@Entity(tableName = "contributions")
data class ContributionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val amount: Double,
    val contributionType: String,
    val paymentMethod: String,
    val referenceCode: String,
    val timestamp: Long,
    val notes: String = ""
)

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val principalAmount: Double,
    val interestRatePercent: Double = 10.0,
    val durationMonths: Int = 3,
    val totalDue: Double,
    val amountRepaid: Double = 0.0,
    val issuedTimestamp: Long,
    val dueTimestamp: Long,
    val purpose: String,
    val status: String = "ACTIVE", // "ACTIVE", "REPAID", "OVERDUE"
    val guarantorName: String = "Committee Guaranteed"
) {
    val remainingBalance: Double
        get() = (totalDue - amountRepaid).coerceAtLeast(0.0)
    val progressFraction: Float
        get() = if (totalDue > 0) (amountRepaid / totalDue).coerceIn(0.0, 1.0).toFloat() else 1f
}

@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cycleNumber: Int,
    val recipientMemberId: Long,
    val recipientName: String,
    val amount: Double,
    val scheduledDate: String,
    val status: String = "PENDING", // "COMPLETED", "PENDING", "UPCOMING"
    val notes: String = ""
) {
    val isPaid: Boolean
        get() = status == "COMPLETED"
    val recipientMemberName: String
        get() = recipientName
    val payoutAmount: Double
        get() = amount
}

@Entity(tableName = "meeting_records")
data class MeetingRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val meetingDate: String,
    val venue: String,
    val attendanceCount: Int,
    val totalSavingsCollected: Double,
    val totalLoansDisbursed: Double,
    val merryGoRoundWinner: String,
    val keyDecisions: String
) {
    val date: String
        get() = meetingDate
    val totalAttendance: Int
        get() = attendanceCount
    val merryGoRoundRecipient: String
        get() = merryGoRoundWinner
    val minutesSummary: String
        get() = keyDecisions
}

typealias MeetingEntity = MeetingRecordEntity
