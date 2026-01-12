# 🚀 Optimización de Queries - Eliminación de Filtrado en Memoria

## 📋 Resumen

Se identificaron y corrigieron múltiples problemas de rendimiento donde se cargaban datos completos en memoria para luego filtrarlos/mapearlos con operaciones de colecciones de Kotlin. Todas estas operaciones fueron movidas a nivel de base de datos usando queries optimizadas.

## 🔴 Problemas Identificados

### 1. Problema N+1 en Lista de Amigos
**Ubicación:** `AmistadService.listaAmigos()`

**ANTES (❌ Ineficiente):**
```kotlin
fun listaAmigos(idUsuario: Long): List<UsuarioComun> {
    val usuario = repositorioUsuarioComun.findById(idUsuario).get()
    return usuario.amigos.map { 
        repositorioUsuarioComun.findById(it.toLong()).get()  // ❌ N queries!
    }
}
```

**Problema:** 
- 1 query para obtener el usuario
- N queries adicionales (una por cada amigo)
- Si un usuario tiene 50 amigos = 51 queries!

**DESPUÉS (✅ Optimizado):**
```kotlin
fun listaAmigos(idUsuario: Long): List<UsuarioComun> {
    return repositorioUsuarioComun.findAmigosByUsuarioId(idUsuario)  // ✅ 1 query!
}
```

**Query creada:**
```sql
SELECT amigo 
FROM UsuarioComun u 
JOIN u.amigos amigoId 
JOIN UsuarioComun amigo ON amigo.id = amigoId 
WHERE u.id = :idUsuario
```

**Mejora:** De N+1 queries a 1 sola query ✅

---

### 2. Filtrado en Memoria de Amigos por Show
**Ubicación:** `AmistadService.amigosQueVanAShow()`

**ANTES (❌ Ineficiente):**
```kotlin
fun amigosQueVanAShow(idUsuario: Long, idShow: String): List<UsuarioComun> {
    val usuario = repositorioUsuarioComun.findById(idUsuario).get()
    val amigos = usuario.amigos.map {  // ❌ N+1 queries
        repositorioUsuarioComun.findById(it.toLong()).get() 
    }
    return amigos.filter { it.listaIdShows().contains(idShow) }  // ❌ Filtrado en memoria
}
```

**Problemas:**
- N+1 queries para obtener amigos
- Carga todos los amigos en memoria
- Filtra en memoria usando `.filter { }`
- Accede a métodos del dominio que pueden hacer más queries

**DESPUÉS (✅ Optimizado):**
```kotlin
fun amigosQueVanAShow(idUsuario: Long, idShow: String): List<UsuarioComun> {
    return repositorioUsuarioComun.findAmigosQueVanAShowOptimizado(idUsuario, idShow)
}
```

**Query creada:**
```sql
SELECT DISTINCT amigo 
FROM UsuarioComun u 
JOIN u.amigos amigoId 
JOIN UsuarioComun amigo ON amigo.id = amigoId 
JOIN amigo.entradasCompradas entrada 
WHERE u.id = :idUsuario 
AND entrada.showId = :showId
```

**Mejora:** De múltiples queries + filtrado en memoria a 1 query optimizada ✅

---

### 3. Filtrado de Entradas Disponibles en Memoria
**Ubicación:** `CarritoService.agregarAlCarrito()`

**ANTES (❌ Ineficiente):**
```kotlin
val entradasDisponibles = repositorioEntrada
    .findEntradasByShowIdAndFuncionId(idShow, funcion.id)
    .filter { !it.estaVendida && it.ubicacion == ubi }  // ❌ Filtrado en memoria
```

**Problemas:**
- Carga TODAS las entradas de la función en memoria
- Filtra en memoria por ubicación y estado vendida
- Desperdicio de memoria y CPU

**DESPUÉS (✅ Optimizado):**
```kotlin
val entradasDisponibles = repositorioEntrada
    .findEntradasDisponibles(idShow, funcion.id, ubi)  // ✅ Filtrado en DB
```

**Query creada:**
```sql
SELECT e 
FROM Entrada e 
WHERE e.showId = :showId 
AND e.funcionId = :funcionId 
AND e.ubicacion = :ubicacion 
AND e.estaVendida = false
```

**Mejora:** Filtrado en base de datos en lugar de memoria ✅

---

### 4. Conteo de Entradas en Memoria
**Ubicación:** `RecaudacionService.entradasVendidasPorUbicacion()`

**ANTES (❌ Ineficiente):**
```kotlin
fun entradasVendidasPorUbicacion(ubicacion: Ubicacion, entradas: List<Entrada>): Int {
    return entradas.filter { 
        it.ubicacion == ubicacion && it.estaVendida  // ❌ Filtrado en memoria
    }.size
}

fun obtenerEstadisticasPorUbicacion(showId: String): Map<Ubicacion, Int> {
    val entradas = repositorioEntrada.entradasVendidasByShowId(showId)  // ❌ Carga todas
    return instalacion.categorias.associateWith { ubicacion ->
        entradasVendidasPorUbicacion(ubicacion, entradas)  // ❌ Filtra cada ubicación
    }
}
```

