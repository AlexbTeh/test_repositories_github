package com.done.common

import java.time.LocalDate
import java.time.LocalDateTime

object Time {
    fun today(): LocalDate = LocalDate.now()
    fun now(): LocalDateTime = LocalDateTime.now()
}
