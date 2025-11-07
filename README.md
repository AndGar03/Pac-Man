# Proyecto Pac-Man Client-Servidor

## Descripción

Este es un proyecto universitario que implementa una versión simplificada del juego Pac-Man con arquitectura Cliente-Servidor, siguiendo estrictamente los principios SOLID y el patrón MVC.

## Arquitectura

El proyecto sigue una arquitectura MVC estricta con separación de capas:
- **Modelo**: Lógica de negocio sin I/O
- **Vista**: Interfaz gráfica sin lógica de negocio
- **Controlador**: Coordinación entre modelo y vista
- **DAO**: Patrón Data Access Object para acceso a base de datos
- **Util**: Utilidades para configuración y manejo de sockets

## Estructura del Proyecto

```
src/main/java/com/miempresa/pacman/
├── cliente/
│   ├── controlador/  (ClientController, ClientLauncher)
│   ├── modelo/       (EstadoConexion)
│   └── vista/        (ClientWindow)
├── servidor/
│   ├── controlador/  (ServerController, ClientHandler, ManejadorResultados, Launcher)
│   ├── modelo/       (PacMan, Item, Jugador, Juego)
│   └── vista/        (GamePanel, ServerWindow)
├── dao/              (IJugadorDAO, JugadorDAOImpl)
└── util/             (Configuracion, ManejadorSockets)
```

## Requisitos Previos

1. Java JDK 8 o superior
2. MySQL Server
3. MySQL Connector/J (driver JDBC para MySQL)

## Configuración

### Base de Datos

1. Crear la base de datos ejecutando el script:
   ```sql
   src/main/resources/data/db_script.sql
   ```

2. Asegúrate de que MySQL esté corriendo y accesible.

### Archivos de Propiedades

#### Servidor (`src/main/resources/data/server.properties`)
```properties
server.port=8888
db.url=jdbc:mysql://localhost:3306/pacman_db
db.user=root
db.password=root
users.to.load=player1:pass1,player2:pass2,admin:admin123
```

#### Cliente (`src/main/resources/data/client.properties`)
```properties
server.ip=localhost
server.port=8888
```

## Ejecución

### Servidor

1. Compilar el proyecto
2. Ejecutar la clase `Launcher` desde el paquete `com.miempresa.pacman.servidor.controlador`
   ```bash
   java com.miempresa.pacman.servidor.controlador.Launcher
   ```

### Cliente

1. Compilar el proyecto
2. Ejecutar la clase `ClientLauncher` desde el paquete `com.miempresa.pacman.cliente.controlador`
   ```bash
   java com.miempresa.pacman.cliente.controlador.ClientLauncher
   ```

## Funcionalidad

### Servidor

- Escucha conexiones en el puerto configurado
- Autentica clientes usando la base de datos
- Muestra el juego en su interfaz gráfica
- Maneja múltiples clientes concurrentemente
- Guarda resultados en RandomAccessFile
- Muestra el mejor jugador al cerrar

### Cliente

- Se conecta al servidor usando un archivo de propiedades (seleccionado con JFileChooser)
- Se autentica con usuario y contraseña
- Envía comandos de movimiento: "arriba", "abajo", "izquierda", "derecha"
- Recibe mensajes del servidor sobre el estado del juego

## Características del Juego

- Pac-Man aparece en el centro del área de juego
- Se seleccionan 4 ítems aleatoriamente de 8 disponibles
- El objetivo es encontrar los 4 ítems
- El movimiento es de 4 píxeles por comando
- Si Pac-Man toca un borde, se detiene inmediatamente
- El juego termina cuando se recogen los 4 ítems
- Los resultados se guardan con nombre, puntaje y tiempo

## Ítems y Puntajes

- Cereza: 100 puntos
- Fresa: 300 puntos
- Naranja: 500 puntos
- Manzana: 700 puntos
- Melón: 1000 puntos
- Galaxian: 2000 puntos
- Campana: 3000 puntos
- Llave: 5000 puntos

## Principios SOLID Implementados

- **S (Single Responsibility)**: Cada clase tiene una única responsabilidad
- **O (Open/Closed)**: El diseño está abierto a extensión, cerrado a modificación
- **L (Liskov Substitution)**: Los subtipos son sustituibles por sus tipos base
- **I (Interface Segregation)**: Interfaces específicas y cohesivas
- **D (Dependency Inversion)**: Dependencia de abstracciones (interfaces), no implementaciones

## Patrón MVC

- **Modelo**: No contiene I/O, solo lógica de negocio
- **Vista**: No contiene lógica de negocio, solo presentación
- **Controlador**: Coordina entre modelo y vista, maneja eventos

## Notas

- El servidor debe estar ejecutándose antes de que el cliente se conecte
- Los usuarios se cargan automáticamente desde el archivo de propiedades del servidor
- Los resultados se guardan en `resultados.dat` en el directorio de ejecución
- El mejor jugador se determina por mayor puntaje, y en caso de empate, menor tiempo

"# Pac-Man" 
