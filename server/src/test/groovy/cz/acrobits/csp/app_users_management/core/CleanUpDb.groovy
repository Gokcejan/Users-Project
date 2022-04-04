package cz.acrobits.csp.app_users_management.core

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate

trait CleanUpDb {

    @Autowired
    JdbcTemplate jdbcTemplate

    def cleanUpDb() {
        jdbcTemplate.execute("DELETE FROM password_reset_request")
        jdbcTemplate.execute("DELETE FROM app_user")
        jdbcTemplate.execute("DELETE FROM persistent_command")
    }
}