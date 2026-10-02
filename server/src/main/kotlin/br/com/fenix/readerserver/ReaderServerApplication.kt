package br.com.fenix.readerserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReaderServerApplication

fun main(args: Array<String>) {
    runApplication<ReaderServerApplication>(*args)
}
