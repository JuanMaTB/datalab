# DataLab – Feedback 1 (Programación Concurrente)

Este proyecto es la resolución del **Feedback 1 de Programación Concurrente**.  
La idea principal es construir un **servicio REST en Spring Boot** capaz de recibir trabajos (*Jobs*) de análisis de datasets (CSV), dividirlos en tareas (*Tasks*) y ejecutarlas **en paralelo**, aplicando concurrencia, transacciones, AOP y buenas prácticas de diseño.

No es un “hola mundo”: aquí hay **paralelismo real**, control de estados, reintentos, auditoría y métricas.

---

## 🧠 Idea general

El sistema funciona así:

1. Se crea un **Job** con un dataset (CSV) y un número de *shards*.
2. El Job se divide en varias **Tasks**.
3. Cada Task analiza una parte distinta del CSV.
4. Las Tasks se ejecutan **en paralelo** usando un pool de hilos.
5. Cada Task genera un **Result** con estadísticas reales (sum, avg, min, max).
6. Al finalizar, el Job pasa a `COMPLETED`, `PARTIAL_SUCCESS`, `FAILED` o `CANCELLED`.

Todo el flujo está instrumentado con **AOP**, **transacciones** y **auditoría**.

---

## 🏗️ Arquitectura y paquetes

La estructura del proyecto sigue una separación clara por capas:

juanma.datalab

├─ aspects/ → AOP (performance, retry)

├─ config/ → executor, async, datasource

├─ controller/ → API REST

├─ domain/ → Job, Task, Result

├─ dto/ → requests y responses

├─ repository/ → Spring Data JPA

└─ service/ → lógica de negocio y concurrencia



La lógica **nunca está en los controladores**: todo pasa por servicios.

---

## ⚙️ Tecnologías usadas

- **Spring Boot 3**
- Spring Web
- Spring Data JPA
- Spring AOP
- Spring Validation
- H2 (perfil `dev`)
- PostgreSQL + HikariCP (perfil `prod`)
- Lombok
- Java 17

---

## 🔄 Concurrencia

La concurrencia se implementa usando:

- `ThreadPoolTaskExecutor` configurable
- `@EnableAsync`
- `@Async`
- `CompletableFuture` para fan-out / fan-in

Cada Task se ejecuta en paralelo y el Job espera a que todas terminen para decidir el estado final.

---

## 🧩 Estrategias de análisis (IoC / DI)

El análisis del CSV no está “hardcodeado”. Se usa una interfaz:

- `AnalyzerStrategy`

Implementaciones:
- `SimpleAnalyzer` → estrategia por defecto (`@Primary`)
- `AdvancedAnalyzer` → solo activa en perfil `prod`

Esto permite cambiar el comportamiento **sin tocar el resto del sistema**, demostrando uso real de IoC y DI.

---

## 🔁 Reintentos automáticos (AOP)

Las Tasks pueden fallar por errores transitorios.  
Para eso se ha implementado:

- Anotación personalizada `@RetryableTransient`
- Aspecto que reintenta la ejecución con backoff
- Excepción `TransientDataException`

Los reintentos se aplican **solo donde tiene sentido**.

---

## ⏱️ Métricas y trazabilidad (AOP + MDC)

Existe un `PerformanceAspect` que:

- Mide el tiempo de ejecución de servicios y repositorios
- Añade un `traceId` a cada petición usando MDC
- Permite seguir un Job completo en logs fácilmente

El traceId se genera al inicio de cada petición HTTP mediante un filtro servlet y se propaga a través de MDC, permitiendo correlacionar logs incluso en ejecuciones asíncronas.


---

## 🧾 Auditoría con REQUIRES_NEW

Cuando una Task falla definitivamente:

- Se registra el fallo en un **servicio de auditoría**
- Usa `@Transactional(REQUIRES_NEW)`
- El fallo se audita **sin romper la transacción principal**

Esto demuestra control avanzado de transacciones.

---

## 🗄️ Persistencia y perfiles

### Perfil `dev`
- Base de datos H2 en memoria
- `ddl-auto: update`
- Consola H2 habilitada

### Perfil `prod`
- PostgreSQL
- Pool de conexiones **HikariCP**
- Configuración realista de producción

No es necesario levantar PostgreSQL para la entrega: el perfil está definido y documentado.

---

## 🌐 API REST disponible

### Crear Job (JSON)
POST /api/jobs

### Crear Job (CSV multipart)
POST /api/jobs

### Consultar estado del Job
GET /api/jobs/{id}

### Obtener resultados paginados
GET /api/jobs/{id}/results?page=0&size=10

### Cancelar Job
POST /api/jobs/{id}:cancel

### Health check
GET /health  
Endpoint técnico para comprobar que el servicio está levantado.

---

## 🧪 Cómo probar el proyecto

### 📦 Empaquetado como fat jar

El proyecto se empaqueta como un **fat jar ejecutable** usando Spring Boot:

mvn clean package  
java -jar target/datalab-0.0.1-SNAPSHOT.jar

De esta forma la aplicación puede ejecutarse sin necesidad de un servidor externo.


### ▶️ Arrancar el servidor

#### Windows (PowerShell)
```mvn spring-boot:run```

#### Linux / macOS
```mvn spring-boot:run```

El servidor arranca en http://localhost:8080.

### 🧪 Prueba 1: Crear un Job (JSON)

#### Windows (PowerShell)
```
$body = @{
  sourceUrl = "classpath:customer_purchases_1000.csv"
  shards = 6
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8080/api/jobs" `
  -ContentType "application/json" `
  -Body $body
```
#### Linux / macOS
```
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"sourceUrl":"classpath:customer_purchases_1000.csv","shards":6}'
```
### 🧪 Prueba 2: Ver estado del Job
```GET /api/jobs/{id} ```

Se puede observar el progreso (%) y el estado final.

### 🧪 Prueba 3: Ver resultados paginados
```GET /api/jobs/{id}/results?page=0&size=5 ```

### 🧪 Prueba 4: Cancelar un Job en ejecución
```POST /api/jobs/{id}:cancel ```
Las Tasks activas detectan el flag y se cancelan correctamente.

### 🧪 Prueba 5: Ver reintentos y auditoría
- El sistema provoca fallos transitorios de forma controlada.
- Se observan reintentos en logs.
- Si una Task falla definitivamente, aparece un registro [AUDIT].
