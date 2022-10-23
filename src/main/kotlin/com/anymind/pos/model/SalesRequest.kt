package com.anymind.pos.model

class SalesRequest {
    var price: String? = null
    var price_modifier = 0.0
    var payment_method: String? = null
    var datetime: String? = null

    constructor(price: String?, price_modifier: Double, payment_method: String?, datetime: String?) {
        this.price = price
        this.price_modifier = price_modifier
        this.payment_method = payment_method
        this.datetime = datetime
    }

    constructor() {}
}