package uniandes.edu.co.proyecto.controller;

import uniandes.edu.co.proyecto.modelo.PuntoDeAtencion;
import uniandes.edu.co.proyecto.repositorios.OperacionRepository;
import uniandes.edu.co.proyecto.repositorios.PuntoDeAtencionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/puntosDeAtencion")
public class PuntoDeAtencionController {

    @Autowired
    private PuntoDeAtencionRepository puntoDeAtencionRepository;

    @Autowired
    private OperacionRepository operacionRepository;

    @GetMapping
    public List<PuntoDeAtencion> getAllPuntosDeAtencion() {
        return puntoDeAtencionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuntoDeAtencion> getPuntoDeAtencionById(@PathVariable Long id) {
        return puntoDeAtencionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public PuntoDeAtencion createPuntoDeAtencion(@RequestBody PuntoDeAtencion puntoDeAtencion) {
        return puntoDeAtencionRepository.save(puntoDeAtencion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PuntoDeAtencion> updatePuntoDeAtencion(@PathVariable Long id, @RequestBody PuntoDeAtencion puntoDeAtencionDetails) {
        return puntoDeAtencionRepository.findById(id)
                .map(puntoDeAtencion -> {
                    puntoDeAtencion.setTipo(puntoDeAtencionDetails.getTipo());
                    return ResponseEntity.ok(puntoDeAtencionRepository.save(puntoDeAtencion));
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePuntoDeAtencion(@PathVariable Long id) {
        if (operacionRepository.hasOperations(id)) {
            return ResponseEntity.badRequest().body("No se puede borrar: Se han hecho operaciones en este punto de atencion.");
        }
    
        PuntoDeAtencion puntoDeAtencion = puntoDeAtencionRepository.findById(id).orElse(null);
        if (puntoDeAtencion == null) {
            return ResponseEntity.notFound().build();
        }
    
        puntoDeAtencionRepository.delete(puntoDeAtencion);
        return ResponseEntity.ok().build();
    }
}

