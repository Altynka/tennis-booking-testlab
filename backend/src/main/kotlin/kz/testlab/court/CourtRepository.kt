package kz.testlab.court

import org.springframework.data.jpa.repository.JpaRepository

/** Spring сам генерирует SQL для findAll, findById, save, delete и т.д. */
interface CourtRepository : JpaRepository<Court, Long>
