# Configuración de MySQL para Pac-Man

## Estado de la Detección

✅ **MySQL está instalado y corriendo en tu sistema:**
- Servicio: MySQL80
- Estado: Running
- Puerto: 3306 (verificado)
- Ubicación: C:\Program Files\MySQL

## Configuración Actual

La configuración en `server.properties` ha sido actualizada con los siguientes valores detectados:

```
db.url=jdbc:mysql://localhost:3306/pacman_db
db.user=root
db.password=root
```

## Verificación de Credenciales

**IMPORTANTE:** El usuario `root` es común, pero la contraseña puede variar según tu instalación.

### Para verificar tu contraseña de MySQL:

1. **Opción 1: Usar la utilidad Java de prueba**
   - Compila y ejecuta: `TestMySQLConnection.java`
   - Este script probará combinaciones comunes de usuario/contraseña

2. **Opción 2: Usar MySQL Command Line**
   - Abre MySQL Command Line Client desde el menú de inicio
   - Intenta conectarte con la contraseña que configuraste durante la instalación

3. **Opción 3: Usar el script de PowerShell**
   - Ejecuta: `test_mysql_connection.ps1`
   - Este script verificará la configuración de MySQL

### Si la contraseña no es "root":

Edita el archivo `src/main/resources/data/server.properties` y cambia:
```properties
db.password=tu_contraseña_aqui
```

Si no tienes contraseña para el usuario root, deja el campo vacío:
```properties
db.password=
```

## Crear la Base de Datos

Antes de ejecutar el servidor, necesitas crear la base de datos:

1. Abre MySQL Command Line Client o MySQL Workbench
2. Ejecuta el script: `src/main/resources/data/db_script.sql`

O desde la línea de comandos:
```bash
mysql -u root -p < src/main/resources/data/db_script.sql
```

## Probar la Conexión

### Método 1: Utilidad Java
```bash
# Compilar
javac -cp "ruta/a/mysql-connector-j.jar" src/main/java/com/miempresa/pacman/util/TestMySQLConnection.java

# Ejecutar
java -cp "ruta/a/mysql-connector-j.jar:." com.miempresa.pacman.util.TestMySQLConnection
```

### Método 2: Script PowerShell
```powershell
powershell -ExecutionPolicy Bypass -File test_mysql_connection.ps1
```

## Solución de Problemas

### Error: "Access denied for user 'root'@'localhost'"
- Verifica que la contraseña en `server.properties` sea correcta
- Si olvidaste la contraseña, consulta la documentación de MySQL para restablecerla

### Error: "Unknown database 'pacman_db'"
- Ejecuta el script `db_script.sql` para crear la base de datos
- Verifica que tengas permisos para crear bases de datos

### Error: "Connection refused" o "Cannot connect to MySQL server"
- Verifica que el servicio MySQL esté corriendo:
  ```powershell
  Get-Service MySQL80
  ```
- Si no está corriendo, inícialo:
  ```powershell
  Start-Service MySQL80
  ```

### Error: "No se encontró el driver de MySQL"
- Asegúrate de tener `mysql-connector-j.jar` en el classpath
- Descarga el conector desde: https://dev.mysql.com/downloads/connector/j/

## Resumen de Configuración Detectada

| Parámetro | Valor Detectado | Estado |
|-----------|----------------|--------|
| Host | localhost | ✅ Correcto |
| Puerto | 3306 | ✅ Verificado |
| Usuario | root | ⚠️ Verificar contraseña |
| Contraseña | root | ⚠️ Verificar si es correcta |
| Base de datos | pacman_db | ⚠️ Debe ser creada |

## Próximos Pasos

1. ✅ Verificar contraseña de MySQL (si es diferente de "root", actualizar `server.properties`)
2. ✅ Crear la base de datos ejecutando `db_script.sql`
3. ✅ Verificar que el driver MySQL Connector/J esté en el classpath
4. ✅ Probar la conexión usando `TestMySQLConnection.java`
5. ✅ Ejecutar el servidor Pac-Man

