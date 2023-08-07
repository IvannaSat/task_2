package com.example.task_2.repository

import com.example.task_2.domain.model.User

interface UserItemClickListener {
    fun onUserDelete(user: User, position: Int)
}
