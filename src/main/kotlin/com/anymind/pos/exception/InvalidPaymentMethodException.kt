package com.anymind.pos.exception

class InvalidPaymentMethodException : ApplicationException() {
    override val errorCode: String?
    override val errorMessage: String?

    init {
        errorCode = ErrorCode.INVALID_PAYMENT_METHOD
        errorMessage = ErrorMessage.INVALID_PAYMENT_METHOD
    }
}