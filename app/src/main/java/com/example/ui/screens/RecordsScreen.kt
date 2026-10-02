package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MeetingRecordEntity
import com.example.data.model.PayoutEntity
import com.example.ui.components.formatKSh
import com.example.ui.theme.ChamaGoldTertiary
import com.example.ui.theme.ChamaGreenPrimary

@Composable
fun RecordsScreen(
    payouts: List<PayoutEntity>,
    meetings: List<MeetingRecordEntity>,
    onTogglePayoutStatus: (PayoutEntity) -> Unit,
    onAddMeetingClick: () -> Unit,
    onAddPayoutClick: () -> Unit = {},
    onDeletePayout: (Long) -> Unit = {},
    onDeleteMeeting: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().testTag("screen_digital_records")) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 1. Digital Chama Header Banner
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("banner_digital_chama"),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_digital_chama),
                            contentDescription = "Kenyan Chama treasurer managing digital financial records on laptop",
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
                                            Color(0xBB081A0B),
                                            Color(0xF0071609)
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
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x33FFFFFF)
                            ) {
                                Text(
                                    text = "KENYAN MICROFINANCE GOVERNANCE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Digital Records & Meetings",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Replace paper ledgers with transparent digital records & rotational payouts.",
                                fontSize = 12.sp,
                                color = Color(0xFFE0E0E0)
                            )
                        }
                    }
                }
            }

            // 2. Merry-Go-Round Rotational Payouts Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "MERRY-GO-ROUND POT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onAddPayoutClick,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Pot", fontSize = 11.sp)
                    }
                }
            }

            if (payouts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Rotational Payouts Scheduled",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Set up rotation cycles to ensure every member receives the monthly table banking pot.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onAddPayoutClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Schedule First Payout", fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                items(payouts) { payout ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_payout_${payout.cycleNumber}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Cycle #${payout.cycleNumber}: ${payout.recipientName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Scheduled: ${payout.scheduledDate} • ${payout.notes}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatKSh(payout.amount),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = ChamaGreenPrimary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = { onTogglePayoutStatus(payout) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (payout.status == "COMPLETED") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                        contentColor = if (payout.status == "COMPLETED") Color(0xFF2E7D32) else Color(0xFFE65100)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (payout.status == "COMPLETED") "Disbursed" else "Pending Disburse",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { onDeletePayout(payout.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Past Meeting Records & Minutes
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.EventNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "TABLE BANKING MEETING LOGS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                }
            }

            if (meetings.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Meeting Minutes Logged",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Record attendance, table collections, loans approved, and key committee decisions.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onAddMeetingClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Record First Meeting", fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                items(meetings) { meeting ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_meeting_${meeting.id}"),
                        shape = RoundedCornerShape(12.dp),
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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = meeting.meetingDate,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "${meeting.attendanceCount} present",
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { onDeleteMeeting(meeting.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Venue: ${meeting.venue}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Savings Collected", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(formatKSh(meeting.totalSavingsCollected), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ChamaGreenPrimary)
                                }
                                Column {
                                    Text("Loans Disbursed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(formatKSh(meeting.totalLoansDisbursed), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ChamaGoldTertiary)
                                }
                                Column {
                                    Text("Pot Recipient", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(meeting.merryGoRoundWinner, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            if (meeting.keyDecisions.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Gavel,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = meeting.keyDecisions,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
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

        // FAB to Log Meeting
        FloatingActionButton(
            onClick = onAddMeetingClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_meeting"),
            containerColor = ChamaGreenPrimary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Log Meeting")
        }
    }
}
