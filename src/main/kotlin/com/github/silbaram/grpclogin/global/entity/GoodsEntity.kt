package com.github.silbaram.grpclogin.global.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "goods")
data class GoodsEntity(
    @Id
    @Column(name = "goods_no")
    val goodsNo: Long = 0,

    @Column(name = "goods_name")
    var goodsName: String? = null,

    var price: Long = 0,

    @Column(name = "stock_level")
    var stockLevel: Int = 0
)