# Arquitectura de Servicios - Antes y Después

## 🔴 ANTES - Arquitectura Monolítica

```
┌─────────────────────────────────────────────────────────────┐
│                    UsuarioController                        │
│  (Dependía de ServiceUsuario Y ServiceShow)                 │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
        ┌───────────────────────────────┐
        │      ServiceUsuario           │
        │   (242 líneas - 8 repos)      │
        │                               │
        │  ❌ Autenticación             │
        │  ❌ Gestión de usuarios       │
        │  ❌ Gestión de carritos       │
        │  ❌ Proceso de compra         │
        │  ❌ Gestión de amistades      │
        │  ❌ Gestión de comentarios    │
        │  ❌ Gestión de crédito        │
        │  ❌ Sincronización Neo4j      │
        └───────────────────────────────┘

        ┌───────────────────────────────┐
        │      ServiceShow              │
        │   (184 líneas - 6 repos)      │
        │                               │
        │  ❌ CRUD de Shows             │
        │  ❌ Cálculos financieros      │
        │  ❌ Estadísticas              │
        │  ❌ Gestión de logs           │
        │  ❌ Lista de espera           │
        └───────────────────────────────┘

PROBLEMAS:
- Violación de SRP
- Alto acoplamiento
- Difícil de testear
- Baja cohesión
- Muchas razones para cambiar
```

## ✅ DESPUÉS - Arquitectura con Separación de Responsabilidades

```
┌─────────────────────────────────────────────────────────────┐
│                    UsuarioController                        │
│         (Usa múltiples servicios especializados)            │
└─────┬───┬────┬────┬─────┬──────┬─────────┬─────────────────┘
      │   │    │    │     │      │         │
      │   │    │    │     │      │         │
      ▼   ▼    ▼    ▼     ▼      ▼         ▼
    ┌───┐┌──┐┌───┐┌────┐┌────┐┌─────┐┌──────────┐
    │ A ││ C││ Co││ Am ││ Cm ││ SU  ││ ServiceS │
    │ u ││ a││ m ││ i  ││ e  ││ s   ││ h        │
    │ t ││ r││ p ││ s  ││ n  ││ u   ││ o        │
    │ h ││ r││ r ││ t  ││ t  ││ a   ││ w        │
    └───┘└──┘└───┘└────┘└────┘└─────┘└──────────┘

┌──────────────────────────────────────────────────────────────┐
│                    ShowController                            │
│            (Usa ServiceShow refactorizado)                   │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
                 ┌───────────────┐
                 │ ServiceShow   │
                 │ (Refactorizado)│
                 └───────┬───────┘
                         │
                         │ (delega cálculos)
                         ▼
                 ┌───────────────┐
                 │ Recaudacion   │
                 │ Service       │
                 └───────────────┘
```

## 📦 Servicios Especializados

### 1. AuthenticationService
```
Responsabilidad: Autenticación
├── loginUsuario()
└── Repositorios: 2
```

### 2. CarritoService
```
Responsabilidad: Gestión de Carritos
├── getCarritoById()
├── agregarAlCarrito()
├── obtenerEntradasCarrito()
├── vaciarCarrito()
└── Repositorios: 4
```

### 3. CompraService
```
Responsabilidad: Proceso de Compra
├── comprarEntradas()
├── verificarPrecio()
├── obtenerEntradasCompradas()
└── Repositorios: 5 + CarritoService
```

### 4. AmistadService
```
Responsabilidad: Gestión de Amistades
├── listaAmigos()
├── agregarAmigo()
├── quitarAmigo()
├── amiguesSugeridos()
├── amigosQueVanAShow()
└── Repositorios: 2
```

### 5. ComentarioService
```
Responsabilidad: Comentarios y Ratings
├── listaComentarios()
├── traerComentarios()
├── dejarComentario()
├── borrarComentario()
└── Repositorios: 3
```

### 6. RecaudacionService
```
Responsabilidad: Cálculos Financieros
├── calcularRecaudacionShow()
├── calcularRentabilidadShow()
├── entradasVendidasPorUbicacion()
├── obtenerEstadisticasPorUbicacion()
└── Repositorios: 3
```

### 7. ServiceUsuario (Refactorizado)
```
Responsabilidad: Operaciones Básicas de Usuario
├── getUserByID()
├── getUserNeoByName()
├── getDataUserByID()
├── editarDatos()
├── sumarCredito()
└── Repositorios: 2 (antes 8!)
```

