# Script para probar conexion final a MySQL con la contraseña proporcionada
$mysqlPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

Write-Host "=== Probando Conexion Final a MySQL ===" -ForegroundColor Cyan
Write-Host ""

$dbUser = "root"
$dbPassword = "TomateRojo"

Write-Host "Configuracion:" -ForegroundColor Yellow
Write-Host "  Usuario: $dbUser" -ForegroundColor White
Write-Host "  Contraseña: [configurada]" -ForegroundColor White
Write-Host "  Host: localhost" -ForegroundColor White
Write-Host "  Puerto: 3306" -ForegroundColor White
Write-Host ""

# Crear archivo temporal con contraseña
$tempFile = [System.IO.Path]::GetTempFileName()
$passwordFile = $tempFile + ".cnf"
Remove-Item $tempFile -ErrorAction SilentlyContinue

@"
[client]
user=$dbUser
password=$dbPassword
host=localhost
port=3306
"@ | Out-File -FilePath $passwordFile -Encoding ASCII

Write-Host "Probando conexion..." -ForegroundColor Yellow
$result = & $mysqlPath --defaults-file="$passwordFile" -e "SELECT VERSION() as version, USER() as user, DATABASE() as current_db;" 2>&1

# Limpiar archivo temporal
Remove-Item $passwordFile -ErrorAction SilentlyContinue -Force

if ($LASTEXITCODE -eq 0 -or ($result -match "version" -and $result -notmatch "ERROR")) {
    Write-Host "[OK] CONEXION EXITOSA!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Informacion de MySQL:" -ForegroundColor Cyan
    $result | Where-Object { $_ -notmatch "Warning" -and $_ -notmatch "mysql:" -and $_ -notmatch "ERROR" -and $_ -notmatch "^\s*$" } | ForEach-Object {
        Write-Host "  $_" -ForegroundColor White
    }
    Write-Host ""
    
    # Verificar base de datos
    Write-Host "Verificando base de datos pacman_db..." -ForegroundColor Yellow
    $tempFile2 = [System.IO.Path]::GetTempFileName()
    $passwordFile2 = $tempFile2 + ".cnf"
    Remove-Item $tempFile2 -ErrorAction SilentlyContinue
    @"
[client]
user=$dbUser
password=$dbPassword
host=localhost
port=3306
"@ | Out-File -FilePath $passwordFile2 -Encoding ASCII
    
    $dbResult = & $mysqlPath --defaults-file="$passwordFile2" -e "SHOW DATABASES LIKE 'pacman_db';" 2>&1
    Remove-Item $passwordFile2 -ErrorAction SilentlyContinue -Force
    
    if ($dbResult -match "pacman_db") {
        Write-Host "[OK] La base de datos pacman_db existe" -ForegroundColor Green
        Write-Host ""
        
        # Verificar tabla usuarios
        Write-Host "Verificando tabla usuarios..." -ForegroundColor Yellow
        $tempFile3 = [System.IO.Path]::GetTempFileName()
        $passwordFile3 = $tempFile3 + ".cnf"
        Remove-Item $tempFile3 -ErrorAction SilentlyContinue
        @"
[client]
user=$dbUser
password=$dbPassword
host=localhost
port=3306
"@ | Out-File -FilePath $passwordFile3 -Encoding ASCII
        
        $tableResult = & $mysqlPath --defaults-file="$passwordFile3" pacman_db -e "SHOW TABLES LIKE 'usuarios';" 2>&1
        Remove-Item $passwordFile3 -ErrorAction SilentlyContinue -Force
        
        if ($tableResult -match "usuarios") {
            Write-Host "[OK] La tabla usuarios existe" -ForegroundColor Green
        } else {
            Write-Host "[ADVERTENCIA] La tabla usuarios NO existe" -ForegroundColor Yellow
        }
    } else {
        Write-Host "[ADVERTENCIA] La base de datos pacman_db NO existe" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Para crear la base de datos, ejecuta:" -ForegroundColor Yellow
        Write-Host "  mysql -u $dbUser -p$dbPassword -h localhost -P 3306 < src/main/resources/data/db_script.sql" -ForegroundColor White
        Write-Host ""
        Write-Host "O desde MySQL Workbench/Command Line:" -ForegroundColor Yellow
        Write-Host "  Ejecuta el contenido de: src/main/resources/data/db_script.sql" -ForegroundColor White
    }
    
    Write-Host ""
    Write-Host "=== Resumen ===" -ForegroundColor Cyan
    Write-Host "[OK] Configuracion de MySQL verificada correctamente" -ForegroundColor Green
    Write-Host "  - Host: localhost" -ForegroundColor White
    Write-Host "  - Puerto: 3306" -ForegroundColor White
    Write-Host "  - Usuario: $dbUser" -ForegroundColor White
    Write-Host "  - Contraseña: Configurada correctamente" -ForegroundColor White
    Write-Host "  - Archivo: src/main/resources/data/server.properties" -ForegroundColor White
    
} else {
    Write-Host "[ERROR] No se pudo conectar a MySQL" -ForegroundColor Red
    Write-Host ""
    Write-Host "Detalles del error:" -ForegroundColor Yellow
    $result | Where-Object { $_ -notmatch "Warning" } | ForEach-Object {
        Write-Host "  $_" -ForegroundColor Red
    }
}

