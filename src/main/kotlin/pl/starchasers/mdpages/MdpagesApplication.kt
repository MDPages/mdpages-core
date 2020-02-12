package pl.starchasers.mdpages

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class MdpagesApplication

fun main(args: Array<String>) {
    runApplication<MdpagesApplication>(*args)
}
