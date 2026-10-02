package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ChamaGreenPrimary

@Composable
fun ChamaFeatureCards(
    onNavigateToContributions: () -> Unit,
    onNavigateToLoans: () -> Unit,
    onNavigateToRecords: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "CHAMA OPERATIONS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        // Card 1: Contributions
        FeatureCardItem(
            imageRes = R.drawable.img_financial_records,
            title = "Contributions",
            description = "Track every member's monthly savings, shares, and welfare records.",
            tagText = "LEDGER & M-PESA",
            testTag = "card_contributions",
            onClick = onNavigateToContributions
        )

        // Card 2: Loans
        FeatureCardItem(
            imageRes = R.drawable.img_loan_management,
            title = "Loans",
            description = "Monitor issued loans, repayments and outstanding balances.",
            tagText = "MICROFINANCE",
            testTag = "card_loans",
            onClick = onNavigateToLoans
        )

        // Card 3: Digital Records
        FeatureCardItem(
            imageRes = R.drawable.img_digital_chama,
            title = "Digital Records",
            description = "Replace paper-based Chama records with organized digital records.",
            tagText = "TABLE BANKING",
            testTag = "card_digital_records",
            onClick = onNavigateToRecords
        )
    }
}

@Composable
private fun FeatureCardItem(
    imageRes: Int,
    title: String,
    description: String,
    tagText: String,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // AI-Generated Photograph Thumbnail
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "$title photograph",
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tagText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChamaGreenPrimary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open $title",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