**Problemas:**
- Carga TODAS las entradas vendidas del show
- Itera múltiples veces sobre la misma lista
- Filtrado en memoria para cada ubicación

**DESPUÉS (✅ Optimizado):**
```kotlin
fun entradasVendidasPorUbicacion(showId: String, ubicacion: Ubicacion): Long {
    return repositorioEntrada.contarEntradasVendidasPorUbicacion(showId, ubicacion)
}

fun obtenerEstadisticasPorUbicacion(showId: String): Map<Ubicacion, Int> {
    return instalacion.categorias.associateWith { ubicacion ->
        entradasVendidasPorUbicacion(showId, ubicacion).toInt()  // ✅ Query por ubicación
    }
}
```

**Query creada:**
```sql
SELECT COUNT(e) 
FROM Entrada e 
WHERE e.showId = :showId 
AND e.ubicacion = :ubicacion 
AND e.estaVendida = true
```

**Mejora:** Conteo en base de datos, sin cargar entradas en memoria ✅

---

### 5. Verificación de Función Agotada en Memoria
**Ubicación:** `CompraService.comprarEntradas()`

**ANTES (❌ Ineficiente):**
```kotlin
funciones.forEach { funcion ->
    val entradasFuncion = repositorioEntrada.findEntradaByFuncionId(funcion.id)
    if (entradasFuncion.all { entrada -> entrada.estaVendida }) {  // ❌ Verifica en memoria
        funcion.funcionAgotada()
    }
}
```

**Problemas:**
- Carga TODAS las entradas de la función
- Verifica en memoria si todas están vendidas
- Múltiples queries innecesarias

**DESPUÉS (✅ Optimizado):**
```kotlin
show.funciones.forEach { funcion ->
    if (repositorioEntrada.todasEntradasVendidasPorFuncion(funcion.id)) {  // ✅ Query
        funcion.funcionAgotada()
    }
}
```

**Query creada:**
```sql
SELECT CASE WHEN COUNT(e) = 0 THEN true ELSE false END
FROM Entrada e 
WHERE e.funcionId = :funcionId 
AND e.estaVendida = false
```

**Mejora:** Verificación booleana en DB sin cargar datos ✅

---

### 6. Filtrado de Shows con Amigos en Memoria
**Ubicación:** `ServiceShow.getShows()`

**ANTES (❌ Ineficiente):**
```kotlin
val repoShows = showRepository.findFilteredShows(idUsuario, artista, locacion, LocalDate.now())
val filteredShows = if (conAmigos == true) {
    repoShows.filter { show ->  // ❌ Filtra en memoria
        getListaAmigosVanAShow(idUsuario, show.id).isNotEmpty()  // ❌ Query por cada show
    }
} else repoShows
```

**Problemas:**
- Carga TODOS los shows filtrados
- Para cada show, hace una query para ver si amigos van
- Filtrado en memoria
- Problema N+1 a nivel de shows

**DESPUÉS (✅ Optimizado):**
```kotlin
val repoShows = if (conAmigos == true && idUsuario != null) {
    val showIdsConAmigos = usuarioComunRepository.findShowIdsConAmigos(idUsuario)  // ✅ 1 query
    if (showIdsConAmigos.isEmpty()) {
        emptyList()
    } else {
        showRepository.findShowsByIdsAndFilters(showIdsConAmigos, artista, locacion, LocalDate.now())
    }
} else {
    showRepository.findFilteredShows(idUsuario, artista, locacion, LocalDate.now())
}
```

**Queries creadas:**

**Query 1 - Obtener IDs de shows con amigos (JPA):**
```sql
SELECT DISTINCT entrada.showId 
FROM UsuarioComun u 
JOIN u.amigos amigoId 
JOIN UsuarioComun amigo ON amigo.id = amigoId 
JOIN amigo.entradasCompradas entrada 
WHERE u.id = :idUsuario
```

**Query 2 - Filtrar shows por IDs (MongoDB):**
```javascript
{
    '_id': {$in: showIds}, 
    'nombreBanda': {$regex: artista, $options: 'i'}, 
    'nombreInstalacion': {$regex: locacion, $options: 'i'}, 
    'funciones': {$elemMatch: {'fecha': {$gte: fecha}}}
}
```

**Mejora:** De N+1 queries a 2 queries optimizadas ✅

---

## 📊 Resumen de Queries Creadas

### RepositorioUsuarioComun (JPA)

| Query | Propósito | Optimización |
|-------|-----------|--------------|
| `findAmigosByUsuarioId()` | Obtener todos los amigos | Evita N+1 |
| `findAmigosQueVanAShowOptimizado()` | Amigos que van a un show | JOIN optimizado |
| `findShowIdsConAmigos()` | IDs de shows con amigos | Para filtrado en MongoDB |

### RepositorioEntradas (JPA)

