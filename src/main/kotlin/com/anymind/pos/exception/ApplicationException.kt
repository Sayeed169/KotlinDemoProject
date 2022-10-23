package com.anymind.pos.exception

abstract class ApplicationException internal constructor() : RuntimeException() {
    abstract val errorCode: String?
    abstract val errorMessage: String?
}