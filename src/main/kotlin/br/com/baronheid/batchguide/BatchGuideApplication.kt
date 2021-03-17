package br.com.baronheid.batchguide

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.system.exitProcess

/**
 * This annotation is a convenience that adds several configurations
 */
@SpringBootApplication
class BatchGuideApplication

fun main(args: Array<String>) {
    exitProcess(SpringApplication.exit(SpringApplication.run(BatchGuideApplication::class.java, *args)))
}