| Query | Propósito | Optimización |
|-------|-----------|--------------|
| `findEntradasDisponibles()` | Entradas no vendidas por ubicación | Filtrado en DB |
| `contarEntradasVendidasPorUbicacion()` | Contar vendidas por ubicación | COUNT en DB |
| `todasEntradasVendidasPorFuncion()` | Verificar si función agotada | Booleano en DB |

### RepositorioShow (MongoDB)

| Query | Propósito | Optimización |
|-------|-----------|--------------|
| `findShowsByIdsAndFilters()` | Shows por lista de IDs | Evita cargar todos |

## 📈 Impacto en Rendimiento

### Escenario 1: Usuario con 50 amigos lista sus amigos
- **Antes:** 51 queries (1 + 50)
- **Después:** 1 query
- **Mejora:** 98% menos queries ✅

### Escenario 2: Usuario con 50 amigos ve shows con amigos
- **Antes:** ~200+ queries (1 inicial + 100 shows × 2 queries c/u)
- **Después:** 2 queries
- **Mejora:** 99% menos queries ✅

### Escenario 3: Agregar entradas al carrito
- **Antes:** 1 query + filtrado en memoria de 1000+ entradas
- **Después:** 1 query filtrada
- **Mejora:** 100% del filtrado en DB ✅

### Escenario 4: Estadísticas de show con 5 ubicaciones
- **Antes:** 1 query + 5 filtrados en memoria sobre 5000 entradas
- **Después:** 5 queries COUNT optimizadas
- **Mejora:** 0 entradas cargadas en memoria ✅

### Escenario 5: Comprar entradas de 3 shows con 2 funciones c/u
- **Antes:** Carga todas las entradas de 6 funciones, verifica en memoria
- **Después:** 6 queries booleanas rápidas
- **Mejora:** 0 entradas cargadas en memoria ✅

## 🎯 Beneficios Obtenidos

### 1. Rendimiento ⬆️
- Menos datos transferidos entre DB y aplicación
- Menos memoria consumida
- Queries más rápidas con índices de DB

### 2. Escalabilidad ⬆️
- El sistema soporta más usuarios simultáneos
- Menos carga en el servidor de aplicación
- Mejor uso de recursos de DB

### 3. Mantenibilidad ⬆️
- Código más simple y directo
- Queries documentadas en repositorios
- Menos lógica de negocio en servicios

### 4. Correctitud ⬆️
- Menos errores por concurrencia
- Datos siempre actualizados desde DB
- No hay cache implícito en memoria

## 🔧 Patrón de Optimización Aplicado

### Antes (Anti-patrón):
```kotlin
// ❌ Cargar todo -> Filtrar en memoria
fun metodo() {
    val todos = repositorio.findAll()
    val filtrados = todos.filter { /* condición */ }
    return filtrados.map { /* transformación */ }
}
```

### Después (Patrón optimizado):
```kotlin
// ✅ Filtrar en DB
fun metodo() {
    return repositorio.findConFiltro(condicion)
}
```

## 📝 Guía para Futuros Desarrollos

### ❌ EVITAR:
```kotlin
// NO hacer esto
val items = repositorio.findAll()
return items.filter { it.activo }
         .map { convertir(it) }
         .take(10)
```

### ✅ HACER:
```kotlin
// Crear query específica
@Query("SELECT i FROM Item i WHERE i.activo = true")
fun findActivosLimit10(): List<Item>
```

### Checklist antes de escribir código:

- [ ] ¿Estoy cargando datos que luego filtro con `.filter()`?
- [ ] ¿Estoy haciendo queries dentro de un `.forEach()` o `.map()`?
- [ ] ¿Puedo mover esta lógica a una query SQL/MongoDB?
- [ ] ¿Estoy usando `.count()` o `.size` sobre colecciones grandes?
- [ ] ¿Puedo usar `COUNT()` en la query en su lugar?

## 🧪 Testing de Rendimiento

### Recomendaciones:

1. **Usar @DataJpaTest** para tests de repositorio
```kotlin
@DataJpaTest
class RepositorioTest {
    @Test
    fun `findAmigosByUsuarioId debe hacer 1 sola query`() {
        // Usar contador de queries o logs
    }
}
```

2. **Habilitar logs de queries** en desarrollo:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```

3. **Usar herramientas de profiling:**
- JPA/Hibernate Statistics
- MongoDB Profiler
- Spring Boot Actuator

## 📚 Referencias

- [N+1 Query Problem](https://www.baeldung.com/hibernate-common-performance-problems-in-logs)
- [Spring Data JPA Best Practices](https://thoughts-on-java.org/5-common-spring-data-jpa-mistakes/)
- [MongoDB Query Optimization](https://www.mongodb.com/docs/manual/core/query-optimization/)

---

**Conclusión:** Todas las operaciones de filtrado y mapeo ahora se realizan en la base de datos, resultando en un sistema significativamente más eficiente y escalable.

**Fecha de optimización:** Enero 2026  
**Estado:** ✅ Completado  
**Impacto:** Alto - Mejora sustancial en rendimiento
