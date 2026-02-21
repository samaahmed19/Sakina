package com.sama.sakina.data.repository

import com.sama.sakina.data.local.database.dao.UserDao
import com.sama.sakina.data.local.database.entity.UserEntity
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    suspend fun saveUser(
        name: String,
        email: String?,
        location: String?
    ) {
        userDao.insertUser(
            UserEntity(
                id = 1,
                name = name,
                email = email,
                location = location
            )
        )
    }

    fun getUser() = userDao.getUser()

    suspend fun getUserOnce(): UserEntity? {
        return userDao.getUserOnce()
    }

    suspend fun updateLocation(location: String?) {
        userDao.updateLocation(location)
    }
}
