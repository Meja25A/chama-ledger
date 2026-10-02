package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ChamaGoldTertiary
import com.example.ui.theme.ChamaGreenPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChamaHeroSection(
    onAddContributionClick: () -> Unit,
    onIssueLoanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_hero_section"),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
        ) {
            // Background Photorealistic Chama Group Image
            Image(
                painter = painterResource(id = R.drawable.img_chama_group),
                contentDescription = "Kenyan Chama members during a table banking meeting",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // High-contrast gradient overlay to ensure text readability while keeping the authentic photo visible
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x660B240E),
                                Color(0xAA081A0B),
                                Color(0xF2071609)
                            )
                        )
                    )
            )

            // Content layer
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Tag & Title
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0x33FFFFFF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = " KENYAN TABLE BANKING",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F),
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "CHAMA LEDGER",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.2.sp
                    )

                    Text(
                        text = "Digital Table Banking",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFCA28)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Manage members, contributions, loans and payouts in one simple digital ledger.",
                        fontSize = 13.sp,
                        color = Color(0xFFE0E0E0),
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }

                // Action Buttons requested by user: [Add Contribution] [Issue Loan]
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ElevatedButton(
                        onClick = onAddContributionClick,
                        modifier = Modifier.testTag("btn_hero_add_contribution"),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = ChamaGreenPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "Add Contribution",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    ElevatedButton(
                        onClick = onIssueLoanClick,
                        modifier = Modifier.testTag("btn_hero_issue_loan"),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = ChamaGoldTertiary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "Issue Loan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
