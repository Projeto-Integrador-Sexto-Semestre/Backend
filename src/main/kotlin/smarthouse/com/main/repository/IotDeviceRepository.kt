package smarthouse.com.main.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import smarthouse.com.main.model.IotDevice

@Repository
interface IotDeviceRepository : JpaRepository<IotDevice, Long> {
    fun findByRoomId(roomId: Long): List<IotDevice>
}