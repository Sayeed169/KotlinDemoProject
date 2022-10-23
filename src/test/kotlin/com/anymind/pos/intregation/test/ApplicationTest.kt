package com.anymind.pos.intregation.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.io.IOException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ApplicationTest {
    @Autowired
    var client: WebTestClient? = null
    @DisplayName("Test with all valid input")
    @ParameterizedTest
    @CsvSource("AllValidInputPaymentCASHLow, AllValidInputPaymentCASHLow", "AllValidInputPaymentCASH_ON_DELIVERYLow, AllValidInputPaymentCASH_ON_DELIVERYLow", "AllValidInputPaymentVISALow, AllValidInputPaymentVISALow", "AllValidInputPaymentMASTERCARDLow, AllValidInputPaymentMASTERCARDLow", "AllValidInputPaymentAMEXLow, AllValidInputPaymentAMEXLow", "AllValidInputPaymentJCBLow, AllValidInputPaymentJCBLow", "AllValidInputPaymentCASHHigh, AllValidInputPaymentCASHHigh", "AllValidInputPaymentCASH_ON_DELIVERYHigh, AllValidInputPaymentCASH_ON_DELIVERYHigh", "AllValidInputPaymentVISAHigh, AllValidInputPaymentVISAHigh", "AllValidInputPaymentMASTERCARDHigh, AllValidInputPaymentMASTERCARDHigh", "AllValidInputPaymentAMEXHigh, AllValidInputPaymentAMEXHigh", "AllValidInputPaymentJCBHigh, AllValidInputPaymentJCBHigh")
    @Throws(IOException::class)
    fun TestFinalPriceAndPointCalculation(input: String, output: String) {
        val response = getBodyByFileName("response/$output")
        client!!.post().uri("/get/sales/price")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(getBodyByFileName("request/$input")).exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.final_price").isEqualTo(response["final_price"].textValue())
                .jsonPath("$.points").isEqualTo(response["points"].intValue())
    }

    @DisplayName("Test with all invalid input for Payment Modifier")
    @ParameterizedTest
    @CsvSource("InvalidPriceModifierPaymentCASH, InvalidPriceModifierException", "InvalidPriceModifierPaymentCASH_ON_DELIVERY, InvalidPriceModifierException", "InvalidPriceModifierPaymentVISA, InvalidPriceModifierException", "InvalidPriceModifierPaymentMASTERCARD, InvalidPriceModifierException", "InvalidPriceModifierPaymentAMEX, InvalidPriceModifierException")
    @Throws(IOException::class)
    fun TestFinalPriceAndPointWithInvalidPaymentModifierValue(input: String, output: String) {
        val response = getBodyByFileName("response/$output")
        client!!.post().uri("/get/sales/price")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(getBodyByFileName("request/$input")).exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(response["errorCode"].textValue())
                .jsonPath("$.errorMessage").isEqualTo(response["errorMessage"].textValue())
    }

    @DisplayName("Test with all invalid input for Payment Modifier")
    @ParameterizedTest
    @CsvSource("InvalidPrice, NumberFormatException, price", "InvalidNegativePrice, InvalidInputException, price", "InvalidNoPrice, NumberFormatException, price", "InvalidPriceModifier, NumberFormatException, price_modifier", "InvalidNegativePriceModifier, InvalidPriceModifierException, price_modifier", "InvalidNoPriceModifier, NumberFormatException, price_modifier", "InvalidPaymentMethod, InvalidPaymentMethodException, payment_method", "EmptyPaymentMethod, InvalidPaymentMethodException, payment_method", "InvalidNoPaymentMethod, InvalidInputException, payment_method", "InvalidDatetime, InvalidInputFormatException, datetime", "EmptyDatetime, InvalidInputFormatException, datetime", "InvalidNoDatetime, NumberFormatException, datetime")
    @Throws(IOException::class)
    fun TestFinalPriceAndPointWithInvalidVaInput(input: String, output: String, messageWord: String?) {
        val response = getBodyByFileName("response/$output")
        val message = String.format(response["errorMessage"].textValue(), messageWord)
        client!!.post().uri("/get/sales/price")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(getBodyByFileName("request/$input")).exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(response["errorCode"].textValue())
                .jsonPath("$.errorMessage").isEqualTo(message)
    }

    @DisplayName("Test with all valid input")
    @Test
    @Throws(IOException::class)
    fun TestSaleListWithValidInput() {
        client!!.post().uri("/get/sales/list")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("""{
  "startDatetime": "2022-09-01T00:00:10Z",
  "endDatetime": "2022-09-01T11:59:00Z"
}""").exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.sales[0].price").isEqualTo("500.00")
                .jsonPath("$.sales[0].points").isEqualTo("19")
                .jsonPath("$.sales[1].price").isEqualTo("0")
                .jsonPath("$.sales[1].points").isEqualTo("0")
    }

    @DisplayName("Test with all valid input")
    @Test
    @Throws(IOException::class)
    fun TestSaleListWithInvalidInput() {
        client!!.post().uri("/get/sales/list")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("""{
  "startDatetime": "abc",
  "endDatetime": "2022-09-01T11:59:00Z"
}""").exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("E100")
                .jsonPath("$.errorMessage").isEqualTo("Invalid input format")
    }

    @DisplayName("Test with invalid method")
    @Test
    @Throws(IOException::class)
    fun TestWithInvalidMethod() {
        client!!.put().uri("/get/sales/list")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("").exchange()
                .expectStatus().isEqualTo(405)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("E101")
                .jsonPath("$.errorMessage").isEqualTo("PUT method not allowed")
    }

    @Throws(IOException::class)
    private fun getBodyByFileName(fileName: String): ObjectNode {
        val responseFile: Resource = ClassPathResource("$fileName.json")
        val mapper = ObjectMapper()
        val jsonNode = mapper.readTree(responseFile.file)
        return jsonNode.deepCopy()
    }
}