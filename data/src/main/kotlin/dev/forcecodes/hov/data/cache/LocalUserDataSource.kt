package dev.forcecodes.hov.data.cache

import dev.forcecodes.hov.data.cache.entity.UserEntity
import dev.forcecodes.hov.data.internal.InternalApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface LocalUserDataSource {
    fun saveUsers(users: List<UserEntity>)
    fun getUserFlow(): Flow<List<UserEntity>>
}

@Singleton
class LocalDataSourceImpl @Inject constructor(
    @InternalApi private val appDatabase: AppDatabase
) : LocalUserDataSource {

    override fun saveUsers(users: List<UserEntity>) {
        appDatabase.userDao()
            .saveUsers(users)
    }

    override fun getUserFlow(): Flow<List<UserEntity>> {
        return appDatabase.userDao()
            .getUsers()
    }
}