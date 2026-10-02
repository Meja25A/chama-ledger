package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
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
import com.example.data.model.ContributionEntity
import com.example.ui.components.MPesaBadge
import com.example.ui.components.formatDate
import com.example.ui.components.formatKSh
import com.example.ui.theme.ChamaGreenPrimary

import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton

@Composable
fun ContributionsScreen(
    contributions: List<ContributionEntity>,
    onAddContributionClick: () -> Unit,
    onDeleteContribution: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filterOptions = listOf("All", "Monthly Shares", "Welfare & Emergency", "Loan Repayment")

    val filteredList = contributions.filter { item ->
        val matchesFilter = (selectedFilter == "All" || item.contributionType == selectedFilter)
        val matchesSearch = searchQuery.isBlank() ||
                item.memberName.contains(searchQuery, ignoreCase = true) ||
                item.referenceCode.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    val totalSum = filteredList.sumOf { it.amount }

    Box(modifier = modifier.fillMaxSize().testTag("screen_contributions")) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 1. AI-Generated Financial Records Header Banner
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("banner_financial_records"),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_financial_records),
                            contentDescription = "Kenyan Chama financial records and ledger notebook",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x55000000),
                                            Color(0xDF09250D)
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
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x33FFFFFF)
                            ) {
                                Text(
                                    text = "KENYAN FINANCIAL RECORDS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Savings & Contribution Ledger",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Track monthly shares, welfare kitty, and real-time M-Pesa receipts.",
                                fontSize = 12.sp,
                                color = Color(0xFFE0E0E0)
                            )
                        }
                    }
                }
            }

            // 2. Aggregate Filter Summary Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (selectedFilter == "All") "Total Recorded Funds" else "$selectedFilter Total",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatKSh(totalSum),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChamaGreenPrimary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "${filteredList.size} entries",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // 3. Search and Type Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by member name or M-Pesa code") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_search_contributions"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filterOptions.forEach { filter ->
                            InputChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // 4. Contribution Items
            if (contributions.isEmpty()) {
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
                                    .background(ChamaGreenPrimary.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = ChamaGreenPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Contributions Recorded Yet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Start tracking table banking savings! Tap below to record monthly shares, emergency welfare, or cash pot collections.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onAddContributionClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Record First Contribution", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No contributions match current filter.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("item_contribution_${item.id}"),
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.memberName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = item.contributionType,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = formatKSh(item.amount),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = ChamaGreenPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { onDeleteContribution(item.id) },
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

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MPesaBadge(code = item.referenceCode)
                                Text(
                                    text = formatDate(item.timestamp),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (item.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Note: ${item.notes}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to Add Contribution
        FloatingActionButton(
            onClick = onAddContributionClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_contribution"),
            containerColor = ChamaGreenPrimary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Record Contribution")
        }
    }
}
