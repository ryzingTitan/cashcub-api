package com.ryzingtitan.cashcub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CashCubApplication

fun main(args: Array<String>) {
    runApplication<CashCubApplication>(*args)
}
