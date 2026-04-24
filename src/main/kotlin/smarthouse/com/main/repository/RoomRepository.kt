package smarthouse.com.main.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import smarthouse.com.main.model.Room

@Repository
interface RoomRepository : JpaRepository<Room, Long> {
    // Busca todos os cómodos de uma determinada casa
    fun findByHouseId(houseId: Long): List<Room>
}