package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.ForbiddenOperationException;
import edu.eci.dosw.tdd.core.exception.GlobalExceptionHandler;
import edu.eci.dosw.tdd.core.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleBookNotAvailable() {
        ResponseEntity<Map<String, String>> response =
                handler.handleBookNotAvailable(new BookNotAvailableException("b1"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("El libro con ID b1 no está disponible para préstamo.", response.getBody().get("error"));
    }

    @Test
    void shouldHandleUserNotFound() {
        ResponseEntity<Map<String, String>> response =
                handler.handleNotFound(new UserNotFoundException("No se encontro usuario con ID: u9"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No se encontro usuario con ID: u9", response.getBody().get("error"));
    }

    @Test
    void shouldHandleLoanLimitExceeded() {
        ResponseEntity<Map<String, String>> response =
                handler.handleLoanLimit(new LoanLimitExceededException("u1", 3));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("El usuario u1 supero el limite de 3 prestamos activos.", response.getBody().get("error"));
    }

    @Test
    void shouldHandleBadRequest() {
        ResponseEntity<Map<String, String>> response =
                handler.handleBadRequest(new IllegalArgumentException("payload invalido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("payload invalido", response.getBody().get("error"));
    }

    @Test
    void shouldHandleForbidden() {
        ResponseEntity<Map<String, String>> response =
                handler.handleForbidden(new ForbiddenOperationException("accion no permitida"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("accion no permitida", response.getBody().get("error"));
    }
}

