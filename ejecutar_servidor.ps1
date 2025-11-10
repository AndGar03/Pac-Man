# Script para ejecutar el servidor Pac-Man
Write-Host "=== Ejecutando Servidor Pac-Man ===" -ForegroundColor Cyan
Write-Host ""

$mysqlDriver = "lib/mysql-connector-j-8.0.33.jar"
$classpath = "build/classes;$mysqlDriver"
$mainClass = "com.miempresa.pacman.servidor.controlador.Launcher"

# Verificar que el driver MySQL existe
if (-not (Test-Path $mysqlDriver)) {
    Write-Host "[ERROR] No se encontro el driver MySQL: $mysqlDriver" -ForegroundColor Red
    Write-Host "Descargando driver MySQL..." -ForegroundColor Yellow
    if (-not (Test-Path "lib")) {
        New-Item -ItemType Directory -Path "lib" | Out-Null
    }
    try {
        Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar" -OutFile $mysqlDriver -ErrorAction Stop
        Write-Host "[OK] Driver MySQL descargado" -ForegroundColor Green
    } catch {
        Write-Host "[ERROR] No se pudo descargar el driver MySQL" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        exit 1
    }
}

# Verificar que las clases estan compiladas
if (-not (Test-Path "build/classes/com/miempresa/pacman/servidor/controlador/Launcher.class")) {
    Write-Host "[ADVERTENCIA] Las clases no estan compiladas. Compilando..." -ForegroundColor Yellow
    Write-Host "Ejecuta: javac -d build/classes -cp `"$mysqlDriver`" -sourcepath src/main/java src/main/java/com/miempresa/pacman/**/*.java" -ForegroundColor Gray
}

Write-Host "Ejecutando servidor..." -ForegroundColor Yellow
Write-Host "Classpath: $classpath" -ForegroundColor Gray
Write-Host "Clase principal: $mainClass" -ForegroundColor Gray
Write-Host ""

# Ejecutar el servidor
java -cp $classpath $mainClass

