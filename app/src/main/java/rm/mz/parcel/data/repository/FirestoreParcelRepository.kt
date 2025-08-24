package rm.mz.parcel.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import rm.mz.parcel.data.model.*
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import rm.mz.parcel.util.isEmpty
import android.content.Context
import com.google.firebase.FirebaseApp
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class FirestoreParcelRepository @Inject constructor(
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    functions: FirebaseFunctions,
    @ApplicationContext private val context: Context
) : BaseFirestoreRepository(firestore, auth, functions),
    ParcelRepository {

    companion object {
        private const val TAG = "FirestoreParcelRepo"
    }

    init {
        try {
            // Initialize Firebase if not already initialized
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
                Log.d(TAG, "Firebase initialized successfully")
            } else {
                Log.d(TAG, "Firebase was already initialized")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase", e)
            throw RuntimeException("Failed to initialize Firebase", e)
        }
    }

    private val parcelsCollection = firestore.collection("parcels")
    private val locationHistoryCollection = firestore.collection("location_history")
    private val sharedLocationsCollection = firestore.collection("shared_locations")
    private val usersCollection = firestore.collection("users")

    override suspend fun createParcel(parcel: Parcel): Result<String> {
        return try {
            val receiverResult = findUserByEmail(parcel.receiverEmail)
            if (receiverResult.isFailure) {
                Log.e(TAG, "Failed to find receiver: ${receiverResult.exceptionOrNull()?.message}")
                return Result.failure(Exception("Receiver email not found"))
            }

            val receiverId = receiverResult.getOrNull() ?: run {
                Log.e(TAG, "Invalid receiver ID")
                return Result.failure(Exception("Invalid receiver"))
            }

            val parcelWithIds = parcel.copy(
                receiverId = receiverId,
                senderId = requireAuth(),
                senderEmail = currentUserEmail ?: ""
            )

            val docRef = parcelsCollection.add(parcelWithIds).await()
            Log.d(TAG, "Successfully created parcel with ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating parcel", e)
            Result.failure(e)
        }
    }

    override suspend fun updateParcelLocation(parcelId: String, location: GeoPoint): Result<Unit> = try {
        parcelsCollection.document(parcelId)
            .update(
                mapOf(
                    "currentLocation" to location,
                    "updatedAt" to Timestamp.now()
                )
            )
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateParcelStatus(parcelId: String, status: ParcelStatus): Result<Unit> {
        return try {
            parcelsCollection.document(parcelId)
                .update("status", status, "updatedAt", Timestamp.now())
                .await()

            val parcel = parcelsCollection.document(parcelId).get().await()
                .toObject(Parcel::class.java) ?: throw Exception("Parcel not found")

            val notification = when (status) {
                ParcelStatus.PICKED_UP -> Pair("Parcel Picked Up", "Your parcel has been picked up")
                ParcelStatus.IN_TRANSIT -> Pair("Parcel In Transit", "Your parcel is on its way")
                ParcelStatus.DELIVERED -> Pair("Parcel Delivered", "Your parcel has been delivered")
                ParcelStatus.CANCELLED -> Pair("Parcel Cancelled", "Your parcel has been cancelled")
                ParcelStatus.PENDING -> return Result.success(Unit)
            }

            sendParcelNotification(
                userId = parcel.receiverId,
                title = notification.first,
                body = notification.second,
                parcelId = parcelId
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getParcelUpdates(parcelId: String): Flow<Parcel> = callbackFlow {
        val subscription = parcelsCollection.document(parcelId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.toObject(Parcel::class.java)?.let { trySend(it) }
            }
        awaitClose { subscription.remove() }
    }

    override fun getSentParcels(userId: String): Flow<List<Parcel>> = callbackFlow {
        val subscription = parcelsCollection
            .whereEqualTo("senderId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val parcels = snapshot?.documents?.mapNotNull { 
                    it.toObject(Parcel::class.java)?.copy(id = it.id) 
                } ?: emptyList()
                trySend(parcels)
            }
            
        awaitClose { subscription.remove() }
    }

    override fun getReceivedParcels(userId: String): Flow<List<Parcel>> = callbackFlow {
        val subscription = parcelsCollection
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val parcels = snapshot?.documents?.mapNotNull { 
                    it.toObject(Parcel::class.java)?.copy(id = it.id) 
                } ?: emptyList()
                trySend(parcels)
            }
            
        awaitClose { subscription.remove() }
    }

    override suspend fun addLocationHistory(
        parcelId: String,
        location: GeoPoint,
        status: ParcelStatus,
        description: String
    ): Result<String> = try {
        val history = LocationHistory(
            parcelId = parcelId,
            location = location,
            status = status,
            description = description,
            timestamp = Timestamp.now()
        )
        
        val docRef = locationHistoryCollection.add(history).await()
        
        // Also update the parcel's current location and status
        parcelsCollection.document(parcelId)
            .update(
                mapOf(
                    "currentLocation" to location,
                    "status" to status,
                    "updatedAt" to Timestamp.now()
                )
            )
            .await()
            
        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getLocationHistory(parcelId: String): Flow<List<LocationHistory>> = callbackFlow {
        val subscription = locationHistoryCollection
            .whereEqualTo("parcelId", parcelId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val history = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(LocationHistory::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                trySend(history)
            }
            
        awaitClose { subscription.remove() }
    }

    override suspend fun shareLocation(
        parcelId: String, 
        sharedWith: String, 
        expirationHours: Int
    ): Result<String> = try {
        val parcel = parcelsCollection.document(parcelId).get().await()
            .toObject(Parcel::class.java) ?: throw Exception("Parcel not found")

        val expiresAt = Calendar.getInstance().apply {
            add(Calendar.HOUR, expirationHours)
        }.time

        val shareableLocation = ShareableLocation(
            parcelId = parcelId,
            sharedBy = requireAuth(),
            sharedWith = sharedWith,
            expiresAt = Timestamp(expiresAt),
            location = parcel.currentLocation ?: throw Exception("No location available"),
            trackingNumber = parcel.trackingNumber
        )

        val docRef = sharedLocationsCollection.add(shareableLocation).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun shareParcelWithMultiple(
        parcelId: String,
        recipients: List<String>,
        options: ShareOptions
    ): Result<List<String>> = try {
        val parcel = parcelsCollection.document(parcelId).get().await()
            .toObject(Parcel::class.java) ?: throw Exception("Parcel not found")

        val expiresAt = Calendar.getInstance().apply {
            add(Calendar.HOUR, options.duration.hours)
        }.time

        val shareResults = recipients.map { email ->
            findUserByEmail(email).map { userId ->
                val shareableLocation = ShareableLocation(
                    parcelId = parcelId,
                    sharedBy = requireAuth(),
                    sharedWith = userId,
                    expiresAt = Timestamp(expiresAt),
                    location = parcel.currentLocation ?: throw Exception("No location available"),
                    trackingNumber = parcel.trackingNumber,
                    permissions = options.permissions,
                    notifyOnUpdates = options.notifyOnUpdates
                )

                val docRef = sharedLocationsCollection.add(shareableLocation).await()

                if (options.notifyOnUpdates) {
                    sendParcelNotification(
                        userId = userId,
                        title = "Parcel Shared",
                        body = "A parcel has been shared with you by ${currentUserEmail}",
                        parcelId = parcelId
                    )
                }

                docRef.id
            }
        }

        val successfulShares = shareResults.mapNotNull { it.getOrNull() }
        if (successfulShares.isEmpty()) {
            Result.failure(Exception("Failed to share with any recipients"))
        } else {
            Result.success(successfulShares)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun stopSharing(shareId: String): Result<Unit> = try {
        sharedLocationsCollection.document(shareId)
            .update("isActive", false)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getSharedLocations(userId: String): Flow<List<ShareableLocation>> = callbackFlow {
        val subscription = sharedLocationsCollection
            .whereEqualTo("sharedWith", userId)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val now = Timestamp.now()
                val locations = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ShareableLocation::class.java)?.let { location ->
                        if (location.expiresAt?.seconds ?: 0 > now.seconds) {
                            location.copy(id = doc.id)
                        } else {
                            doc.reference.update("isActive", false)
                            null
                        }
                    }
                } ?: emptyList()
                
                trySend(locations)
            }
            
        awaitClose { subscription.remove() }
    }

    override suspend fun findUserByEmail(email: String): Result<String> {
        return try {
            Log.d(TAG, "Finding user by email: $email")

            // First verify the email is registered in Auth
            val authMethods = auth.fetchSignInMethodsForEmail(email).await()
            if (authMethods.isEmpty()) {
                Log.d(TAG, "Email not found in Auth, creating new user: $email")
                
                // Create user in Auth
                try {
                    // Generate a temporary password (you might want to implement a proper password generation)
                    val tempPassword = "Temp${System.currentTimeMillis()}"
                    val authResult = auth.createUserWithEmailAndPassword(email, tempPassword).await()
                    
                    // Create user document in Firestore
                    val userId = authResult.user?.uid ?: throw Exception("Failed to get user ID")
                    usersCollection.document(userId)
                        .set(
                            mapOf(
                                "email" to email,
                                "createdAt" to Timestamp.now()
                            )
                        )
                        .await()

                    // Send password reset email
                    auth.sendPasswordResetEmail(email).await()
                    
                    Log.d(TAG, "Created new user with ID: $userId")
                    return Result.success(userId)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to create new user", e)
                    return Result.failure(Exception("Failed to create user account"))
                }
            }

            // User exists in Auth, get or create Firestore document
            val snapshot = usersCollection
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            val userId = snapshot.documents.firstOrNull()?.id
            if (userId != null) {
                Log.d(TAG, "Found existing user with ID: $userId")
                Result.success(userId)
            } else {
                // User exists in Auth but not in Firestore
                val authUser = auth.fetchSignInMethodsForEmail(email).await()
                if (!authUser.isEmpty()) {
                    // Create Firestore document for existing Auth user
                    val newDoc = usersCollection.add(
                        mapOf(
                            "email" to email,
                            "createdAt" to Timestamp.now()
                        )
                    ).await()
                    
                    Log.d(TAG, "Created Firestore document for existing Auth user: ${newDoc.id}")
                    Result.success(newDoc.id)
                } else {
                    Log.e(TAG, "User document not found and user not in Auth")
                    Result.failure(Exception("User not found"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding/creating user by email", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserEmail(userId: String): Result<String> = try {
        val snapshot = usersCollection.document(userId).get().await()
        val email = snapshot.getString("email")
        if (email != null) {
            Result.success(email)
        } else {
            Result.failure(Exception("Email not found"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun searchUsers(query: String): Result<List<UserSearchResult>> {
        return try {
            if (query.length < 3) {
                return Result.success(emptyList())
            }

            Log.d(TAG, "Searching for users with query: $query")

            // Get user details from Firestore with prefix matching
            val snapshot = usersCollection
                .whereGreaterThanOrEqualTo("email", query.lowercase())
                .whereLessThanOrEqualTo("email", query.lowercase() + '\uf8ff')
                .limit(5)
                .get()
                .await()

            val users = snapshot.documents.mapNotNull { doc ->
                val email = doc.getString("email")?.lowercase() ?: return@mapNotNull null
                val displayName = doc.getString("displayName")
                UserSearchResult(
                    id = doc.id,
                    email = email,
                    displayName = displayName,
                    isExactMatch = email == query.lowercase()
                )
            }.filter { it.id != currentUserId } // Exclude current user
            .sortedWith(
                compareByDescending<UserSearchResult> { it.isExactMatch }
                .thenBy { it.email }
            )

            Log.d(TAG, "Found ${users.size} users")
            Result.success(users)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching users", e)
            Result.failure(e)
        }
    }

    override suspend fun updateFcmToken(token: String): Result<Unit> = try {
        requireAuth()
        usersCollection.document(currentUserId!!)
            .update("fcmToken", token)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun sendParcelNotification(
        userId: String,
        title: String,
        body: String,
        parcelId: String
    ): Result<Unit> = try {
        val data = hashMapOf(
            "userId" to userId,
            "title" to title,
            "body" to body,
            "parcelId" to parcelId
        )
        
        functions
            .getHttpsCallable("sendParcelNotification")
            .call(data)
            .await()
            
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
