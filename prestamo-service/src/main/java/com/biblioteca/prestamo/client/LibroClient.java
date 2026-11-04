package com.biblioteca.prestamo.client;

import com.biblioteca.prestamo.dto.LibroResumen;
import com.biblioteca.prestamo.exception.BusinessException;
import com.biblioteca.prestamo.exception.RemoteServiceException;
import com.biblioteca.prestamo.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Component
public class LibroClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public LibroClient(@Value("${services.libro-url}") String libroUrl, ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().baseUrl(libroUrl).build();
        this.objectMapper = objectMapper;
    }

    public LibroResumen reservar(Long id) {
        try {
            LibroResumen libro = restClient.post()
                    .uri("/api/libros/{id}/reservar", id)
                    .retrieve()
                    .body(LibroResumen.class);
            if (libro == null) {
                throw new RemoteServiceException("libro-service devolvió una respuesta vacía al reservar");
            }
            return libro;
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException("No existe un libro con el ID " + id);
            }
            if (ex.getStatusCode().value() == 409) {
                throw new BusinessException(extractMessage(ex));
            }
            throw new RemoteServiceException("libro-service respondió HTTP " + ex.getStatusCode().value() + ": " + extractMessage(ex), ex);
        } catch (RestClientException ex) {
            throw new RemoteServiceException("No se pudo comunicar con libro-service", ex);
        }
    }

    public LibroResumen liberar(Long id) {
        try {
            LibroResumen libro = restClient.post()
                    .uri("/api/libros/{id}/liberar", id)
                    .retrieve()
                    .body(LibroResumen.class);
            if (libro == null) {
                throw new RemoteServiceException("libro-service devolvió una respuesta vacía al liberar");
            }
            return libro;
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException("No existe un libro con el ID " + id);
            }
            throw new RemoteServiceException("libro-service respondió HTTP " + ex.getStatusCode().value() + ": " + extractMessage(ex), ex);
        } catch (RestClientException ex) {
            throw new RemoteServiceException("No se pudo comunicar con libro-service", ex);
        }
    }

    private String extractMessage(RestClientResponseException ex) {
        try {
            String body = ex.getResponseBodyAsString();
            if (body == null || body.isBlank()) {
                return ex.getStatusText();
            }
            Map<?, ?> json = objectMapper.readValue(body, Map.class);
            Object message = json.get("message");
            return message != null ? message.toString() : body;
        } catch (Exception ignored) {
            return ex.getResponseBodyAsString();
        }
    }
}
