package com.tecup.backend.controllers;

import com.tecup.backend.models.Event;
import com.tecup.backend.models.User;
import com.tecup.backend.payload.repository.EventRepository;
import com.tecup.backend.payload.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/events")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    // Obtener todos los eventos
    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('ORGANIZADOR') or hasRole('ADMIN') or hasRole('JURADO')")
    public List<Event> getAllEvents() {
        logger.info("Listando todos los eventos para usuarios logeados.");
        return eventRepository.findAll();
    }

    // Crear un nuevo evento
    @PostMapping("/add")
    @PreAuthorize("hasRole('ORGANIZADOR')") // Solo los organizadores pueden crear eventos
    public ResponseEntity<?> addEvent(@RequestBody Event event) {
        logger.info("Intentando agregar un nuevo evento: {}", event.getName());

        // Obtener el usuario autenticado desde el contexto de seguridad
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Usuario autenticado no encontrado.");
        }

        // Asociar el usuario autenticado como organizador del evento
        event.setUser(user.get());

        // Validar fechas
        if (event.getFecha_inicio() == null || event.getFecha_fin() == null) {
            return ResponseEntity.badRequest().body("Error: Las fechas de inicio y fin son obligatorias.");
        }

        if (event.getFecha_inicio().after(event.getFecha_fin())) {
            return ResponseEntity.badRequest().body("Error: La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        // Guardar el evento
        Event savedEvent = eventRepository.save(event);
        return ResponseEntity.ok(savedEvent);
    }

}
