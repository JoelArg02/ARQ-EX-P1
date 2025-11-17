@echo off
echo ========================================
echo   COMERCIALIZADORA MONSTER - Desktop
echo ========================================
echo.
echo Compilando aplicacion...
echo.

cd /d "%~dp0"
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo.
    echo ERROR: La compilacion fallo
    pause
    exit /b 1
)

echo.
echo ========================================
echo   Compilacion exitosa!
echo ========================================
echo.
echo Ejecutando aplicacion...
echo.

java -jar target\CLIESC_Comercializadora_RESTJAVA-1.0-SNAPSHOT.jar

if %errorlevel% neq 0 (
    echo.
    echo ERROR: No se pudo ejecutar la aplicacion
    echo.
    echo Verifica que:
    echo - Los servidores REST esten corriendo
    echo - Las bases de datos esten activas
    pause
)
