package uniandes.edu.co.proyecto.controller;

import uniandes.edu.co.proyecto.modelo.Prestamo;
import uniandes.edu.co.proyecto.modelo.Prestamo.EstadoPrestamo;
import uniandes.edu.co.proyecto.repositorios.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @GetMapping
    public List<Prestamo> getAllPrestamos() {
        return prestamoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prestamo> getPrestamoById(@PathVariable Long id) {
        return prestamoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Prestamo createPrestamo(@RequestBody Prestamo prestamo) {
        return prestamoRepository.save(prestamo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prestamo> updatePrestamo(@PathVariable Long id, @RequestBody Prestamo prestamoDetails) {
        return prestamoRepository.findById(id)
                .map(prestamo -> {
                    prestamo.setTipoProducto(prestamoDetails.getTipoProducto());
                    prestamo.setEstadoPrestamo(prestamoDetails.getEstadoPrestamo());
                    prestamo.setMonto(prestamoDetails.getMonto());
                    prestamo.setInteres(prestamoDetails.getInteres());
                    prestamo.setCantidadCuotas(prestamoDetails.getCantidadCuotas());
                    prestamo.setDiaPagoDeCuotas(prestamoDetails.getDiaPagoDeCuotas());
                    prestamo.setValorCuota(prestamoDetails.getValorCuota());
                    return ResponseEntity.ok(prestamoRepository.save(prestamo));
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrestamo(@PathVariable Long id) {
        return prestamoRepository.findById(id)
                .map(prestamo -> {
                    prestamoRepository.delete(prestamo);
                    return ResponseEntity.ok().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<?> cerrarPrestamo(@PathVariable Long id) {
        Prestamo prestamo = prestamoRepository.findById(id).orElse(null);
        if (prestamo == null) {
            return ResponseEntity.notFound().build();
        }
        if (!prestamo.getEstadoPrestamo().equals(EstadoPrestamo.pagado)) {
            return ResponseEntity.badRequest().body("El préstamo no está en estado pagado y no puede cerrarse.");
        }
        if (prestamo.getSaldoPendiente() > 0) {
            return ResponseEntity.badRequest().body("El préstamo tiene un saldo pendiente y no puede cerrarse.");
        }
    
        prestamoRepository.cerrarPrestamoSiSaldoEsCero(id);
        return ResponseEntity.ok().build();
}

}
