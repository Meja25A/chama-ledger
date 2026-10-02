package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MPesaGreen
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatKSh(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    formatter.maximumFractionDigits = 0
    formatter.minimumFractionDigits = 0
    return "KSh ${formatter.format(amount)}"
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun MPesaBadge(
    code: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = MPesaGreen.copy(alpha = 0.12f),
        contentColor = MPesaGreen
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MPesaGreen)
            )
            Text(
                text = " M-PESA: $code",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MPesaGreen
            )
        }
    }
}

@Composable
fun RoleBadge(
    role: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (role) {
        "Chairperson" -> Pair(Color(0xFFE8F5E9), Color(0xFF1B5E20))
        "Treasurer" -> Pair(Color(0xFFFFF8E1), Color(0xFFB78103))
        "Secretary" -> Pair(Color(0xFFE0F2F1), Color(0xFF00796B))
        else -> Pair(Color(0xFFF1F3F4), Color(0xFF424242))
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Text(
            text = role,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun LoanStatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, fgColor, icon) = when (status) {
        "ACTIVE" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), Icons.Default.Schedule)
        "REPAID" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Default.CheckCircle)
        "OVERDUE" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Icons.Default.Warning)
        else -> Triple(Color(0xFFECEFF1), Color(0xFF455A64), Icons.Default.Info)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fgColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = " $status",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = fgColor
            )
        }
    }
}
