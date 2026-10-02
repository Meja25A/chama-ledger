package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContributionEntity
import com.example.data.model.LoanEntity
import com.example.ui.components.ChamaFeatureCards
import com.example.ui.components.ChamaHeroSection
import com.example.ui.components.ChamaStatOverview
import com.example.ui.components.MPesaBadge
import com.example.ui.components.formatDate
import com.example.ui.components.formatKSh
import com.example.ui.theme.ChamaGreenPrimary
import com.example.ui.viewmodel.ChamaFinancialSummary

@Composable
fun DashboardScreen(
    summary: ChamaFinancialSummary,
    recentContributions: List<ContributionEntity>,
    activeLoans: List<LoanEntity>,
    onAddContributionClick: () -> Unit,
    onIssueLoanClick: () -> Unit,
    onNavigateToContributions: () -> Unit,
    onNavigateToLoans: () -> Unit,
    onNavigateToRecords: () -> Unit,
    onNavigateToAiAdvisor: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_dashboard")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 1. Dashboard Hero Section with photorealistic Chama meeting image
        item {
            ChamaHeroSection(
                onAddContributionClick = onAddContributionClick,
                onIssueLoanClick = onIssueLoanClick
            )
        }

        // 2. Chama Financial Overview Statistics
        item {
            ChamaStatOverview(summary = summary)
        }

        // 3. AI Advisor with Google Search Grounding Feature Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToAiAdvisor)
                    .testTag("card_ai_advisor_feature"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFF1B5E20), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CHAMA AI ADVISOR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE8F0FE)
                            ) {
                                Text(
                                    text = "Google Search Live",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A73E8),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Smart Financial Consultation",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Ask questions with Google Search Grounding & multi-turn Gemini reasoning.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open AI Advisor",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 4. AI-Generated Feature Cards (Contributions, Loans, Digital Records)
        item {
            ChamaFeatureCards(
                onNavigateToContributions = onNavigateToContributions,
                onNavigateToLoans = onNavigateToLoans,
                onNavigateToRecords = onNavigateToRecords
            )
        }

        // 4. Recent Contributions Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT CONTRIBUTIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = onNavigateToContributions) {
                    Text("View All (${recentContributions.size})", fontSize = 12.sp)
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }

        if (recentContributions.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No contributions recorded yet", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Record member savings, shares, or welfare to see ledger logs.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        androidx.compose.material3.OutlinedButton(onClick = onAddContributionClick) {
                            Text("+ Record Contribution", fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            items(recentContributions.take(3)) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToContributions),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.memberName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${item.contributionType} • ${formatDate(item.timestamp)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            MPesaBadge(code = item.referenceCode)
                        }

                        Text(
                            text = "+ ${formatKSh(item.amount)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ChamaGreenPrimary
                        )
                    }
                }
            }
        }

        // 5. Active Loans Mini Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE COMMUNITY LOANS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = onNavigateToLoans) {
                    Text("Manage Loans", fontSize = 12.sp)
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }

        if (activeLoans.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No active table loans", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Disburse loans to members at 10% flat interest rate.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        androidx.compose.material3.OutlinedButton(onClick = onIssueLoanClick) {
                            Text("+ Issue Table Loan", fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            items(activeLoans.take(2)) { loan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToLoans),
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = loan.memberName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Balance: ${formatKSh(loan.remainingBalance)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = loan.purpose,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { loan.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            strokeCap = StrokeCap.Round,
                            color = ChamaGreenPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Repaid: ${formatKSh(loan.amountRepaid)} of ${formatKSh(loan.totalDue)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(loan.progressFraction * 100).toInt()}% paid",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChamaGreenPrimary
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
