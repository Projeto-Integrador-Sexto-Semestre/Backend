package smarthouse.com.main.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import smarthouse.com.main.model.DeviceType

@Repository
interface DeviceTypeRepository : JpaRepository<DeviceType, Long>