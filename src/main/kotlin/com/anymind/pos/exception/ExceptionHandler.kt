package com.anymind.pos.exception

import com.anymind.pos.util.Constants
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

//@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(ex: ApplicationException): ResponseEntity<Any> {
        val node = JsonNodeFactory.instance.objectNode()
                .put(Constants.KEY_ERROR_CODE, ex.errorCode)
                .put(Constants.KEY_ERROR_MESSAGE, ex.errorMessage)
        return ResponseEntity(node, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NumberFormatException::class)
    fun handleNumberFormatException(ex: NumberFormatException?): ResponseEntity<Any> {
        val node = JsonNodeFactory.instance.objectNode()
                .put(Constants.KEY_ERROR_CODE, ErrorCode.NUMBER_FORMAT_INVALID)
                .put(Constants.KEY_ERROR_MESSAGE, ErrorMessage.NUMBER_FORMAT_INVALID)
        return ResponseEntity(node, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleNumberFormatException(ex: Exception?): ResponseEntity<Any> {
        val node = JsonNodeFactory.instance.objectNode()
                .put(Constants.KEY_ERROR_CODE, ErrorCode.INPUT_FORMAT_ERROR)
                .put(Constants.KEY_ERROR_MESSAGE, ErrorMessage.INPUT_FORMAT_ERROR)
        return ResponseEntity(node, HttpStatus.BAD_REQUEST)
    }

    override fun handleHttpRequestMethodNotSupported(ex: HttpRequestMethodNotSupportedException,
                                                     headers: HttpHeaders,
                                                     status: HttpStatus,
                                                     request: WebRequest): ResponseEntity<Any> {
        val strBuilder = StringBuilder()
        strBuilder.append(ex.method).append(ErrorMessage.METHOD_NOT_ALLOWED)
        val node = JsonNodeFactory.instance.objectNode()
                .put(Constants.KEY_ERROR_CODE, ErrorCode.METHOD_NOT_ALLOWED)
                .put(Constants.KEY_ERROR_MESSAGE, strBuilder.toString())
        return ResponseEntity(node, HttpStatus.METHOD_NOT_ALLOWED)
    }
}