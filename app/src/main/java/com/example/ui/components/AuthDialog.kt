package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.UserProfile
import com.example.ui.theme.ChamaGreenPrimary

@Composable
fun AuthDialog(
    userProfile: UserProfile?,
    syncStatus: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInDemo: (name: String, email: String) -> Unit,
    onSignOut: () -> Unit,
    onSyncAllToFirestore: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ChamaGreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Firebase & Google Auth", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Profile Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(ChamaGreenPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = userProfile?.displayName ?: "Not Authenticated",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = userProfile?.email ?: "Guest Mode • Local Room DB",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        // Firestore status indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = ChamaGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Firestore Sync:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = syncStatus,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChamaGreenPrimary
                            )
                        }
                    }
                }

                // If loading
                if (isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Authenticating with Firebase...", fontSize = 12.sp)
                    }
                }

                // Actions
                if (userProfile == null) {
                    Button(
                        onClick = onSignInWithGoogle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_google_sign_in"),
                        colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Sign In with Google (Firebase Auth)", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onSignInDemo("Mejja (Chama Admin)", "emdymejja@gmail.com")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_signin_mejja"),
                        colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Sign In as Mejja (emdymejja@gmail.com)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Text(
                        text = "Or choose sign-in method:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onSignInDemo("Wanjiku Mwangi (Chair)", "wanjiku@chama.ke")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Chairperson", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                onSignInDemo("Brian Ochieng (Treasurer)", "treasurer@chama.ke")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Treasurer", fontSize = 11.sp)
                        }
                    }
                } else {
                    Button(
                        onClick = onSyncAllToFirestore,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ChamaGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sync All Data to Firestore Cloud", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sign Out from Firebase")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
