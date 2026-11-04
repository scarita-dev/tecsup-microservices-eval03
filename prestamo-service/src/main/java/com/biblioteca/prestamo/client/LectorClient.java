package com.biblioteca.prestamo.client;

import com.biblioteca.prestamo.dto.LectorResumen;
import com.biblioteca.prestamo.exception.RemoteServiceException;
import com.biblioteca.prestamo.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Component
public class LectorClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public LectorClient(@Value("${services.lector-url}") String lectorUrl, ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().baseUrl(lectorUrl).build();
        this.objectMapper = objectMapper;
    }

    public LectorResumen obtenerPorId(Long id) {
        try {
            LectorResumen lector = restClient.get()
                    .uri("/api/lectores/{id}", id)
                    .retrieve()
                    .body(LectorResumen.class);
            if (lector == null) {
                throw new RemoteServiceException("lector-service devolvió una respuesta vacía");
            }
            return lector;
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException("No existe un lector con el ID " + id);
            }
            throw new RemoteServiceException("lector-service respondió HTTP " + ex.getStatusCode().value() + ": " + extractMessage(ex), ex);
        } catch (RestClientException ex) {
            throw new RemoteServiceException("No se pudo comunicar con lector-service", ex);
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
