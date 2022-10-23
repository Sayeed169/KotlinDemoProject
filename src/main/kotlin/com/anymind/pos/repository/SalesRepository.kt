package com.anymind.pos.repository

import com.anymind.pos.model.Sale
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SalesRepository : JpaRepository<Sale?, Long> {
    fun findAllBySaleDatetimeBetween(startDatetime: Date?, endDatetime: Date?): List<Sale?>?
}