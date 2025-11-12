package com.sena.springecommerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.springecommerce.model.Orden;
// Ya no necesitamos importar los services de DetalleOrden o Producto si no se usan
import com.sena.springecommerce.service.IOrdenService;
import com.sena.springecommerce.service.IUsuarioService; // Asumimos que es mejor usar el Service que el Repository

@RestController
@RequestMapping("/api/ordenes") // Nomenclatura REST: plural en la URL
public class APIOrdenController {

	// Se inyecta la dependencia principal: IOrdenService
	private final IOrdenService ordenService;
	// private final IUsuarioService usuarioService; // Se inyectaría si fuera
	// necesario.

	// 1. Inyección de dependencias por Constructor (Mejor Práctica)
	public APIOrdenController(IOrdenService ordenService /* , IUsuarioService usuarioService */) {
		this.ordenService = ordenService;
		// this.usuarioService = usuarioService;
	}

	// --- MÉTODOS CRUD ---

	/**
	 * Obtiene todas las órdenes. GET /api/ordenes
	 */
	@GetMapping
	public List<Orden> getAllOrdenes() {
		return ordenService.findAll();
	}

	/**
	 * Obtiene una orden por su ID. GET /api/ordenes/{id}
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Orden> getOrdenById(@PathVariable Integer id) {
		Optional<Orden> orden = ordenService.findById(id);

		// Uso de Optional: si está presente (map), devuelve 200 OK. Si no, devuelve 404
		// NOT FOUND.
		return orden.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Crea una nueva orden. POST /api/ordenes
	 */
	@PostMapping
	public ResponseEntity<Orden> createOrden(@RequestBody Orden orden) {
		// Lógica de negocio esencial (ej: asignación de campos internos):

		// 1. Se debe asegurar que el número de orden sea único. Esto debería
		// hacerse en la capa de servicio, no en el controlador.
		// orden.setNumero(generarNumeroUnicoDeOrden());

		// 2. La fecha de creación debe ser la actual si no viene seteada.
		// if (orden.getFechacreacion() == null) {
		// orden.setFechacreacion(new Date());
		// }

		// 3. Se asume que la orden que se recibe ya tiene asignado el Usuario.

		Orden savedOrden = ordenService.save(orden);

		// Devuelve la orden creada con un estado HTTP 201 Created
		return ResponseEntity.status(HttpStatus.CREATED).body(savedOrden);
	}

	/**
	 * Actualiza una orden existente. PUT /api/ordenes/{id}
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Orden> updateOrden(@PathVariable Integer id, @RequestBody Orden ordenDetails) {
		Optional<Orden> ordenOptional = ordenService.findById(id);

		if (ordenOptional.isEmpty()) {
			// Devuelve 404 NOT FOUND si la orden no existe
			return ResponseEntity.notFound().build();
		}

		Orden existingOrden = ordenOptional.get();

		// 4. Implementación de la Lógica de Actualización
		// Se actualizan SOLO los campos que se pueden modificar (ej: Total, Usuario,
		// etc. no se tocan).
		existingOrden.setNumero(ordenDetails.getNumero());
		existingOrden.setTotal(ordenDetails.getTotal());
		// No se debe modificar la fecha de creación en un PUT, a menos que sea el
		// propósito.

		Orden updatedOrden = ordenService.save(existingOrden);

		// Devuelve 200 OK con el objeto actualizado
		return ResponseEntity.ok(updatedOrden);
	}

}