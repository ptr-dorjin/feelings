package feelings.guide.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import feelings.guide.data.AnswerDao
import feelings.guide.data.FeelingsDatabase
import feelings.guide.data.QuestionDao
import feelings.guide.data.SeedCallback
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FeelingsDatabase =
        Room.inMemoryDatabaseBuilder(context, FeelingsDatabase::class.java)
            .addCallback(SeedCallback(context))
            .allowMainThreadQueries()
            .build()

    @Provides
    fun provideQuestionDao(database: FeelingsDatabase): QuestionDao = database.questionDao()

    @Provides
    fun provideAnswerDao(database: FeelingsDatabase): AnswerDao = database.answerDao()
}
