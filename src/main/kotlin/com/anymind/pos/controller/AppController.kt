package com.anymind.pos.controller

import com.anymind.pos.model.SalesList
import com.anymind.pos.model.SalesRequest
import com.anymind.pos.model.TimeRange
import com.anymind.pos.service.SalesService
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/get/sales"], produces = [MediaType.APPLICATION_JSON_VALUE])
class AppController internal constructor(private val salesService: SalesService) {
    @PostMapping(value = ["/list"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getSalesByTime(@RequestBody timeRange: TimeRange): SalesList {
        return salesService.getSalesByTime(timeRange)
    }

    @PostMapping(value = ["/price"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getSalesPriceWithPoints(@RequestBody request: SalesRequest): ObjectNode {
        return salesService.calculateSalesPoint(request)
    }
}