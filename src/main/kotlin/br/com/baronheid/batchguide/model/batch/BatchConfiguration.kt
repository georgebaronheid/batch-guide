package br.com.baronheid.batchguide.model.batch

import br.com.baronheid.batchguide.model.entities.Person
import br.com.baronheid.batchguide.model.listeners.JobCompletionNotificationListener
import br.com.baronheid.batchguide.model.processor.PersonItemProcessor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import javax.sql.DataSource
import org.springframework.batch.core.launch.support.RunIdIncrementer





/**
 *
 */
@Configuration
@EnableBatchProcessing
class BatchConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {

    /**
     * Defines the input. ItemReader searches for the files and parses each line with enough information to build the
     * object Person
     */

    @Bean
    fun reader() = FlatFileItemReaderBuilder<Person>()
        .name("personItemReader")
        .resource(ClassPathResource("sample-data.csv"))
        .delimited()
        .names("firstName", "lastName")
        .fieldSetMapper(object : BeanWrapperFieldSetMapper<Person>() {
            init {
                setTargetType(Person::class.java)
            }
        })
        .build()

    /**
     * Defines the processor.
     */
    @Bean
    fun processor(): PersonItemProcessor = PersonItemProcessor()

    /**
     * Defines the output. In this case it includes the processed information in a memory based database created
     * by @EnableBatchProcessing.
     */
    @Bean
    fun writer(dataSource: DataSource): JdbcBatchItemWriter<Person> = JdbcBatchItemWriterBuilder<Person>()
        .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider())
        .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
        .dataSource(dataSource)
        .build()

    /**
     * This first method defines the job which is composed of steps.
     */
    @Bean
    fun importUserJob(listener: JobCompletionNotificationListener, step1: Step): Job {
        return jobBuilderFactory["importUserJob"]
            .incrementer(RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build()
    }

    /**
     * Each step involves a reader, a processor and a writer. The incrementer is needed because it involves the use of a
     * database to maintain its execution state. In this case we defined the amount of records processed to be 10.
     * In the generic method chunk we define the Input and Output types of each chunk, in this case <Person, Person>,
     * accordingly.
     */
    @Bean
    fun step1(writer: JdbcBatchItemWriter<Person>) = stepBuilderFactory
        .get("step1")
        .chunk<Person, Person>(10)
        .reader(reader())
        .processor(processor())
        .writer(writer)
        .build()

}
