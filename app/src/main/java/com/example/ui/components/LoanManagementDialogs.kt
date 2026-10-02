package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LoanEntity
import com.example.data.model.MemberEntity
import com.example.ui.theme.ChamaGoldTertiary
import com.example.ui.theme.ChamaGreenPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun RecordLoanDialog(
    members: List<MemberEntity>,
    onDismiss: () -> Unit,
    onConfirm: (
        memberId: Long,
        memberName: String,
        loanAmount: Double,
        interestRate: Double,
        disbursementDate: String,
        disbursementTimestamp: Long,
        durationMonths: Int,
        repaymentStatus: String,
        purpose: String,
        guarantor: String
    ) -> Unit
) {
    var selectedMember by remember { mutableStateOf(members.firstOrNull()) }
    var customBorrowerName by remember { mutableStateOf("") }
    var loanAmountText by remember { mutableStateOf("20000") }
    var interestRate by remember { mutableDoubleStateOf(10.0) }
    var durationMonths by remember { mutableIntStateOf(3) }
    var repaymentStatus by remember { mutableStateOf("ACTIVE") }
    var purpose by remember { mutableStateOf("Agribusiness supplies & inputs") }
    var guarantor by remember { mutableStateOf("Executive Committee Guarantee") }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    var disbursementDateStr by remember { mutableStateOf(dateFormat.format(Date())) }
    var disbursementTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }

    var memberMenuExpanded by remember { mutableStateOf(false) }

    val interestPresets = listOf(5.0, 8.0, 10.0, 12.0, 15.0)
    val statusOptions = listOf("ACTIVE", "REPAID", "OVERDUE")
    val durationOptions = listOf(1, 2, 3, 6, 12)

    val loanAmount = loanAmountText.toDoubleOrNull() ?: 0.0
    val totalInterest = loanAmount * (interestRate / 100.0)
    val totalDue = loanAmount + totalInterest
    val monthlyInstallment = if (durationMonths > 0) totalDue / durationMonths else totalDue

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Record Member Loan", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(
                        text = "Persisting to Firestore 'loans' collection",
                        fontSize = 11.sp,
                        color = ChamaGoldTertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Borrower Member Picker
                if (members.isNotEmpty()) {
                    Box {
                        OutlinedTextField(
                            value = selectedMember?.name ?: customBorrowerName.ifBlank { "Select Borrower Member" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Borrower Member *") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ChamaGreenPrimary) },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { memberMenuExpanded = true }
                                .testTag("select_borrower_dropdown")
                        )
                        DropdownMenu(
                            expanded = memberMenuExpanded,
                            onDismissRequest = { memberMenuExpanded = false }
                        ) {
                            members.forEach { m ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(m.name, fontWeight = FontWeight.Bold)
                                            Text("${m.phone} • Status: ${m.status}", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        selectedMember = m
                                        memberMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = customBorrowerName,
                        onValueChange = { customBorrowerName = it },
                        label = { Text("Borrower Full Name *") },
                        placeholder = { Text("e.g. David Kiprono") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ChamaGreenPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("input_borrower_name")
                    )
                }

                // 2. Loan Amount (KSh)
                OutlinedTextField(
                    value = loanAmountText,
                    onValueChange = { loanAmountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Loan Amount (KSh) *") },
                    placeholder = { Text("e.g. 20000") },
                    leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = ChamaGoldTertiary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_loan_amount")
                )

                // Quick preset amounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5000, 10000, 20000, 50000).forEach { amt ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (loanAmount.toInt() == amt) ChamaGoldTertiary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { loanAmountText = amt.toString() }
                                .weight(1f)
                        ) {
                            Text(
                                text = "${amt / 1000}k",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (loanAmount.toInt() == amt) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // 3. Interest Rate (%)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interest Rate: ${interestRate.toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Standard Table Banking: 10%",
                            fontSize = 10.sp,
                            color = ChamaGreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        interestPresets.forEach { rate ->
                            val isSelected = interestRate == rate
                            FilterChip(
                                selected = isSelected,
                                onClick = { interestRate = rate },
                                label = { Text("${rate.toInt()}%", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ChamaGoldTertiary,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 4. Disbursement Date
                OutlinedTextField(
                    value = disbursementDateStr,
                    onValueChange = {
                        disbursementDateStr = it
                        try {
                            val parsed = dateFormat.parse(it)
                            if (parsed != null) disbursementTimestamp = parsed.time
                        } catch (e: Exception) {
                            // Keep current timestamp
                        }
                    },
                    label = { Text("Disbursement Date *") },
                    placeholder = { Text("e.g. 02 Oct 2026") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = ChamaGreenPrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_disbursement_date")
                )

                // 5. Loan Duration (Months)
                Column {
                    Text(
                        text = "Loan Duration (Months): $durationMonths Months",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        durationOptions.forEach { m ->
                            val isSelected = durationMonths == m
                            FilterChip(
                                selected = isSelected,
                                onClick = { durationMonths = m },
                                label = { Text("${m}M", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ChamaGreenPrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = ChamaGreenPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 6. Repayment Status
                Column {
                    Text(
                        text = "Repayment Status (Firestore):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        statusOptions.forEach { st ->
                            val isSelected = repaymentStatus == st
                            val (chipBg, chipText) = when (st) {
                                "ACTIVE" -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
                                "REPAID" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                                else -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { repaymentStatus = st },
                                label = { Text(st, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = chipBg,
                                    selectedLabelColor = chipText
                                ),
                                modifier = Modifier.weight(1f).testTag("chip_loan_status_$st")
                            )
                        }
                    }
                }

                // 7. Purpose & Guarantor
                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("Loan Purpose") },
                    placeholder = { Text("e.g. Agribusiness supplies, stock, tuition") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_loan_purpose")
                )

                OutlinedTextField(
                    value = guarantor,
                    onValueChange = { guarantor = it },
                    label = { Text("Guarantor / Committee Member") },
                    placeholder = { Text("e.g. Committee Guarantee or Member Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 8. Live Calculation Summary Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "LOAN TERMS SUMMARY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = ChamaGreenPrimary
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Principal Loan Amount:", fontSize = 12.sp)
                            Text(formatKSh(loanAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Interest (${interestRate.toInt()}%):", fontSize = 12.sp)
                            Text(formatKSh(totalInterest), fontSize = 12.sp, color = ChamaGoldTertiary, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Repayment Due:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(formatKSh(totalDue), fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFFD32F2F))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Monthly Installment (${durationMonths} mo):", fontSize = 11.sp, color = Color.Gray)
                            Text(formatKSh(monthlyInstallment), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChamaGreenPrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val borrowerName = selectedMember?.name ?: customBorrowerName.trim()
                    val borrowerId = selectedMember?.id ?: 0L

                    if (loanAmount > 0 && borrowerName.isNotBlank()) {
                        onConfirm(
                            borrowerId,
                            borrowerName,
                            loanAmount,
                            interestRate,
                            disbursementDateStr,
                            disbursementTimestamp,
                            durationMonths,
                            repaymentStatus,
                            purpose,
                            guarantor
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChamaGoldTertiary, contentColor = Color.Black),
                modifier = Modifier.testTag("btn_save_loan_firestore")
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save to Firestore 'loans'")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RecordLoanRepaymentDialog(
    loan: LoanEntity,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, method: String, refCode: String, notes: String) -> Unit
) {
    var amountText by remember { mutableStateOf(loan.remainingBalance.toInt().toString()) }
    var selectedMethod by remember { mutableStateOf("M-Pesa") }
    var refCode by remember {
        mutableStateOf("REP" + (100..999).random() + ('A'..'Z').random() + (10..99).random())
    }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Record Loan Repayment", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Updates Firestore 'loans' collection", fontSize = 11.sp, color = ChamaGreenPrimary)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = loan.memberName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "Purpose: ${loan.purpose}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Due:", fontSize = 12.sp)
                            Text(formatKSh(loan.totalDue), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Paid So Far:", fontSize = 12.sp)
                            Text(formatKSh(loan.amountRepaid), fontSize = 12.sp, color = ChamaGreenPrimary, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Remaining Balance:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                formatKSh(loan.remainingBalance),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                }

                // Repayment Amount Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Repayment Amount (KSh) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_repayment_amount")
                )

                // Quick buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val half = (loan.remainingBalance / 2).toInt()
                    val full = loan.remainingBalance.toInt()

                    OutlinedButton(onClick = { amountText = half.toString() }, modifier = Modifier.weight(1f)) {
                        Text("Pay Half ($half)", fontSize = 11.sp)
                    }
                    Button(
                        onClick = { amountText = full.toString() },
                        colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pay Full ($full)", fontSize = 11.sp)
                    }
                }

                // Reference
                OutlinedTextField(
                    value = refCode,
                    onValueChange = { refCode = it.uppercase() },
                    label = { Text("M-Pesa Reference Code") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Remarks (Optional)") },
                    placeholder = { Text("e.g. Table meeting cash or M-Pesa transfer") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(amount, selectedMethod, refCode, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                modifier = Modifier.testTag("btn_confirm_loan_repayment")
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Confirm Repayment")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
