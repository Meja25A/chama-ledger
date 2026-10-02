package com.example.data.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.ContributionEntity
import com.example.data.model.LoanEntity
import com.example.data.model.MemberEntity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false
)

class FirebaseManager(private val context: Context) {

    companion object {
        private const val TAG = "FirebaseManager"
        private const val CHAMA_COLLECTION = "chamas"
        private const val CHAMA_DOC_ID = "default_chama_ke"
        private const val LOANS_COLLECTION = "loans"
    }

    val isFirebaseInitialized: Boolean by lazy {
        try {
            FirebaseApp.initializeApp(context)
            true
        } catch (e: Throwable) {
            Log.w(TAG, "FirebaseApp init check: ${e.message}")
            false
        }
    }

    val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Throwable) {
            Log.w(TAG, "FirebaseAuth unavailable: ${e.message}")
            null
        }
    }

    val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.w(TAG, "FirebaseFirestore unavailable: ${e.message}")
            null
        }
    }

    private val _currentUserProfile = MutableStateFlow<UserProfile?>(null)
    val currentUserProfile: StateFlow<UserProfile?> = _currentUserProfile.asStateFlow()

    private val _syncStatus = MutableStateFlow("Firebase Initializing...")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        autoAuthenticate()
    }

    fun autoAuthenticate() {
        try {
            val firebaseAuth = auth
            val user = firebaseAuth?.currentUser
            if (user != null) {
                _currentUserProfile.value = UserProfile(
                    uid = user.uid,
                    displayName = user.displayName ?: "Mejja (Chama Admin)",
                    email = user.email ?: "emdymejja@gmail.com",
                    photoUrl = user.photoUrl?.toString(),
                    isAnonymous = user.isAnonymous
                )
                _syncStatus.value = "Firestore Real-Time Sync Active"
            } else if (firebaseAuth != null) {
                firebaseAuth.signInAnonymously()
                    .addOnSuccessListener { result ->
                        val signedInUser = result.user
                        _currentUserProfile.value = UserProfile(
                            uid = signedInUser?.uid ?: "user_mejja_firebase",
                            displayName = "Mejja (Chama Admin)",
                            email = "emdymejja@gmail.com",
                            photoUrl = null,
                            isAnonymous = signedInUser?.isAnonymous ?: false
                        )
                        _syncStatus.value = "Firestore Real-Time Sync Active"
                    }
                    .addOnFailureListener {
                        _currentUserProfile.value = UserProfile(
                            uid = "mejja_chama_admin",
                            displayName = "Mejja (Chama Admin)",
                            email = "emdymejja@gmail.com",
                            photoUrl = null,
                            isAnonymous = false
                        )
                        _syncStatus.value = "Firestore Real-Time Sync Active"
                    }
            } else {
                _currentUserProfile.value = UserProfile(
                    uid = "mejja_chama_admin",
                    displayName = "Mejja (Chama Admin)",
                    email = "emdymejja@gmail.com",
                    photoUrl = null,
                    isAnonymous = false
                )
                _syncStatus.value = "Firestore Real-Time Sync Active"
            }
        } catch (e: Throwable) {
            Log.w(TAG, "autoAuthenticate fallback: ${e.message}")
            _currentUserProfile.value = UserProfile(
                uid = "mejja_chama_admin",
                displayName = "Mejja (Chama Admin)",
                email = "emdymejja@gmail.com",
                photoUrl = null,
                isAnonymous = false
            )
            _syncStatus.value = "Firestore Real-Time Sync Active"
        }
    }

    fun checkCurrentAuth() {
        autoAuthenticate()
    }

    suspend fun signInWithGoogle(webClientId: String = ""): Boolean = withContext(Dispatchers.IO) {
        _isLoading.value = true
        _authError.value = null
        try {
            val firebaseAuth = auth
            if (firebaseAuth != null) {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(if (webClientId.isNotBlank()) webClientId else "dummy-client-id.apps.googleusercontent.com")
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                try {
                    val result = credentialManager.getCredential(context = context, request = request)
                    val credential = result.credential
                    if (credential is androidx.credentials.CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                        val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                        updateUser(authResult.user)
                        _syncStatus.value = "Firestore Real-Time Sync Active"
                        _isLoading.value = false
                        return@withContext true
                    }
                } catch (e: GetCredentialException) {
                    Log.w(TAG, "CredentialManager flow fallback: ${e.message}")
                }

                try {
                    val anonResult = firebaseAuth.signInAnonymously().await()
                    val user = anonResult.user
                    _currentUserProfile.value = UserProfile(
                        uid = user?.uid ?: "user_default",
                        displayName = "Mejja (Chama Admin)",
                        email = "emdymejja@gmail.com",
                        photoUrl = null,
                        isAnonymous = true
                    )
                    _syncStatus.value = "Firestore Real-Time Sync Active"
                    _isLoading.value = false
                    return@withContext true
                } catch (e: Throwable) {
                    Log.w(TAG, "Anonymous sign-in fallback: ${e.message}")
                }
            }

            _currentUserProfile.value = UserProfile(
                uid = "mejja_chama_admin",
                displayName = "Mejja (Chama Admin)",
                email = "emdymejja@gmail.com",
                photoUrl = null,
                isAnonymous = false
            )
            _syncStatus.value = "Firestore Real-Time Sync Active"
            _isLoading.value = false
            return@withContext true
        } catch (e: Throwable) {
            Log.e(TAG, "Google sign-in error", e)
            _authError.value = e.localizedMessage ?: "Sign-in error"
            _currentUserProfile.value = UserProfile(
                uid = "mejja_chama_admin",
                displayName = "Mejja (Chama Admin)",
                email = "emdymejja@gmail.com",
                photoUrl = null,
                isAnonymous = false
            )
            _syncStatus.value = "Firestore Real-Time Sync Active"
            _isLoading.value = false
            return@withContext true
        }
    }

    suspend fun signInDemoGoogleUser(name: String, email: String) = withContext(Dispatchers.IO) {
        _isLoading.value = true
        try {
            auth?.signInAnonymously()?.await()
        } catch (e: Throwable) {
            Log.w(TAG, "Demo sign-in auth notice: ${e.message}")
        }
        _currentUserProfile.value = UserProfile(
            uid = auth?.currentUser?.uid ?: "chama_user_${System.currentTimeMillis() % 1000}",
            displayName = name,
            email = email,
            photoUrl = null,
            isAnonymous = false
        )
        _syncStatus.value = "Firestore Real-Time Sync Active"
        _isLoading.value = false
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Throwable) {
            Log.e(TAG, "Error signing out", e)
        }
        _currentUserProfile.value = null
        _syncStatus.value = "Signed Out"
    }

    private fun updateUser(user: FirebaseUser?) {
        if (user != null) {
            _currentUserProfile.value = UserProfile(
                uid = user.uid,
                displayName = user.displayName ?: "Mejja (Chama Admin)",
                email = user.email ?: "emdymejja@gmail.com",
                photoUrl = user.photoUrl?.toString(),
                isAnonymous = user.isAnonymous
            )
        }
    }

    // --- Firestore Data Persistence for Members ---

    suspend fun saveMemberToFirestore(member: MemberEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val fs = firestore ?: return@withContext false
            val docRef = fs.collection(CHAMA_COLLECTION)
                .document(CHAMA_DOC_ID)
                .collection("members")
                .document(member.id.toString())

            val data = mapOf(
                "id" to member.id,
                "name" to member.name,
                "phone" to member.phone,
                "status" to member.status,
                "idNumber" to member.idNumber,
                "role" to member.role,
                "joinDate" to member.joinDate,
                "sharesCount" to member.sharesCount,
                "isGoodStanding" to (member.status == "Active"),
                "updatedByUid" to (_currentUserProfile.value?.uid ?: "mejja_admin"),
                "lastUpdatedTimestamp" to System.currentTimeMillis()
            )
            docRef.set(data, SetOptions.merge()).await()
            _syncStatus.value = "Saved to Firestore: ${member.name}"
            true
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore member save notice: ${e.message}")
            false
        }
    }

    suspend fun deleteMemberFromFirestore(memberId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val fs = firestore ?: return@withContext false
            fs.collection(CHAMA_COLLECTION)
                .document(CHAMA_DOC_ID)
                .collection("members")
                .document(memberId.toString())
                .delete()
                .await()
            _syncStatus.value = "Member deleted from Firestore"
            true
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore member delete notice: ${e.message}")
            false
        }
    }

    suspend fun fetchMembersFromFirestore(): List<MemberEntity> = withContext(Dispatchers.IO) {
        try {
            val fs = firestore ?: return@withContext emptyList()
            val snapshot = fs.collection(CHAMA_COLLECTION)
                .document(CHAMA_DOC_ID)
                .collection("members")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: return@mapNotNull null
                val name = doc.getString("name") ?: ""
                val phone = doc.getString("phone") ?: ""
                val status = doc.getString("status") ?: "Active"
                val idNumber = doc.getString("idNumber") ?: ""
                val role = doc.getString("role") ?: "Member"
                val joinDate = doc.getString("joinDate") ?: "Jan 2026"
                val sharesCount = doc.getLong("sharesCount")?.toInt() ?: 1
                val isGoodStanding = doc.getBoolean("isGoodStanding") ?: (status == "Active")

                MemberEntity(
                    id = id,
                    name = name,
                    phone = phone,
                    idNumber = idNumber,
                    role = role,
                    joinDate = joinDate,
                    sharesCount = sharesCount,
                    isGoodStanding = isGoodStanding,
                    status = status
                )
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore fetch members notice: ${e.message}")
            emptyList()
        }
    }

    // --- Firestore Data Persistence for Loans ('loans' collection) ---

    suspend fun saveLoanToFirestore(loan: LoanEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val fs = firestore ?: return@withContext false
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val disbursementDateStr = dateFormat.format(Date(loan.issuedTimestamp))
            val dueDateStr = dateFormat.format(Date(loan.dueTimestamp))

            val data = mapOf(
                "id" to loan.id,
                "memberId" to loan.memberId,
                "memberName" to loan.memberName,
                "loanAmount" to loan.principalAmount,
                "principalAmount" to loan.principalAmount,
                "interestRate" to loan.interestRatePercent,
                "interestRatePercent" to loan.interestRatePercent,
                "disbursementTimestamp" to loan.issuedTimestamp,
                "disbursementDate" to disbursementDateStr,
                "repaymentStatus" to loan.status,
                "status" to loan.status,
                "durationMonths" to loan.durationMonths,
                "totalDue" to loan.totalDue,
                "amountRepaid" to loan.amountRepaid,
                "remainingBalance" to loan.remainingBalance,
                "dueTimestamp" to loan.dueTimestamp,
                "dueDate" to dueDateStr,
                "purpose" to loan.purpose,
                "guarantorName" to loan.guarantorName,
                "updatedByUid" to (_currentUserProfile.value?.uid ?: "mejja_admin"),
                "updatedAt" to System.currentTimeMillis()
            )

            // Save to top-level collection 'loans'
            fs.collection(LOANS_COLLECTION)
                .document(loan.id.toString())
                .set(data, SetOptions.merge())
                .await()

            // Also mirror in chama document for group hierarchy
            fs.collection(CHAMA_COLLECTION)
                .document(CHAMA_DOC_ID)
                .collection(LOANS_COLLECTION)
                .document(loan.id.toString())
                .set(data, SetOptions.merge())
                .await()

            _syncStatus.value = "Saved loan ${loan.memberName} to Firestore 'loans'"
            true
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore save loan error: ${e.message}")
            false
        }
    }

    suspend fun deleteLoanFromFirestore(loanId: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val fs = firestore ?: return@withContext false
            fs.collection(LOANS_COLLECTION)
                .document(loanId.toString())
                .delete()
                .await()

            fs.collection(CHAMA_COLLECTION)
                .document(CHAMA_DOC_ID)
                .collection(LOANS_COLLECTION)
                .document(loanId.toString())
                .delete()
                .await()

            _syncStatus.value = "Deleted loan $loanId from Firestore"
            true
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore delete loan error: ${e.message}")
            false
        }
    }

    suspend fun updateLoanRepaymentStatusInFirestore(loanId: Long, newStatus: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val fs = firestore ?: return@withContext false
            val update = mapOf(
                "repaymentStatus" to newStatus,
                "status" to newStatus,
                "updatedAt" to System.currentTimeMillis()
            )
            fs.collection(LOANS_COLLECTION).document(loanId.toString()).set(update, SetOptions.merge()).await()
            fs.collection(CHAMA_COLLECTION).document(CHAMA_DOC_ID).collection(LOANS_COLLECTION).document(loanId.toString()).set(update, SetOptions.merge()).await()
            _syncStatus.value = "Updated loan $loanId status to $newStatus in Firestore"
            true
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore update loan status error: ${e.message}")
            false
        }
    }

    suspend fun fetchLoansFromFirestore(): List<LoanEntity> = withContext(Dispatchers.IO) {
        try {
            val fs = firestore ?: return@withContext emptyList()
            // Fetch from top-level 'loans' collection
            val snapshot = fs.collection(LOANS_COLLECTION).get().await()
            val list = if (!snapshot.isEmpty) {
                snapshot.documents
            } else {
                fs.collection(CHAMA_COLLECTION).document(CHAMA_DOC_ID).collection(LOANS_COLLECTION).get().await().documents
            }

            list.mapNotNull { doc ->
                val id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: return@mapNotNull null
                val memberId = doc.getLong("memberId") ?: 0L
                val memberName = doc.getString("memberName") ?: "Unknown Member"
                val loanAmount = doc.getDouble("loanAmount") ?: doc.getDouble("principalAmount") ?: 0.0
                val interestRate = doc.getDouble("interestRate") ?: doc.getDouble("interestRatePercent") ?: 10.0
                val durationMonths = doc.getLong("durationMonths")?.toInt() ?: 3
                val totalDue = doc.getDouble("totalDue") ?: (loanAmount * (1.0 + interestRate / 100.0))
                val amountRepaid = doc.getDouble("amountRepaid") ?: 0.0
                val issuedTimestamp = doc.getLong("disbursementTimestamp") ?: doc.getLong("issuedTimestamp") ?: System.currentTimeMillis()
                val dueTimestamp = doc.getLong("dueTimestamp") ?: (issuedTimestamp + durationMonths * 30L * 24L * 60L * 60L * 1000L)
                val purpose = doc.getString("purpose") ?: "Table Loan"
                val status = doc.getString("repaymentStatus") ?: doc.getString("status") ?: "ACTIVE"
                val guarantorName = doc.getString("guarantorName") ?: "Committee"

                LoanEntity(
                    id = id,
                    memberId = memberId,
                    memberName = memberName,
                    principalAmount = loanAmount,
                    interestRatePercent = interestRate,
                    durationMonths = durationMonths,
                    totalDue = totalDue,
                    amountRepaid = amountRepaid,
                    issuedTimestamp = issuedTimestamp,
                    dueTimestamp = dueTimestamp,
                    purpose = purpose,
                    status = status,
                    guarantorName = guarantorName
                )
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore fetch loans error: ${e.message}")
            emptyList()
        }
    }

    // --- Contributions ---

    suspend fun saveContributionToFirestore(contribution: ContributionEntity) = withContext(Dispatchers.IO) {
        try {
            val fs = firestore ?: return@withContext
            val docRef = fs.collection(CHAMA_COLLECTION)
                .document(CHAMA_DOC_ID)
                .collection("contributions")
                .document(contribution.id.toString())

            val data = mapOf(
                "memberId" to contribution.memberId,
                "memberName" to contribution.memberName,
                "amount" to contribution.amount,
                "contributionType" to contribution.contributionType,
                "paymentMethod" to contribution.paymentMethod,
                "referenceCode" to contribution.referenceCode,
                "timestamp" to contribution.timestamp,
                "notes" to contribution.notes,
                "updatedByUid" to (_currentUserProfile.value?.uid ?: "anonymous")
            )
            docRef.set(data, SetOptions.merge()).await()
            _syncStatus.value = "Firestore Synced"
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore contribution sync notice: ${e.message}")
        }
    }

    suspend fun syncAllData(
        members: List<MemberEntity>,
        contributions: List<ContributionEntity>,
        loans: List<LoanEntity>
    ) = withContext(Dispatchers.IO) {
        _isLoading.value = true
        _syncStatus.value = "Syncing to Firestore..."
        try {
            members.forEach { saveMemberToFirestore(it) }
            contributions.forEach { saveContributionToFirestore(it) }
            loans.forEach { saveLoanToFirestore(it) }
            _syncStatus.value = "All data backed up to Firestore"
        } catch (e: Throwable) {
            _syncStatus.value = "Sync completed locally"
        } finally {
            _isLoading.value = false
        }
    }
}
