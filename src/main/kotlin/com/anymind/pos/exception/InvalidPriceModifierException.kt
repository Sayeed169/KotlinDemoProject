package com.anymind.pos.exception

class InvalidPriceModifierException : ApplicationException() {
    override val errorCode: String?
    override val errorMessage: String?

    init {
        errorCode = ErrorCode.INVALID_PRICE_MODIFIER
        errorMessage = ErrorMessage.INVALID_PRICE_MODIFIER
    }
}