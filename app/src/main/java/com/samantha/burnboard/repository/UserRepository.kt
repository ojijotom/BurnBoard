package com.samantha.burnboard.repository

import com.samantha.burnboard.data.UserDao
import com.samantha.burnboard.model.User

class UserRepository(private val userDao: UserDao) {
    suspend fun registerUser(user: User) {
        userDao.registerUser(user)
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.loginUser(email, password)
    }
}