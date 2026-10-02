package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.ChamaDatabase
import com.example.data.firebase.FirebaseManager
import com.example.data.gemini.GeminiService
import com.example.data.model.ChatMessage
import com.example.data.model.ContributionEntity
import com.example.data.model.LoanEntity
import com.example.data.model.MeetingEntity
import com.example.data.model.MemberEntity
import com.example.data.model.MessageSender
import com.example.data.model.PayoutEntity
import com.example.data.repository.ChamaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FinancialSummary(
    val totalSavingsPool: Double = 0.0,
    val activeLoansTotal: Double = 0.0,
    val totalInterestEarned: Double = 0.0,
    val availableCashAtHand: Double = 0.0,
    val repaymentRatePercent: Double = 100.0,
    val totalMembersCount: Int = 0,
    val totalSavings: Double = 0.0,
    val activeLoansBalance: Double = 0.0,
    val totalLoansCount: Int = 0,
    val welfareFund: Double = 0.0,
    val totalMembers: Int = 0
)

typealias ChamaFinancialSummary = FinancialSummary

class ChamaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChamaRepository
    val firebaseManager: FirebaseManager
    private val geminiService = GeminiService()

    init {
        val db = ChamaDatabase.getDatabase(application)
        repository = ChamaRepository(db.chamaDao())
        firebaseManager = FirebaseManager(application.applicationContext)
    }

    // Navigation Tab state: 0 = Dashboard, 1 = Contributions, 2 = Loans, 3 = Members, 4 = Records, 5 = AI Advisor
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    // Modal dialogs state
    val showAddContributionDialog = MutableStateFlow(false)
    val showIssueLoanDialog = MutableStateFlow(false)
    val showAddMemberDialog = MutableStateFlow(false)
    val showAddMeetingDialog = MutableStateFlow(false)
    val showAddPayoutDialog = MutableStateFlow(false)
    val showClearConfirmDialog = MutableStateFlow(false)
    val showAuthDialog = MutableStateFlow(false)
    val selectedLoanForRepayment = MutableStateFlow<LoanEntity?>(null)

    // Data streams from Room Repository
    val members: StateFlow<List<MemberEntity>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contributions: StateFlow<List<ContributionEntity>> = repository.allContributions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loans: StateFlow<List<LoanEntity>> = repository.allLoans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payouts: StateFlow<List<PayoutEntity>> = repository.allPayouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val meetings: StateFlow<List<MeetingEntity>> = repository.allMeetings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial Metrics Calculation
    val financialSummary: StateFlow<FinancialSummary> = combine(
        members,
        contributions,
        loans
    ) { membersList, contributionsList, loansList ->
        val totalSavings = contributionsList.sumOf { it.amount }
        val activeLoans = loansList.filter { it.status == "ACTIVE" }
        val activeLoansBalance = activeLoans.sumOf { it.remainingBalance }
        val totalLoansDisbursed = loansList.sumOf { it.principalAmount }
        val totalLoanRepayments = loansList.sumOf { it.amountRepaid }
        val totalInterestEarned = loansList.sumOf { (it.totalDue - it.principalAmount) }
        val welfare = contributionsList.filter { it.contributionType.contains("Welfare", ignoreCase = true) }.sumOf { it.amount }

        val cashAtHand = (totalSavings + totalLoanRepayments) - totalLoansDisbursed
        val totalExpectedRepayments = loansList.sumOf { it.totalDue }
        val repaymentRate = if (totalExpectedRepayments > 0) {
            (totalLoanRepayments / totalExpectedRepayments) * 100.0
        } else 100.0

        FinancialSummary(
            totalSavingsPool = totalSavings,
            activeLoansTotal = activeLoansBalance,
            totalInterestEarned = totalInterestEarned,
            availableCashAtHand = if (cashAtHand < 0) 0.0 else cashAtHand,
            repaymentRatePercent = repaymentRate.coerceIn(0.0, 100.0),
            totalMembersCount = membersList.size,
            totalSavings = totalSavings,
            activeLoansBalance = activeLoansBalance,
            totalLoansCount = activeLoans.size,
            welfareFund = if (welfare > 0) welfare else (totalSavings * 0.15),
            totalMembers = membersList.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary())

    // Firebase Auth & Firestore Sync state
    val currentUserProfile = firebaseManager.currentUserProfile
    val firestoreSyncStatus = firebaseManager.syncStatus
    val isAuthLoading = firebaseManager.isLoading

    init {
        // Automatically sync data from Firestore on launch
        viewModelScope.launch {
            syncMembersFromFirestore()
            syncLoansFromFirestore()
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            firebaseManager.signInWithGoogle()
        }
    }

    fun signInDemoUser(name: String, email: String) {
        viewModelScope.launch {
            firebaseManager.signInDemoGoogleUser(name, email)
        }
    }

    fun signOut() {
        firebaseManager.signOut()
    }

    fun syncAllToFirestore() {
        viewModelScope.launch {
            firebaseManager.syncAllData(
                members.value,
                contributions.value,
                loans.value
            )
        }
    }

    // Gemini AI Multi-turn Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = MessageSender.CHAMA_AI,
                text = "Habari! I am ChamaBot, your Kenyan Table Banking & Chama AI Advisor.\n\n" +
                        "I can help you with:\n" +
                        "• Analyzing loan repayments & interest rates (10% flat table-banking)\n" +
                        "• Central Bank of Kenya (CBK) rates & inflation insights\n" +
                        "• Managing M-Pesa records and merry-go-round rotation schedules\n" +
                        "• Microfinance & SACCO transition guidelines\n\n" +
                        "Ask any question about managing your group members, loans, or finances!",
                modelUsed = "gemini-3.1-flash-lite-preview",
                isGroundingUsed = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _selectedAiModel = MutableStateFlow("gemini-3.1-flash-lite-preview")
    val selectedAiModel: StateFlow<String> = _selectedAiModel.asStateFlow()

    fun setSelectedAiModel(model: String) {
        _selectedAiModel.value = model
    }

    private val _isSearchGroundingEnabled = MutableStateFlow(false)
    val isSearchGroundingEnabled: StateFlow<Boolean> = _isSearchGroundingEnabled.asStateFlow()

    fun toggleSearchGrounding(enabled: Boolean) {
        _isSearchGroundingEnabled.value = enabled
    }

    private val _isChatGenerating = MutableStateFlow(false)
    val isChatGenerating: StateFlow<Boolean> = _isChatGenerating.asStateFlow()

    fun sendChatMessage(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isBlank() || _isChatGenerating.value) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            text = trimmed
        )
        val updatedList = _chatMessages.value + userMessage
        _chatMessages.value = updatedList
        _isChatGenerating.value = true

        viewModelScope.launch {
            val response = geminiService.generateChatResponse(
                history = updatedList,
                newPrompt = trimmed,
                modelId = _selectedAiModel.value,
                enableSearchGrounding = _isSearchGroundingEnabled.value
            )

            val aiMessage = ChatMessage(
                sender = MessageSender.CHAMA_AI,
                text = response.text,
                modelUsed = _selectedAiModel.value,
                isGroundingUsed = response.isGroundingUsed,
                searchSources = response.searchSources
            )

            _chatMessages.value = _chatMessages.value + aiMessage
            _isChatGenerating.value = false
        }
    }

    fun clearChatHistory() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = MessageSender.CHAMA_AI,
                text = "Chat history cleared. How can I assist your Chama today?",
                modelUsed = _selectedAiModel.value
            )
        )
    }

    // Business Actions with Firestore Persistence

    fun addContribution(
        memberId: Long,
        memberName: String,
        amount: Double,
        type: String,
        method: String,
        referenceCode: String,
        notes: String
    ) {
        val actualMemberId = if (memberId == 0L && memberName.isNotBlank()) {
            ensureMemberExists(memberName)
        } else {
            memberId
        }

        val contribution = ContributionEntity(
            memberId = actualMemberId,
            memberName = memberName,
            amount = amount,
            contributionType = type,
            paymentMethod = method,
            referenceCode = referenceCode,
            timestamp = System.currentTimeMillis(),
            notes = notes
        )
        viewModelScope.launch {
            val id = repository.addContribution(contribution)
            firebaseManager.saveContributionToFirestore(contribution.copy(id = id))
        }
    }

    // --- Record Member Loans with Firestore Collection 'loans' ---

    fun recordMemberLoan(
        borrowerId: Long,
        borrowerName: String,
        loanAmount: Double,
        interestRatePercent: Double,
        disbursementDate: String,
        disbursementTimestamp: Long,
        durationMonths: Int,
        repaymentStatus: String,
        purpose: String,
        guarantor: String
    ) {
        val actualBorrowerId = if (borrowerId == 0L && borrowerName.isNotBlank()) {
            ensureMemberExists(borrowerName)
        } else {
            borrowerId
        }

        val totalDue = loanAmount + (loanAmount * (interestRatePercent / 100.0))
        val dueTime = disbursementTimestamp + (durationMonths.toLong() * 30L * 24L * 60L * 60L * 1000L)

        val loan = LoanEntity(
            memberId = actualBorrowerId,
            memberName = borrowerName,
            principalAmount = loanAmount,
            interestRatePercent = interestRatePercent,
            durationMonths = durationMonths,
            totalDue = totalDue,
            amountRepaid = 0.0,
            issuedTimestamp = disbursementTimestamp,
            dueTimestamp = dueTime,
            purpose = purpose,
            status = repaymentStatus,
            guarantorName = guarantor
        )
        viewModelScope.launch {
            val id = repository.issueLoan(loan)
            val savedLoan = loan.copy(id = id)
            // Persist to Cloud Firestore 'loans' collection
            firebaseManager.saveLoanToFirestore(savedLoan)
        }
    }

    fun updateLoanStatus(loan: LoanEntity, newStatus: String) {
        val updated = loan.copy(status = newStatus)
        viewModelScope.launch {
            repository.updateLoan(updated)
            firebaseManager.updateLoanRepaymentStatusInFirestore(loan.id, newStatus)
        }
    }

    fun deleteLoan(id: Long) {
        viewModelScope.launch {
            repository.deleteLoan(id)
            firebaseManager.deleteLoanFromFirestore(id)
        }
    }

    fun syncLoansFromFirestore() {
        viewModelScope.launch {
            val remoteLoans = firebaseManager.fetchLoansFromFirestore()
            if (remoteLoans.isNotEmpty()) {
                remoteLoans.forEach { remote ->
                    repository.issueLoan(remote)
                }
            }
        }
    }

    fun recordRepayment(
        loan: LoanEntity,
        amount: Double,
        method: String,
        referenceCode: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.recordLoanRepayment(loan, amount, method, referenceCode, notes)
            val updatedAmountRepaid = loan.amountRepaid + amount
            val updatedBalance = (loan.totalDue - updatedAmountRepaid).coerceAtLeast(0.0)
            val isCleared = updatedBalance <= 0.01
            val updatedLoan = loan.copy(
                amountRepaid = updatedAmountRepaid,
                status = if (isCleared) "REPAID" else "ACTIVE"
            )
            // Update Firestore 'loans' collection
            firebaseManager.saveLoanToFirestore(updatedLoan)
        }
    }

    // --- Member Management with Cloud Firestore Persistence ---

    fun addMember(
        name: String,
        phone: String,
        status: String = "Active",
        idNumber: String = "",
        role: String = "Member",
        shares: Int = 1
    ) {
        val member = MemberEntity(
            name = name,
            phone = phone,
            idNumber = idNumber,
            role = role,
            joinDate = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date()),
            sharesCount = shares,
            isGoodStanding = (status == "Active"),
            status = status
        )
        viewModelScope.launch {
            val id = repository.addMember(member)
            val savedMember = member.copy(id = id)
            // Persist directly to Cloud Firestore
            firebaseManager.saveMemberToFirestore(savedMember)
        }
    }

    fun updateMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.updateMember(member)
            firebaseManager.saveMemberToFirestore(member)
        }
    }

    fun deleteMember(member: MemberEntity) {
        viewModelScope.launch {
            repository.deleteMember(member)
            firebaseManager.deleteMemberFromFirestore(member.id)
        }
    }

    fun syncMembersFromFirestore() {
        viewModelScope.launch {
            val remoteMembers = firebaseManager.fetchMembersFromFirestore()
            if (remoteMembers.isNotEmpty()) {
                remoteMembers.forEach { remote ->
                    repository.addMember(remote)
                }
            }
        }
    }

    fun ensureMemberExists(name: String): Long {
        val existing = members.value.firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }
        if (existing != null) return existing.id

        var newId = 0L
        val newMember = MemberEntity(
            name = name.trim(),
            phone = "07" + (10000000..99999999).random(),
            idNumber = "",
            role = "Member",
            joinDate = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date()),
            sharesCount = 1,
            isGoodStanding = true,
            status = "Active"
        )
        viewModelScope.launch {
            newId = repository.addMember(newMember)
            firebaseManager.saveMemberToFirestore(newMember.copy(id = newId))
        }
        return newId
    }

    fun deleteContribution(id: Long) {
        viewModelScope.launch {
            repository.deleteContribution(id)
        }
    }

    fun addPayout(
        cycleNumber: Int,
        recipientMemberId: Long,
        recipientMemberName: String,
        amount: Double,
        scheduledDate: String,
        notes: String
    ) {
        val payout = PayoutEntity(
            cycleNumber = cycleNumber,
            recipientMemberId = recipientMemberId,
            recipientName = recipientMemberName,
            amount = amount,
            scheduledDate = scheduledDate,
            status = "PENDING",
            notes = notes
        )
        viewModelScope.launch {
            repository.addPayout(payout)
        }
    }

    fun deletePayout(id: Long) {
        viewModelScope.launch {
            repository.deletePayout(id)
        }
    }

    fun togglePayoutStatus(payout: PayoutEntity) {
        val newStatus = if (payout.status == "COMPLETED") "PENDING" else "COMPLETED"
        viewModelScope.launch {
            repository.updatePayout(payout.copy(status = newStatus))
        }
    }

    fun addMeetingRecord(
        date: String,
        venue: String,
        attendance: Int,
        savingsCollected: Double,
        loansDisbursed: Double,
        recipient: String,
        minutes: String
    ) {
        val meeting = MeetingRecordEntity(
            meetingDate = date,
            venue = venue,
            attendanceCount = attendance,
            totalSavingsCollected = savingsCollected,
            totalLoansDisbursed = loansDisbursed,
            merryGoRoundWinner = recipient,
            keyDecisions = minutes
        )
        viewModelScope.launch {
            repository.addMeeting(meeting)
        }
    }

    fun deleteMeeting(id: Long) {
        viewModelScope.launch {
            repository.deleteMeeting(id)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }
}
