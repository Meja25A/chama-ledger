package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContributionEntity
import com.example.data.model.LoanEntity
import com.example.data.model.MemberEntity
import com.example.ui.components.RoleBadge
import com.example.ui.components.formatKSh
import com.example.ui.theme.ChamaGreenPrimary

@Composable
fun MembersScreen(
    members: List<MemberEntity>,
    contributions: List<ContributionEntity>,
    loans: List<LoanEntity>,
    onAddMemberClick: () -> Unit,
    onDeleteMember: (MemberEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredMembers = members.filter {
        searchQuery.isBlank() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery) ||
                it.role.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = modifier.fillMaxSize().testTag("screen_members")) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Summary Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Chama Membership Directory",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${members.size} Registered Table Banking Members",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ChamaGreenPrimary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ChamaGreenPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (members.isEmpty()) " Ready" else " Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChamaGreenPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Search if there are members
            if (members.isNotEmpty()) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name, phone or role") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_search_members"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            // Empty State
            if (members.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
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
                                    .size(60.dp)
                                    .background(ChamaGreenPrimary.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = ChamaGreenPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No Members Registered Yet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Start building your Chama! Add your Chairperson, Treasurer, Secretary, and members to track contributions and loans.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onAddMemberClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Register First Member", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Member List
                items(filteredMembers) { member ->
                    val memberContributions = contributions.filter { it.memberId == member.id }
                    val totalMemberSaved = memberContributions
                        .filter { it.contributionType == "Monthly Shares" }
                        .sumOf { it.amount }
                    val activeMemberLoans = loans.filter { it.memberId == member.id && it.status == "ACTIVE" }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_member_${member.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar Icon
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(ChamaGreenPrimary.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = ChamaGreenPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = member.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        RoleBadge(role = member.role)
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = " ${member.phone} • Joined ${member.joinDate}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onDeleteMember(member) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete Member",
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Stats pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Monthly Shares", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${member.sharesCount} unit (${formatKSh(member.sharesCount * 2000.0)}/mo)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Column {
                                    Text("Total Saved", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(formatKSh(totalMemberSaved), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ChamaGreenPrimary)
                                }
                                Column {
                                    Text("Active Loan", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    val balance = activeMemberLoans.sumOf { it.remainingBalance }
                                    Text(
                                        text = if (balance > 0) formatKSh(balance) else "None",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (balance > 0) Color(0xFFD32F2F) else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to Add Member
        FloatingActionButton(
            onClick = onAddMemberClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_member"),
            containerColor = ChamaGreenPrimary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Member")
        }
    }
}
