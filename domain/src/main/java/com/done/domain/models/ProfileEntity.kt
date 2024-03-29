package com.done.domain.models

data class ProfileEntity(
    val userAvatar: String,
    val userFullName: String,
    val userName: String,
    val about: String,
    val repoCount: Int,
    val followerCount: Int,
    val followingCount: Int,
)
