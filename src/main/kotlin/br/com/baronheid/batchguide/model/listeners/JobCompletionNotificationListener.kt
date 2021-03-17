package br.com.baronheid.batchguide.model.listeners

import br.com.baronheid.batchguide.model.entities.Person
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.listener.JobExecutionListenerSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

/**
 * Listens for when a job is {BatchStatus.COMPLETED} and then uses JdbcTemplate to inspect the results.
 */
@Component
class JobCompletionNotificationListener() : JobExecutionListenerSupport() {

    private val log: Logger = LoggerFactory.getLogger(JobCompletionNotificationListener::class.java)

    private var jdbcTemplate: JdbcTemplate? = null

    @Autowired
    constructor(jdbcTemplate: JdbcTemplate) : this() {
        this.jdbcTemplate = jdbcTemplate
    }

    override fun afterJob(jobExecution: JobExecution) {
        if (jobExecution.status == BatchStatus.COMPLETED) log.info("Job Finished")
            .also {
                jdbcTemplate!!
                    .query("SELECT first_name, last_name FROM people") { rs, _ ->
                        Person(rs.getString(1), rs.getString(2))
                    }
                    .forEach { log.info("Found <$it> in the database.")}
            }
    }

}
