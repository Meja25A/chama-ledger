package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.LoanEntity
import com.example.data.model.MemberEntity
import com.example.ui.components.formatDate
import com.example.ui.components.formatKSh
import com.example.ui.theme.ChamaGoldTertiary
import com.example.ui.theme.ChamaGreenPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LoansScreen(
    loans: List<LoanEntity>,
    onIssueLoanClick: () -> Unit,
    onRepaymentClick: (LoanEntity) -> Unit,
    onUpdateLoanStatus: (LoanEntity, String) -> Unit = { _, _ -> },
    onDeleteLoan: (Long) -> Unit = {},
    onRefreshFromFirestore: () -> Unit = {},
    firestoreStatus: String = "Firestore Collection 'loans' Synced",
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var loanToDelete by remember { mutableStateOf<LoanEntity?>(null) }

    val statusTabs = listOf("ALL", "ACTIVE", "REPAID", "OVERDUE")

    val filteredLoans = loans.filter { loan ->
        val matchesSearch = searchQuery.isBlank() ||
                loan.memberName.contains(searchQuery, ignoreCase = true) ||
                loan.purpose.contains(searchQuery, ignoreCase = true) ||
                loan.status.contains(searchQuery, ignoreCase = true)

        val matchesStatus = when (selectedFilter) {
            "ACTIVE" -> loan.status == "ACTIVE"
            "REPAID" -> loan.status == "REPAID" || loan.status == "CLEARED"
            "OVERDUE" -> loan.status == "OVERDUE" || (loan.status == "ACTIVE" && loan.dueTimestamp < System.currentTimeMillis())
            else -> true
        }

        matchesSearch && matchesStatus
    }

    val totalLoanAmountDisbursed = loans.sumOf { it.principalAmount }
    val totalRepaid = loans.sumOf { it.amountRepaid }
    val totalOutstanding = loans.filter { it.status == "ACTIVE" }.sumOf { it.remainingBalance }
    val averageInterestRate = if (loans.isNotEmpty()) loans.map { it.interestRatePercent }.average() else 10.0

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_loans")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 1. Hero Banner with Table Banking Branding
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("banner_loan_management"),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_loan_management),
                            contentDescription = "Kenyan Chama loan agreement and table banking",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x33000000),
                                            Color(0xF0181502)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0x33FFFFFF)
                                ) {
                                    Text(
                                        text = "FIRESTORE COLLECTION: 'loans'",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD54F),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                // Sync from Firestore button
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0x44FFFFFF),
                                    modifier = Modifier
                                        .clickable { onRefreshFromFirestore() }
                                        .testTag("btn_sync_firestore_loans")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudSync,
                                            contentDescription = "Sync Firestore Loans",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Sync",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Member Loans Portfolio",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Tracking loan amount, interest rate, disbursement date & repayment status.",
                                fontSize = 11.sp,
                                color = Color(0xFFE0E0E0),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 2. Loan Portfolio Metrics Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Disbursed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(formatKSh(totalLoanAmountDisbursed), fontSize = 16.sp, fontWeight = FontWeight.Black)
                            }
                            Column {
                                Text("Avg. Interest", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${String.format(Locale.getDefault(), "%.1f", averageInterestRate)}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ChamaGoldTertiary)
                            }
                            Column {
                                Text("Total Repaid", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(formatKSh(totalRepaid), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ChamaGreenPrimary)
                            }
                            Column {
                                Text("Balance Due", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(formatKSh(totalOutstanding), fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFD32F2F))
                            }
                        }
                    }
                }
            }

            // 3. Search & Repayment Status Filter
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search borrower, purpose, or status...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_search_loans"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        statusTabs.forEach { tab ->
                            val isSelected = selectedFilter == tab
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = tab },
                                label = { Text(tab, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ChamaGoldTertiary.copy(alpha = 0.25f),
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("filter_tab_$tab")
                            )
                        }
                    }
                }
            }

            // 4. Loan Cards List
            if (filteredLoans.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(ChamaGoldTertiary.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = ChamaGoldTertiary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (loans.isEmpty()) "No Member Loans Recorded Yet" else "No Matching Loans Found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (loans.isEmpty())
                                    "Record a table loan for a member with loan amount, interest rate, disbursement date, and status. It will persist directly to the Firestore 'loans' collection."
                                else
                                    "Try clearing your search query or switching to 'ALL' status filter.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onIssueLoanClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ChamaGoldTertiary, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_record_first_loan")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Record Loan in Firestore")
                            }
                        }
                    }
                }
            } else {
                items(filteredLoans, key = { it.id }) { loan ->
                    LoanRecordCard(
                        loan = loan,
                        onRepaymentClick = { onRepaymentClick(loan) },
                        onUpdateStatus = { newStatus -> onUpdateLoanStatus(loan, newStatus) },
                        onDeleteClick = { loanToDelete = loan }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onIssueLoanClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_issue_loan"),
            containerColor = ChamaGoldTertiary,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "Record Member Loan")
        }
    }

    // Delete Loan Confirmation Dialog
    loanToDelete?.let { loan ->
        AlertDialog(
            onDismissRequest = { loanToDelete = null },
            title = { Text("Delete Loan from Firestore?") },
            text = {
                Text("Are you sure you want to permanently delete the loan of ${formatKSh(loan.principalAmount)} for ${loan.memberName} from the Firestore 'loans' collection? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteLoan(loan.id)
                        loanToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete from Firestore")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { loanToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LoanRecordCard(
    loan: LoanEntity,
    onRepaymentClick: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var statusMenuExpanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val disbursementDateStr = dateFormat.format(Date(loan.issuedTimestamp))
    val dueDateStr = dateFormat.format(Date(loan.dueTimestamp))

    val isOverdue = loan.status == "ACTIVE" && loan.dueTimestamp < System.currentTimeMillis()
    val displayStatus = if (isOverdue && loan.status == "ACTIVE") "OVERDUE" else loan.status

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("loan_card_${loan.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Borrower Name, Purpose & Repayment Status Dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(ChamaGoldTertiary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = loan.memberName.take(1).uppercase(),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color(0xFF7A5901)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = loan.memberName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = loan.purpose,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Interactive Repayment Status Badge with Menu
                Box {
                    val (statusBg, statusText) = when (displayStatus) {
                        "REPAID", "CLEARED" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                        "OVERDUE" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                        else -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusBg,
                        modifier = Modifier
                            .clickable { statusMenuExpanded = true }
                            .testTag("status_badge_${loan.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = displayStatus,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusText
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Change Status",
                                tint = statusText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        Text(
                            text = "Set Status (Firestore 'loans'):",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                        listOf("ACTIVE", "REPAID", "OVERDUE").forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s, fontWeight = if (loan.status == s) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    onUpdateStatus(s)
                                    statusMenuExpanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Delete Loan from Firestore", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                statusMenuExpanded = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Core Required Fields in 2x2 Grid:
            // [Loan Amount] & [Interest Rate]
            // [Disbursement Date] & [Repayment Status / Due Date]
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Loan Amount", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                            Text(formatKSh(loan.principalAmount), fontSize = 15.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Interest Rate", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${loan.interestRatePercent.toInt()}% Table Rate",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7A5901)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Disbursement Date", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(12.dp), tint = ChamaGreenPrimary)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(disbursementDateStr, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Repayment Due Date", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                            Text(dueDateStr, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (isOverdue) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar: Paid vs Remaining
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Paid: ${formatKSh(loan.amountRepaid)} of ${formatKSh(loan.totalDue)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(loan.progressFraction * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (loan.remainingBalance <= 0) ChamaGreenPrimary else ChamaGoldTertiary
                    )
                }

                LinearProgressIndicator(
                    progress = { loan.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = if (loan.remainingBalance <= 0) ChamaGreenPrimary else ChamaGoldTertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions & Cloud Indicator Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Synced to Firestore",
                        tint = ChamaGreenPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Firestore: loans/${loan.id}",
                        fontSize = 10.sp,
                        color = ChamaGreenPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (loan.remainingBalance > 0.01) {
                    Button(
                        onClick = onRepaymentClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_repay_loan_${loan.id}")
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Record Repayment", fontSize = 11.sp)
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ChamaGreenPrimary.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ChamaGreenPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fully Settled", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChamaGreenPrimary)
                        }
                    }
                }
            }
        }
    }
}
