package br.com.baronheid.batchguide

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BatchGuideApplication

fun main(args: Array<String>) {
	runApplication<BatchGuideApplication>(*args)
}
