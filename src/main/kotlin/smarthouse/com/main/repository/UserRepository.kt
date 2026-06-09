package smarthouse.com.main.repository

import org.springframework.data.jpa.repository.JpaRepository
import smarthouse.com.main.model.User

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}