### 8. ServiceShow (Refactorizado)
```
Responsabilidad: Operaciones de Shows
├── getShowByID()
├── getInstalacionByID()
├── getListaAmigosVanAShow()
├── getShows()
├── getShowAdmin() ──┐
├── getShowDetalles()│
├── deleteShow()     │
├── editarDatos()    │
├── crearFuncion()   │
└── agregarAUsuarioAEspera()
    registrarLogClick()
                     │
                     └──> Usa RecaudacionService
```

## 🔄 Flujo de Dependencias

### Compra de Entradas (Ejemplo)
```
UsuarioController
      │
      └──> CompraService.comprarEntradas()
                │
                ├──> CarritoService.getCarritoById()
                ├──> RepositorioUsuarioComun
                ├──> RepositorioUsuarioNeo
                ├──> RepositorioShow
                ├──> RepositorioShowNeo
                ├──> RepositorioEntrada
                └──> CarritoService.vaciarCarrito()
```

### Dejar Comentario (Ejemplo)
```
UsuarioController
      │
      └──> ComentarioService.dejarComentario()
                │
                ├──> RepositorioUsuarioComun
                ├──> RepositorioShow
                └──> actualizarPuntajeShow() (interno)
                      │
                      └──> RepositorioComentarios
```

### Vista Admin de Shows (Ejemplo)
```
ShowController
      │
      └──> ServiceShow.getShowAdmin()
                │
                ├──> RepositorioUsuarioAdmin
                ├──> RepositorioShow
                ├──> RepositorioInstalacion
                └──> RecaudacionService
                      ├──> calcularRecaudacionShow()
                      └──> calcularRentabilidadShow()
```

## 📊 Comparación de Complejidad

### Antes
```
ServiceUsuario
├── Complejidad ciclomática: ALTA
├── Cohesión: BAJA
├── Acoplamiento: ALTO (8 repositorios)
└── Líneas de código: 242
```

### Después
```
AuthenticationService
├── Complejidad ciclomática: BAJA
├── Cohesión: ALTA
├── Acoplamiento: BAJO (2 repositorios)
└── Líneas de código: 41

CarritoService
├── Complejidad ciclomática: MEDIA
├── Cohesión: ALTA
├── Acoplamiento: MEDIO (4 repositorios)
└── Líneas de código: 95

[... otros servicios ...]

ServiceUsuario (Refactorizado)
├── Complejidad ciclomática: BAJA
├── Cohesión: ALTA
├── Acoplamiento: BAJO (2 repositorios)
└── Líneas de código: 86
```

## 🎯 Ventajas de la Nueva Arquitectura

### 1. Mantenibilidad ⬆️
- Cambios localizados
- Código más legible
- Responsabilidades claras

### 2. Testabilidad ⬆️
- Servicios pequeños y enfocados
- Fácil crear mocks
- Tests más simples

### 3. Escalabilidad ⬆️
- Fácil agregar funcionalidades
- Servicios independientes
- Mejor organización

### 4. Reutilización ⬆️
- Servicios reutilizables
- Lógica encapsulada
- Menos duplicación

### 5. Desacoplamiento ⬆️
- Menos dependencias por clase
- Interfaces claras
- Bajo acoplamiento

## 🔍 Análisis de Impacto

### Sin Impacto en:
- ✅ APIs públicas (endpoints mantienen firmas)
- ✅ DTOs y modelos de dominio
- ✅ Repositorios existentes
- ✅ Configuración de base de datos

### Con Mejora en:
- ✅ Estructura interna de servicios
- ✅ Inyección de dependencias
- ✅ Separación de responsabilidades
- ✅ Calidad del código

## 📈 Matriz de Responsabilidades

| Responsabilidad | Antes | Después |
|----------------|-------|---------|
| Autenticación | ServiceUsuario | AuthenticationService |
| Gestión Carrito | ServiceUsuario | CarritoService |
| Proceso Compra | ServiceUsuario | CompraService |
| Gestión Amigos | ServiceUsuario | AmistadService |
| Comentarios | ServiceUsuario | ComentarioService |
| Cálculos $ | ServiceShow | RecaudacionService |
| Datos Usuario | ServiceUsuario | ServiceUsuario (limpio) |
| Gestión Shows | ServiceShow | ServiceShow (limpio) |

---

**Conclusión:** La arquitectura refactorizada sigue los principios SOLID, 
especialmente SRP, resultando en código más mantenible, testeable y escalable.
