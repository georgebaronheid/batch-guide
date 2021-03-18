package br.com.baronheid.batchguide.model.processor

import br.com.baronheid.batchguide.model.entities.Person
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor

/**
 * ItemProcessor is a SpringBatch's interface. It'll make it easier to link a batch job that, in this case,
 * will transform data to UpperCase.
 */
class PersonItemProcessor : ItemProcessor<Person, Person> {

    private val log: Logger = LoggerFactory.getLogger(PersonItemProcessor::class.java)

    override fun process(item: Person): Person =
        Person(
            item.firstName.toUpperCase(), item.lastName.toUpperCase()
        )
            .also { log.info("Converting ($item) into ($it)") }
}

