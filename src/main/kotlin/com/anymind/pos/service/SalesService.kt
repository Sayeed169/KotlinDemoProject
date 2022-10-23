package com.anymind.pos.service

import com.anymind.pos.exception.InvalidInputException
import com.anymind.pos.exception.InvalidPaymentMethodException
import com.anymind.pos.exception.InvalidPriceModifierException
import com.anymind.pos.model.Sale
import com.anymind.pos.model.SalesList
import com.anymind.pos.model.SalesRequest
import com.anymind.pos.model.TimeRange
import com.anymind.pos.repository.SalesRepository
import com.anymind.pos.util.Constants
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.Supplier

/**
 * @author sayeed.hassan
 */
@Service
class SalesService
/**
 * Constructor
 * @param salesRepository
 */ internal constructor(private val salesRepository: SalesRepository) {
    /**
     * Returns list of sales based on the time range provided by the user
     * @param timeRange specifying start and end of the range
     * @return List of sales data
     */
    fun getSalesByTime(timeRange: TimeRange): SalesList {
        var startDatetime = Instant.parse(Optional.ofNullable(timeRange.startDatetime)
                .orElseThrow(Supplier { InvalidInputException("startDatetime") }))
        val endDatetime = Instant.parse(Optional.ofNullable(timeRange.endDatetime)
                .orElseThrow(Supplier { InvalidInputException("endDatetime") }))
        val list = SalesList()
        while (startDatetime.toEpochMilli() < endDatetime.toEpochMilli()) {
            val nextHour = startDatetime.plus(1, ChronoUnit.HOURS)
            var price = BigDecimal(0)
            var point = 0
            val saleList = salesRepository.findAllBySaleDatetimeBetween(Date.from(startDatetime), Date.from(nextHour))
            for (sale in saleList!!) {
                price = price.add(BigDecimal(sale?.price))
                point += sale!!.points
            }
            list.addSaleElement(Sale(0, Date.from(startDatetime), startDatetime.toString(), price.toString(), point))
            startDatetime = nextHour
        }
        return list
    }

    /**
     * Calculate points and final price for a sale detail, calculates points based on this mapping
     * `````````````````````````````````````````````````````````````````
     * |Payment Method   | Final Price                   | Point       |
     * `````````````````````````````````````````````````````````````````
     * |CASH             | price * 0.9 ~ price           | price * 0.05|
     * |CASH_ON_DELIVERY | price ~ price * 1.02          | price * 0.05|
     * |VISA             | price * 0.95 ~ price * 1      | price * 0.03|
     * |MASTERCARD       | price * 0.95 ~ price * 1      | price * 0.03|
     * |AMEX             | price * 0.98 ~ price * 1.01   | price * 0.02|
     * |JCB              | price * 0.95 ~ price * 1      | price * 0.05|
     * `````````````````````````````````````````````````````````````````
     * @param salesRequest provided by user
     * @return calculated points and final price
     */
    fun calculateSalesPoint(salesRequest: SalesRequest): ObjectNode {
        val price: BigDecimal = BigDecimal(Optional.ofNullable(salesRequest.price)
                .orElseThrow<InvalidInputException>(Supplier { InvalidInputException("price") }))
        if (price.toInt() < 0) {
            throw InvalidInputException("price")
        }
        val paymentMethod: String = Optional.ofNullable(salesRequest.payment_method)
                .orElseThrow<InvalidInputException>(Supplier { InvalidInputException("payment_method") })
        val priceModifier: Double = Optional.of(salesRequest.price_modifier)
                .orElseThrow<InvalidInputException>(Supplier { InvalidInputException("price_modifier") })
        val finalPrice: String
        val points: Int
        when (paymentMethod) {
            Constants.CASH -> {
                finalPrice = calculateFinalPrice(0.9, 1.0, priceModifier, price)
                points = calculatePoints(price, "0.05")
            }
            Constants.CASH_ON_DELIVERY -> {
                finalPrice = calculateFinalPrice(1.0, 1.2, priceModifier, price)
                points = calculatePoints(price, "0.05")
            }
            Constants.VISA, Constants.MASTERCARD -> {
                finalPrice = calculateFinalPrice(0.95, 1.0, priceModifier, price)
                points = calculatePoints(price, "0.03")
            }
            Constants.AMEX -> {
                finalPrice = calculateFinalPrice(0.98, 1.01, priceModifier, price)
                points = calculatePoints(price, "0.02")
            }
            Constants.JCB -> {
                finalPrice = calculateFinalPrice(0.95, 1.0, priceModifier, price)
                points = calculatePoints(price, "0.05")
            }
            else -> throw InvalidPaymentMethodException()
        }
        val datetime = Instant.parse(Optional.ofNullable(salesRequest.datetime)
                .orElseThrow<InvalidInputException>(Supplier { InvalidInputException("datetime") }))
        salesRepository.save(
                Sale(datetime.toEpochMilli(), Date.from(datetime), salesRequest.datetime, price.toString(), points)
        )
        return JsonNodeFactory.instance.objectNode()
                .put(Constants.FINAL_PRICE_RESPONSE_KEY, finalPrice)
                .put(Constants.POINT_RESPONSE_KEY, points)
    }

    /**
     * Calculates the final price based on the price and priceModifier
     * Validates if priceModifier is out of range
     * @param rangeStart is start of the valid range
     * @param rangeEnd is end of the valid range
     * @param priceModifier is the multiplier for final price
     * @param price is slaes price
     * @return single string representing final price
     */
    private fun calculateFinalPrice(rangeStart: Double, rangeEnd: Double, priceModifier: Double, price: BigDecimal): String {
        return if (priceModifier >= rangeStart && priceModifier <= rangeEnd) {
            price.multiply(BigDecimal.valueOf(priceModifier))
                    .setScale(2, RoundingMode.HALF_UP).toString()
        } else {
            throw InvalidPriceModifierException()
        }
    }

    /**
     * Calculates the points earned from the price
     * @param price is sales price
     * @param pointMultiplier is point multiplier
     * @return an integer representing eared points
     */
    private fun calculatePoints(price: BigDecimal, pointMultiplier: String): Int {
        return price.multiply(BigDecimal(pointMultiplier)).toInt()
    }
}