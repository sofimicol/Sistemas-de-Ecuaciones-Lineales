@echo off
rem Descarga JavaFX (solo la primera vez), compila y ejecuta la interfaz. Uso: doble clic, o "ejecutar" en la raiz del repo.
setlocal
cd /d "%~dp0"
set "VERSION=21.0.5"
set "FX=lib\javafx"

where java >nul 2>nul || (echo No se encontro java en el PATH. Instalar un JDK 17 o superior. & goto :error)

if not exist "%FX%\javafx-controls-%VERSION%-win.jar" (
    echo Descargando JavaFX %VERSION% para Windows...
    if exist "%FX%" rmdir /s /q "%FX%"
    mkdir "%FX%"
    for %%m in (base graphics controls) do (
        curl.exe -fL -o "%FX%\javafx-%%m-%VERSION%-win.jar" "https://repo1.maven.org/maven2/org/openjfx/javafx-%%m/%VERSION%/javafx-%%m-%VERSION%-win.jar" || goto :error
    )
)

javac -encoding UTF-8 --module-path "%FX%" --add-modules javafx.controls -sourcepath src -d out\app src\vista\AppGUI.java || goto :error
java --enable-native-access=javafx.graphics --module-path "%FX%" --add-modules javafx.controls -cp out\app vista.AppGUI || goto :error
exit /b 0

:error
echo.
echo No se pudo ejecutar la aplicacion. Revisar el mensaje de arriba.
pause
exit /b 1
