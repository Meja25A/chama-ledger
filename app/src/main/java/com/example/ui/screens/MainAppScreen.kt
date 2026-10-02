package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AddContributionDialog
import com.example.ui.components.AddMeetingDialog
import com.example.ui.components.AddMemberDialog
import com.example.ui.components.AddPayoutDialog
import com.example.ui.components.AuthDialog
import com.example.ui.components.RecordLoanDialog
import com.example.ui.components.RecordLoanRepaymentDialog
import com.example.ui.theme.ChamaGoldTertiary
import com.example.ui.theme.ChamaGreenPrimary
import com.example.ui.viewmodel.ChamaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: ChamaViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val members by viewModel.members.collectAsState()
    val contributions by viewModel.contributions.collectAsState()
    val loans by viewModel.loans.collectAsState()
    val payouts by viewModel.payouts.collectAsState()
    val meetings by viewModel.meetings.collectAsState()
    val financialSummary by viewModel.financialSummary.collectAsState()

    val showAddContribution by viewModel.showAddContributionDialog.collectAsState()
    val showIssueLoan by viewModel.showIssueLoanDialog.collectAsState()
    val showAddMember by viewModel.showAddMemberDialog.collectAsState()
    val showAddMeeting by viewModel.showAddMeetingDialog.collectAsState()
    val showAddPayout by viewModel.showAddPayoutDialog.collectAsState()
    val showClearConfirm by viewModel.showClearConfirmDialog.collectAsState()
    val showAuthDialog by viewModel.showAuthDialog.collectAsState()
    val selectedLoanForRepayment by viewModel.selectedLoanForRepayment.collectAsState()

    // Auth & Sync State
    val currentUserProfile by viewModel.currentUserProfile.collectAsState()
    val firestoreSyncStatus by viewModel.firestoreSyncStatus.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()

    // AI Chat State
    val chatMessages by viewModel.chatMessages.collectAsState()
    val selectedAiModel by viewModel.selectedAiModel.collectAsState()
    val isSearchGroundingEnabled by viewModel.isSearchGroundingEnabled.collectAsState()
    val isChatGenerating by viewModel.isChatGenerating.collectAsState()

    // Back handling: If on secondary tab, return to Dashboard first
    BackHandler(enabled = currentTab != 0) {
        viewModel.setTab(0)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFF00A859), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Chama Ledger",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Digital Table Banking • Kenya",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    // Reset / Clear Data Button
                    IconButton(
                        onClick = { viewModel.showClearConfirmDialog.value = true },
                        modifier = Modifier.testTag("btn_clear_data")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear All Data",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Firebase Auth & Firestore Sync Badge / Button
                    Surface(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { viewModel.showAuthDialog.value = true }
                            .testTag("btn_auth_profile"),
                        shape = RoundedCornerShape(14.dp),
                        color = if (currentUserProfile != null) ChamaGreenPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (currentUserProfile != null) Icons.Default.CloudDone else Icons.Default.Lock,
                                contentDescription = "Firebase Status",
                                tint = if (currentUserProfile != null) ChamaGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentUserProfile != null) "Mejja • Connected" else "Sign In",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentUserProfile != null) ChamaGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Home", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChamaGreenPrimary,
                        indicatorColor = ChamaGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = { Icon(Icons.Default.Savings, contentDescription = "Savings") },
                    label = { Text("Savings", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChamaGreenPrimary,
                        indicatorColor = ChamaGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_contributions")
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Loans") },
                    label = { Text("Loans", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChamaGreenPrimary,
                        indicatorColor = ChamaGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_loans")
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { viewModel.setTab(3) },
                    icon = { Icon(Icons.Default.Group, contentDescription = "Members") },
                    label = { Text("Members", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChamaGreenPrimary,
                        indicatorColor = ChamaGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_members")
                )
                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { viewModel.setTab(4) },
                    icon = { Icon(Icons.Default.HistoryEdu, contentDescription = "Records") },
                    label = { Text("Records", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChamaGreenPrimary,
                        indicatorColor = ChamaGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_records")
                )
                NavigationBarItem(
                    selected = currentTab == 5,
                    onClick = { viewModel.setTab(5) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Chama AI") },
                    label = { Text("AI Advisor", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChamaGoldTertiary,
                        indicatorColor = ChamaGoldTertiary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_ai_advisor")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> DashboardScreen(
                    summary = financialSummary,
                    recentContributions = contributions,
                    activeLoans = loans.filter { it.status == "ACTIVE" },
                    onAddContributionClick = { viewModel.showAddContributionDialog.value = true },
                    onIssueLoanClick = { viewModel.showIssueLoanDialog.value = true },
                    onNavigateToContributions = { viewModel.setTab(1) },
                    onNavigateToLoans = { viewModel.setTab(2) },
                    onNavigateToRecords = { viewModel.setTab(4) },
                    onNavigateToAiAdvisor = { viewModel.setTab(5) }
                )
                1 -> ContributionsScreen(
                    contributions = contributions,
                    onAddContributionClick = { viewModel.showAddContributionDialog.value = true },
                    onDeleteContribution = { viewModel.deleteContribution(it) }
                )
                2 -> LoansScreen(
                    loans = loans,
                    onIssueLoanClick = { viewModel.showIssueLoanDialog.value = true },
                    onRepaymentClick = { loan -> viewModel.selectedLoanForRepayment.value = loan },
                    onUpdateLoanStatus = { loan, newStatus -> viewModel.updateLoanStatus(loan, newStatus) },
                    onDeleteLoan = { loanId -> viewModel.deleteLoan(loanId) },
                    onRefreshFromFirestore = { viewModel.syncLoansFromFirestore() },
                    firestoreStatus = firestoreSyncStatus
                )
                3 -> MembersScreen(
                    members = members,
                    contributions = contributions,
                    loans = loans,
                    onAddMemberClick = { viewModel.showAddMemberDialog.value = true },
                    onUpdateMember = { updated -> viewModel.updateMember(updated) },
                    onDeleteMember = { member -> viewModel.deleteMember(member) },
                    onRefreshFromFirestore = { viewModel.syncMembersFromFirestore() },
                    firestoreStatus = firestoreSyncStatus
                )
                4 -> RecordsScreen(
                    payouts = payouts,
                    meetings = meetings,
                    onTogglePayoutStatus = { payout -> viewModel.togglePayoutStatus(payout) },
                    onAddMeetingClick = { viewModel.showAddMeetingDialog.value = true },
                    onAddPayoutClick = { viewModel.showAddPayoutDialog.value = true },
                    onDeletePayout = { viewModel.deletePayout(it) },
                    onDeleteMeeting = { viewModel.deleteMeeting(it) }
                )
                5 -> AiAdvisorScreen(
                    messages = chatMessages,
                    isGenerating = isChatGenerating,
                    selectedModel = selectedAiModel,
                    isSearchGroundingEnabled = isSearchGroundingEnabled,
                    onSelectModel = { viewModel.setSelectedAiModel(it) },
                    onToggleSearchGrounding = { viewModel.toggleSearchGrounding(it) },
                    onSendMessage = { viewModel.sendChatMessage(it) },
                    onClearChat = { viewModel.clearChatHistory() }
                )
            }
        }
    }

    // Modal Dialogs
    if (showAuthDialog) {
        AuthDialog(
            userProfile = currentUserProfile,
            syncStatus = firestoreSyncStatus,
            isLoading = isAuthLoading,
            onDismiss = { viewModel.showAuthDialog.value = false },
            onSignInWithGoogle = { viewModel.signInWithGoogle() },
            onSignInDemo = { name, email -> viewModel.signInDemoUser(name, email) },
            onSignOut = { viewModel.signOut() },
            onSyncAllToFirestore = { viewModel.syncAllToFirestore() }
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.showClearConfirmDialog.value = false },
            title = { Text("Clear All Chama Data?") },
            text = { Text("This will permanently erase all contributions, loans, members, payouts, and meetings from the database so you can start completely fresh.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        viewModel.showClearConfirmDialog.value = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All Data")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.showClearConfirmDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddContribution) {
        AddContributionDialog(
            members = members,
            onDismiss = { viewModel.showAddContributionDialog.value = false },
            onConfirm = { memberId, memberName, amount, type, method, refCode, notes ->
                viewModel.addContribution(memberId, memberName, amount, type, method, refCode, notes)
                viewModel.showAddContributionDialog.value = false
            }
        )
    }

    if (showIssueLoan) {
        RecordLoanDialog(
            members = members,
            onDismiss = { viewModel.showIssueLoanDialog.value = false },
            onConfirm = { borrowerId, borrowerName, loanAmount, interestRate, disbDate, disbTimestamp, duration, status, purpose, guarantor ->
                viewModel.recordMemberLoan(
                    borrowerId,
                    borrowerName,
                    loanAmount,
                    interestRate,
                    disbDate,
                    disbTimestamp,
                    duration,
                    status,
                    purpose,
                    guarantor
                )
                viewModel.showIssueLoanDialog.value = false
            }
        )
    }

    if (showAddPayout) {
        AddPayoutDialog(
            members = members,
            nextCycleNumber = (payouts.maxOfOrNull { it.cycleNumber } ?: 0) + 1,
            onDismiss = { viewModel.showAddPayoutDialog.value = false },
            onConfirm = { cycle, recipientId, recipientName, amount, date, notes ->
                viewModel.addPayout(cycle, recipientId, recipientName, amount, date, notes)
                viewModel.showAddPayoutDialog.value = false
            }
        )
    }

    selectedLoanForRepayment?.let { loan ->
        RecordLoanRepaymentDialog(
            loan = loan,
            onDismiss = { viewModel.selectedLoanForRepayment.value = null },
            onConfirm = { amount, method, refCode, notes ->
                viewModel.recordRepayment(loan, amount, method, refCode, notes)
                viewModel.selectedLoanForRepayment.value = null
            }
        )
    }

    if (showAddMember) {
        AddMemberDialog(
            onDismiss = { viewModel.showAddMemberDialog.value = false },
            onConfirm = { name, phone, status, idNumber, role, shares ->
                viewModel.addMember(name, phone, status, idNumber, role, shares)
                viewModel.showAddMemberDialog.value = false
            }
        )
    }

    if (showAddMeeting) {
        AddMeetingDialog(
            members = members,
            onDismiss = { viewModel.showAddMeetingDialog.value = false },
            onConfirm = { date, venue, attendance, savings, loansAmt, recipient, notes ->
                viewModel.addMeetingRecord(date, venue, attendance, savings, loansAmt, recipient, notes)
                viewModel.showAddMeetingDialog.value = false
            }
        )
    }
}
