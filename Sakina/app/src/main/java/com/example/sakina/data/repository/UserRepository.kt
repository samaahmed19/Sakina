package com.example.sakina.data.repository

import com.example.sakina.data.local.database.dao.UserDao
import com.example.sakina.data.local.database.entity.UserEntity
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
}
