package smarthouse.com.main.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import smarthouse.com.main.model.House

@Repository
interface HouseRepository : JpaRepository<House, Long> {
    // Busca todas as casas que pertencem a um determinado usuário
    fun findByUserId(userId: Long): List<House>
}