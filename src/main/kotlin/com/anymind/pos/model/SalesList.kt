package com.anymind.pos.model

class SalesList {
    private var sales: MutableList<Sale> = ArrayList()
    fun addSaleElement(sale: Sale) {
        sales.add(sale)
    }

    constructor(sales: MutableList<Sale>) {
        this.sales = sales
    }

    constructor() {}

    fun getSales(): List<Sale> {
        return sales
    }

    fun setSales(sales: MutableList<Sale>) {
        this.sales = sales
    }
}