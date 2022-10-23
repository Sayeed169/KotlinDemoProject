package com.anymind.pos.exception

class InvalidInputException(inputField: String) : ApplicationException() {
    override val errorCode: String?
    override val errorMessage: String?

    init {
        errorCode = ErrorCode.INVALID_INPUT
        errorMessage = String.format(ErrorMessage.INVALID_INPUT, inputField)
    }
}