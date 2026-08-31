package feelings.guide.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import feelings.guide.data.AnswerDao
import feelings.guide.data.DATABASE_NAME
import feelings.guide.data.FeelingsDatabase
import feelings.guide.data.QuestionDao
import feelings.guide.data.SeedCallback
import feelings.guide.data.migration3To4
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FeelingsDatabase =
        Room.databaseBuilder(context, FeelingsDatabase::class.java, DATABASE_NAME)
            .addMigrations(migration3To4(context))
            .addCallback(SeedCallback(context))
            .build()

    @Provides
    fun provideQuestionDao(database: FeelingsDatabase): QuestionDao = database.questionDao()

    @Provides
    fun provideAnswerDao(database: FeelingsDatabase): AnswerDao = database.answerDao()
}
