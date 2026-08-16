package com.elxes.simplenote.di

import android.content.Context
import androidx.room.Room
import com.elxes.simplenote.data.local.NoteDatabase
import com.elxes.simplenote.data.local.dao.NoteDao
import com.elxes.simplenote.data.repository.NoteRepositoryImpl
import com.elxes.simplenote.data.repository.PreferenceRepositoryImpl
import com.elxes.simplenote.domain.repository.NoteRepository
import com.elxes.simplenote.domain.repository.PreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "note_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(db: NoteDatabase): NoteDao {
        return db.noteDao
    }

    @Provides
    @Singleton
    fun provideNoteRepository(dao: NoteDao): NoteRepository {
        return NoteRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun providePreferenceRepository(): PreferenceRepository {
        return PreferenceRepositoryImpl()
    }
}
