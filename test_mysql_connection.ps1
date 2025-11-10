# Script para probar conexion a MySQL
# Este script ayuda a detectar la configuracion de MySQL en tu sistema

Write-Host "=== Deteccion de Configuracion MySQL ===" -ForegroundColor Cyan
Write-Host ""

# Verificar si MySQL esta corriendo
Write-Host "1. Verificando si MySQL esta corriendo..." -ForegroundColor Yellow
$mysqlRunning = netstat -an | Select-String ":3306.*LISTENING"
if ($mysqlRunning) {
    Write-Host "   [OK] MySQL esta corriendo en el puerto 3306" -ForegroundColor Green
} else {
    Write-Host "   [ERROR] MySQL no parece estar corriendo en el puerto 3306" -ForegroundColor Red
    Write-Host "   Por favor, inicia el servicio MySQL" -ForegroundColor Yellow
    exit
}

Write-Host ""

# Buscar instalacion de MySQL
Write-Host "2. Buscando instalacion de MySQL..." -ForegroundColor Yellow
$mysqlPaths = @(
    "C:\Program Files\MySQL",
    "C:\Program Files (x86)\MySQL",
    "C:\xampp\mysql",
    "C:\wamp\bin\mysql"
)

$mysqlFound = $false
foreach ($path in $mysqlPaths) {
    if (Test-Path $path) {
        Write-Host "   [OK] MySQL encontrado en: $path" -ForegroundColor Green
        $mysqlFound = $true
        
        # Buscar archivo my.ini
        $configFiles = Get-ChildItem -Path $path -Recurse -Filter "my.ini" -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($configFiles) {
            Write-Host "   [OK] Archivo de configuracion encontrado: $($configFiles.FullName)" -ForegroundColor Green
            Write-Host "   Puedes revisar este archivo para ver el puerto configurado" -ForegroundColor Gray
        }
        break
    }
}

if (-not $mysqlFound) {
    Write-Host "   [ADVERTENCIA] No se encontro MySQL en las ubicaciones comunes" -ForegroundColor Yellow
    Write-Host "   MySQL esta corriendo pero no se pudo encontrar la instalacion" -ForegroundColor Gray
}

Write-Host ""

# Verificar servicios de MySQL
Write-Host "3. Verificando servicios de MySQL..." -ForegroundColor Yellow
$services = Get-Service | Where-Object {$_.DisplayName -like "*MySQL*" -or $_.Name -like "*mysql*"}
if ($services) {
    foreach ($service in $services) {
        $statusColor = if ($service.Status -eq "Running") { "Green" } else { "Yellow" }
        Write-Host "   Servicio: $($service.DisplayName) - Estado: $($service.Status)" -ForegroundColor $statusColor
    }
} else {
    Write-Host "   [ADVERTENCIA] No se encontraron servicios de MySQL con nombres estandar" -ForegroundColor Yellow
}

Write-Host ""

# Informacion de configuracion recomendada
Write-Host "4. Configuracion recomendada para server.properties:" -ForegroundColor Yellow
Write-Host ""
Write-Host "   db.url=jdbc:mysql://localhost:3306/pacman_db" -ForegroundColor White
Write-Host "   db.user=root" -ForegroundColor White
Write-Host "   db.password=[TU_CONTRASENA]" -ForegroundColor White
Write-Host ""
Write-Host "   NOTA: El puerto 3306 es correcto segun la verificacion." -ForegroundColor Gray
Write-Host "   Debes configurar el usuario y contrasena segun tu instalacion de MySQL." -ForegroundColor Gray
Write-Host ""

# Intentar encontrar mysql.exe para probar conexion
Write-Host "5. Intentando probar conexion..." -ForegroundColor Yellow
$mysqlExe = Get-Command mysql -ErrorAction SilentlyContinue
if ($mysqlExe) {
    Write-Host "   [OK] Cliente MySQL encontrado: $($mysqlExe.Source)" -ForegroundColor Green
    Write-Host "   Puedes probar la conexion manualmente con:" -ForegroundColor Gray
    Write-Host "   mysql -u root -p -h localhost -P 3306" -ForegroundColor White
} else {
    Write-Host "   [ADVERTENCIA] Cliente MySQL no esta en el PATH" -ForegroundColor Yellow
    Write-Host "   Puedes probar la conexion usando la utilidad Java TestMySQLConnection" -ForegroundColor Gray
}

Write-Host ""
Write-Host "=== Resumen ===" -ForegroundColor Cyan
Write-Host "Host: localhost" -ForegroundColor White
Write-Host "Puerto: 3306" -ForegroundColor White
Write-Host "Base de datos: pacman_db (debe ser creada)" -ForegroundColor White
Write-Host "Usuario: root (comun, verifica en tu instalacion)" -ForegroundColor White
Write-Host "Contrasena: [Verifica en tu instalacion de MySQL]" -ForegroundColor White
Write-Host ""
Write-Host "Para crear la base de datos, ejecuta el script:" -ForegroundColor Yellow
Write-Host "src/main/resources/data/db_script.sql" -ForegroundColor White
Write-Host ""
