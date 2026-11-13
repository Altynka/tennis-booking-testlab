package kz.testlab.court

import kz.testlab.common.ApiException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/courts")
class CourtController(private val repository: CourtRepository) {

    /** GET /courts — список всех кортов */
    @GetMapping
    fun getAll(): List<Court> = repository.findAll()

    /** GET /courts/{id} — один корт или 404 в едином формате ошибок */
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): Court =
        repository.findById(id).orElseThrow {
            ApiException(HttpStatus.NOT_FOUND, "COURT_NOT_FOUND", "Court $id not found")
        }
}
