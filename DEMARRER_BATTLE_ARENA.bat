@echo off
setlocal enabledelayedexpansion
title SkillQuest - Battle Arena
echo =====================================================
echo   SKILLQUEST BATTLE ARENA - Demarrage
echo =====================================================

REM --- CONFIGURATION ---
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_301
set JAVAC="%JAVA_HOME%\bin\javac.exe"
set JAVAXE="%JAVA_HOME%\bin\java.exe"
set MYSQL_JAR=%USERPROFILE%\.m2\repository\mysql\mysql-connector-java\5.1.47\mysql-connector-java-5.1.47.jar

cd /d "%~dp0"

REM --- COMPILATION ---
echo [1/3] Compilation...
if not exist "target\classes" mkdir "target\classes"
dir /s /b "src\main\java\*.java" > sources_list.txt
%JAVAC% -encoding UTF-8 -source 1.8 -target 1.8 -d "target\classes" -cp "%MYSQL_JAR%" @sources_list.txt
del sources_list.txt

if %ERRORLEVEL% NEQ 0 (
    echo ERREUR DE COMPILATION !
    pause
    exit /b 1
)

REM --- RESSOURCES ---
echo [2/3] Copie des ressources...
xcopy /E /Y /I /Q "src\main\resources" "target\classes" >nul 2>nul

REM --- LANCEMENT ---
echo [3/3] Lancement...
set CLASSPATH=target\classes;%MYSQL_JAR%
%JAVAXE% -cp "%CLASSPATH%" main.AppLauncher

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERREUR: Verifiez que MySQL (XAMPP) est demarre !
    pause
)
