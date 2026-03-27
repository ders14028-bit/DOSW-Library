# DOSW-Library

## Estado de Entrega (Parte 1 y Parte 2)

- Persistencia relacional con Spring Data JPA y PostgreSQL/H2.
- Arquitectura por capas con capa persistence desacoplada (dao, mapper, repository).
- Autenticacion con JWT en `/api/auth/login`.
- Autorizacion por roles (`USER`, `LIBRARIAN`) con `@PreAuthorize`.
- API stateless (`SessionCreationPolicy.STATELESS`) y CSRF deshabilitado.
- CORS configurado globalmente.
- Manejo de errores de negocio y respuestas de seguridad:
	- `401 Unauthorized` para token ausente/invalido/expirado.
	- `403 Forbidden` para acceso sin permisos.
- Control de inventario implementado para prestamos/devoluciones.
- HTTPS habilitado por perfil `ssl` con certificado local de desarrollo.

## Diagrama de componentes general

![img.png](src/main/resources/images/general.png)

---

## Diagrama de componentes especifico

![img.png](src/main/resources/images/especifico.png)

---

## Diagrama de clases

![img.png](src/main/resources/images/clases.png)

---

## Pruebas

### Pruebas unitarias

![img.png](img.png)

---

## Video de evidencia

- [Video](https://youtu.be/87mfAid5HuE)

- [Video actualizado con parte 2](https://youtu.be/iKK2_mWsTwA)

---

