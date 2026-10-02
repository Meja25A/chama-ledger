package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ChamaFinancialSummary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChamaStatOverview(
    summary: ChamaFinancialSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Total Savings",
                value = formatKSh(summary.totalSavings),
                subtitle = "Monthly Shares",
                icon = Icons.Default.AccountBalance,
                iconTint = Color(0xFF1B5E20),
                iconBg = Color(0xFFE8F5E9),
                modifier = Modifier.weight(1f),
                testTag = "stat_total_savings"
            )

            StatCard(
                title = "Active Loans",
                value = formatKSh(summary.activeLoansBalance),
                subtitle = "${summary.totalLoansCount} outstanding",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconTint = Color(0xFFE65100),
                iconBg = Color(0xFFFFF3E0),
                modifier = Modifier.weight(1f),
                testTag = "stat_active_loans"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Welfare Kitty",
                value = formatKSh(summary.welfareFund),
                subtitle = "Emergency reserve",
                icon = Icons.Default.HealthAndSafety,
                iconTint = Color(0xFF00796B),
                iconBg = Color(0xFFE0F2F1),
                modifier = Modifier.weight(1f),
                testTag = "stat_welfare_fund"
            )

            StatCard(
                title = "Chama Members",
                value = "${summary.totalMembers} active",
                subtitle = "Table banking group",
                icon = Icons.Default.Group,
                iconTint = Color(0xFF5E35B1),
                iconBg = Color(0xFFEDE7F6),
                modifier = Modifier.weight(1f),
                testTag = "stat_members_count"
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
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
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
