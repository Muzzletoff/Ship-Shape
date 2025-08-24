package rm.mz.parcel.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import com.google.maps.GeoApiContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rm.mz.parcel.data.repository.FirestoreParcelRepository
import rm.mz.parcel.data.repository.ParcelRepository
import javax.inject.Singleton
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
    
    @Provides
    @Singleton
    fun provideParcelRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        functions: FirebaseFunctions,
        @ApplicationContext context: Context
    ): ParcelRepository {
        return FirestoreParcelRepository(firestore, auth, functions, context)
    }
    
    @Provides
    @Singleton
    fun provideGeoApiContext(): GeoApiContext {
        return GeoApiContext.Builder()
            .apiKey("YOUR_MAPS_API_KEY")
            .build()
    }
} 