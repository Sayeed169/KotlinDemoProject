package com.anymind.pos.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Entity
class Sale {
    @Id
    @JsonIgnore
    var id: Long = 0
        private set

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    var saleDatetime: Date? = null
    var datetime: String? = null
    var price: String? = null
    var points = 0

    constructor(id: Long, saleDatetime: Date?, datetime: String?, price: String?, points: Int) {
        this.id = id
        this.saleDatetime = saleDatetime
        this.datetime = datetime
        this.price = price
        this.points = points
    }

    constructor() {}
}