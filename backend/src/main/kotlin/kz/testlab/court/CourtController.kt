package kz.testlab.court

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/courts")
class CourtController(private val repository: CourtRepository) {

    /** GET /courts — список всех кортов */
    @GetMapping
    fun getAll(): List<Court> = repository.findAll()

    /** GET /courts/{id} — один корт или 404, если такого нет */
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): Court =
        repository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Court $id not found")
        }
}
