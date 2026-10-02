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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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

@Composable
fun AddContributionDialog(
    members: List<MemberEntity>,
    onDismiss: () -> Unit,
    onConfirm: (memberId: Long, memberName: String, amount: Double, type: String, method: String, refCode: String, notes: String) -> Unit
) {
    var selectedMember by remember { mutableStateOf(members.firstOrNull()) }
    var customMemberName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("2000") }
    var selectedType by remember { mutableStateOf("Monthly Shares") }
    var selectedMethod by remember { mutableStateOf("M-Pesa") }
    var refCode by remember {
        mutableStateOf("QK" + (100..999).random() + ('A'..'Z').random() + ('A'..'Z').random() + (10..99).random())
    }
    var notes by remember { mutableStateOf("") }

    var memberMenuExpanded by remember { mutableStateOf(false) }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    var methodMenuExpanded by remember { mutableStateOf(false) }

    val contributionTypes = listOf(
        "Monthly Shares",
        "Welfare & Emergency",
        "Merry-Go-Round Pot",
        "Meeting Fine",
        "Registration Fee"
    )
    val paymentMethods = listOf("M-Pesa", "Cash", "Equity Bank", "KCB")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Record Contribution", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                // Member Selector or Input
                if (members.isNotEmpty()) {
                    Box {
                        OutlinedTextField(
                            value = selectedMember?.name ?: customMemberName.ifBlank { "Select Member" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Chama Member") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { memberMenuExpanded = true }
                                .testTag("input_contribution_member")
                        )
                        DropdownMenu(
                            expanded = memberMenuExpanded,
                            onDismissRequest = { memberMenuExpanded = false }
                        ) {
                            members.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text("${m.name} (${m.role})") },
                                    onClick = {
                                        selectedMember = m
                                        customMemberName = ""
                                        memberMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = customMemberName,
                        onValueChange = { customMemberName = it },
                        label = { Text("Member Name") },
                        placeholder = { Text("e.g. Mary Wambui") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_contribution_custom_member"),
                        supportingText = {
                            Text("No members in database yet. Type name to auto-register.", fontSize = 11.sp)
                        }
                    )
                }

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Amount (KSh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_contribution_amount")
                )

                // Quick amount chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("1000", "2000", "4000", "6000").forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (amountText == preset) ChamaGreenPrimary else Color(0xFFEEEEEE),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { amountText = preset }
                        ) {
                            Text(
                                text = "KSh $preset",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (amountText == preset) Color.White else Color.Black,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Type Selector
                Box {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Contribution Type") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { typeMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = typeMenuExpanded,
                        onDismissRequest = { typeMenuExpanded = false }
                    ) {
                        contributionTypes.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    selectedType = t
                                    typeMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Payment Method
                Box {
                    OutlinedTextField(
                        value = selectedMethod,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Method") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { methodMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = methodMenuExpanded,
                        onDismissRequest = { methodMenuExpanded = false }
                    ) {
                        paymentMethods.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    selectedMethod = p
                                    methodMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Reference Code (M-Pesa)
                OutlinedTextField(
                    value = refCode,
                    onValueChange = { refCode = it.uppercase() },
                    label = { Text("Transaction Reference (e.g. M-Pesa Code)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Optional Notes") },
                    placeholder = { Text("e.g. Table meeting payment") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    val memberName = selectedMember?.name ?: customMemberName.trim()
                    val memberId = selectedMember?.id ?: 0L

                    if (amount > 0 && memberName.isNotBlank()) {
                        onConfirm(
                            memberId,
                            memberName,
                            amount,
                            selectedType,
                            selectedMethod,
                            refCode,
                            notes
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                modifier = Modifier.testTag("btn_confirm_contribution")
            ) {
                Text("Save Contribution")
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
fun IssueLoanDialog(
    members: List<MemberEntity>,
    onDismiss: () -> Unit,
    onConfirm: (memberId: Long, memberName: String, principal: Double, interest: Double, months: Int, purpose: String, guarantor: String) -> Unit
) {
    var selectedMember by remember { mutableStateOf(members.firstOrNull()) }
    var customBorrowerName by remember { mutableStateOf("") }
    var principalText by remember { mutableStateOf("20000") }
    var interestRate by remember { mutableDoubleStateOf(10.0) }
    var durationMonths by remember { mutableIntStateOf(3) }
    var purpose by remember { mutableStateOf("Agribusiness supplies & inputs") }
    var guarantor by remember { mutableStateOf("Committee Guarantee") }
    var memberMenuExpanded by remember { mutableStateOf(false) }

    val principal = principalText.toDoubleOrNull() ?: 0.0
    val totalDue = principal + (principal * (interestRate / 100.0))
    val monthlyInstallment = if (durationMonths > 0) totalDue / durationMonths else totalDue

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Issue Table Loan", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                // Borrower Member
                if (members.isNotEmpty()) {
                    Box {
                        OutlinedTextField(
                            value = selectedMember?.name ?: customBorrowerName.ifBlank { "Select Borrower" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Borrower") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { memberMenuExpanded = true }
                                .testTag("input_loan_member")
                        )
                        DropdownMenu(
                            expanded = memberMenuExpanded,
                            onDismissRequest = { memberMenuExpanded = false }
                        ) {
                            members.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.name) },
                                    onClick = {
                                        selectedMember = m
                                        customBorrowerName = ""
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
                        label = { Text("Borrower Name") },
                        placeholder = { Text("e.g. John Kamau") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_loan_custom_borrower"),
                        supportingText = {
                            Text("No members in database yet. Type name to auto-register.", fontSize = 11.sp)
                        }
                    )
                }

                // Principal
                OutlinedTextField(
                    value = principalText,
                    onValueChange = { principalText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Loan Principal (KSh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_loan_principal")
                )

                // Quick preset amounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("10000", "20000", "30000", "50000").forEach { p ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (principalText == p) ChamaGoldTertiary else Color(0xFFEEEEEE),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { principalText = p }
                        ) {
                            Text(
                                text = "KSh ${p.toInt() / 1000}k",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (principalText == p) Color.White else Color.Black,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Duration in Months
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Duration:", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    listOf(1, 2, 3, 6).forEach { m ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (durationMonths == m) ChamaGreenPrimary else Color(0xFFEEEEEE),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { durationMonths = m }
                        ) {
                            Text(
                                text = "${m}mo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (durationMonths == m) Color.White else Color.Black,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Loan Calculation Summary Box
                Surface(
                    color = Color(0xFFF1F8E9),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Chama Interest Rate:", fontSize = 12.sp, color = Color(0xFF33691E))
                            Text("$interestRate% flat", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF33691E))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Repayable:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(formatKSh(totalDue), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ChamaGreenPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Monthly Installment:", fontSize = 12.sp, color = Color(0xFF555555))
                            Text(formatKSh(monthlyInstallment) + " / month", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Purpose
                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("Loan Purpose") },
                    placeholder = { Text("e.g. Small business inventory") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Guarantor
                OutlinedTextField(
                    value = guarantor,
                    onValueChange = { guarantor = it },
                    label = { Text("Guarantor Member(s)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val borrowerName = selectedMember?.name ?: customBorrowerName.trim()
                    val borrowerId = selectedMember?.id ?: 0L

                    if (principal > 0 && borrowerName.isNotBlank()) {
                        onConfirm(borrowerId, borrowerName, principal, interestRate, durationMonths, purpose, guarantor)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChamaGoldTertiary),
                modifier = Modifier.testTag("btn_confirm_issue_loan")
            ) {
                Text("Disburse Loan")
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
fun RecordRepaymentDialog(
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
                Text("Record Loan Repayment", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                // Loan Info Card
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = loan.memberName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Purpose: ${loan.purpose}",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Due:", fontSize = 12.sp)
                            Text(formatKSh(loan.totalDue), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Paid so far:", fontSize = 12.sp)
                            Text(formatKSh(loan.amountRepaid), fontSize = 12.sp, color = ChamaGreenPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Remaining Balance:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                formatKSh(loan.remainingBalance),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                }

                // Repayment Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Repayment Amount (KSh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_repayment_amount")
                )

                // Quick buttons: Pay Half, Pay Full
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val half = (loan.remainingBalance / 2).toInt()
                    val full = loan.remainingBalance.toInt()

                    OutlinedButton(
                        onClick = { amountText = half.toString() },
                        modifier = Modifier.weight(1f)
                    ) {
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

                // M-Pesa / Reference Code
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
                    label = { Text("Repayment Remarks") },
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
                modifier = Modifier.testTag("btn_confirm_repayment")
            ) {
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

@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, status: String, idNumber: String, role: String, shares: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("07") }
    var status by remember { mutableStateOf("Active") }
    var idNumber by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Member") }
    var shares by remember { mutableIntStateOf(1) }
    var roleMenuExpanded by remember { mutableStateOf(false) }

    val roles = listOf("Member", "Chairperson", "Treasurer", "Secretary", "Vice Chair")
    val statusOptions = listOf("Active", "Pending", "Suspended", "Dormant")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Add Group Member", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Persists to Cloud Firestore & Local DB", fontSize = 11.sp, color = ChamaGreenPrimary)
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
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    placeholder = { Text("e.g. Faith Nyambura") },
                    modifier = Modifier.fillMaxWidth().testTag("input_member_name")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number (M-Pesa) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("input_member_phone")
                )

                // Member Status Selector
                Column {
                    Text(
                        text = "Member Status (Firestore):",
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
                            val isSelected = status == st
                            val (chipBg, chipText) = when (st) {
                                "Active" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                                "Pending" -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
                                "Suspended" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                                else -> Color(0xFFEEEEEE) to Color(0xFF616161)
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { status = st },
                                label = { Text(st, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = chipBg,
                                    selectedLabelColor = chipText
                                ),
                                modifier = Modifier.weight(1f).testTag("chip_status_$st")
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = idNumber,
                    onValueChange = { idNumber = it.filter { ch -> ch.isDigit() } },
                    label = { Text("National ID Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Role
                Box {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chama Role") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { roleMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = roleMenuExpanded,
                        onDismissRequest = { roleMenuExpanded = false }
                    ) {
                        roles.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r) },
                                onClick = {
                                    role = r
                                    roleMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Shares
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Monthly Shares (Units):", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 2, 3, 5).forEach { s ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (shares == s) ChamaGreenPrimary else Color(0xFFEEEEEE),
                                modifier = Modifier.clickable { shares = s }
                            ) {
                                Text(
                                    text = "$s unit",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (shares == s) Color.White else Color.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), phone.trim(), status, idNumber.trim(), role, shares)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                modifier = Modifier.testTag("btn_confirm_add_member")
            ) {
                Text("Register Member")
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
fun AddPayoutDialog(
    members: List<MemberEntity>,
    nextCycleNumber: Int,
    onDismiss: () -> Unit,
    onConfirm: (cycleNumber: Int, recipientId: Long, recipientName: String, amount: Double, scheduledDate: String, notes: String) -> Unit
) {
    var cycleText by remember { mutableStateOf(nextCycleNumber.toString()) }
    var selectedMember by remember { mutableStateOf(members.firstOrNull()) }
    var customRecipientName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("30000") }
    var scheduledDate by remember { mutableStateOf("15 Nov 2026") }
    var notes by remember { mutableStateOf("Rotational merry-go-round cycle") }
    var memberMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Schedule Rotational Payout", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                OutlinedTextField(
                    value = cycleText,
                    onValueChange = { cycleText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Cycle Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                if (members.isNotEmpty()) {
                    Box {
                        OutlinedTextField(
                            value = selectedMember?.name ?: customRecipientName.ifBlank { "Select Recipient" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Recipient Member") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { memberMenuExpanded = true }
                        )
                        DropdownMenu(
                            expanded = memberMenuExpanded,
                            onDismissRequest = { memberMenuExpanded = false }
                        ) {
                            members.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.name) },
                                    onClick = {
                                        selectedMember = m
                                        customRecipientName = ""
                                        memberMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = customRecipientName,
                        onValueChange = { customRecipientName = it },
                        label = { Text("Recipient Member Name") },
                        placeholder = { Text("e.g. Grace Achieng") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Pot Amount (KSh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = scheduledDate,
                    onValueChange = { scheduledDate = it },
                    label = { Text("Scheduled Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Rotation Rules") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cycle = cycleText.toIntOrNull() ?: nextCycleNumber
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    val recipientName = selectedMember?.name ?: customRecipientName.trim()
                    val recipientId = selectedMember?.id ?: 0L

                    if (amount > 0 && recipientName.isNotBlank()) {
                        onConfirm(cycle, recipientId, recipientName, amount, scheduledDate, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary)
            ) {
                Text("Schedule Payout")
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
fun AddMeetingDialog(
    members: List<MemberEntity>,
    onDismiss: () -> Unit,
    onConfirm: (date: String, venue: String, attendance: Int, savings: Double, loans: Double, recipient: String, notes: String) -> Unit
) {
    var date by remember { mutableStateOf("05 Nov 2026") }
    var venue by remember { mutableStateOf("Community Hall & Table Banking") }
    var attendanceText by remember { mutableStateOf("6") }
    var savingsText by remember { mutableStateOf("20000") }
    var loansText by remember { mutableStateOf("15000") }
    var recipient by remember { mutableStateOf(members.firstOrNull()?.name ?: "All Members") }
    var notes by remember { mutableStateOf("Confirmed monthly contributions and table loans.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Record Meeting Session", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Meeting Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Meeting Venue") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = attendanceText,
                    onValueChange = { attendanceText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Attendance Count") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = savingsText,
                    onValueChange = { savingsText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Total Savings Collected (KSh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = loansText,
                    onValueChange = { loansText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Total Loans Disbursed (KSh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("Merry-Go-Round Pot Winner") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Key Decisions & Minutes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val attendance = attendanceText.toIntOrNull() ?: 0
                    val savings = savingsText.toDoubleOrNull() ?: 0.0
                    val loans = loansText.toDoubleOrNull() ?: 0.0
                    onConfirm(date, venue, attendance, savings, loans, recipient, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary)
            ) {
                Text("Save Meeting Minutes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
