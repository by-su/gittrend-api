package com.rootbly.openpulse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@EnableScheduling
@SpringBootApplication
class OpenpulseApplication

fun main(args: Array<String>) {
    runApplication<OpenpulseApplication>(*args)
}
