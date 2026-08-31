package feelings.guide.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DataStoreModule::class])
object TestDataStoreModule {

    // Hilt's @Singleton scope is tied to each @HiltAndroidTest class's own component, so running
    // many test classes in one instrumentation process can invoke this @Provides method more than
    // once against the same underlying file, which DataStore's file-locking rejects ("multiple
    // DataStores active for the same file"). A plain process-wide holder guarantees exactly one
    // DataStore instance regardless of how many Hilt components get built.
    @Volatile
    private var instance: DataStore<Preferences>? = null

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        instance ?: synchronized(this) {
            instance ?: PreferenceDataStoreFactory.create(
                produceFile = { context.preferencesDataStoreFile("feelings_guide_settings_test") }
            ).also { instance = it }
        }
}
