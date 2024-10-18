@echo off

rem
rem Run the CPRL Virtual Machine (CVM) interpreter on a single ".obj" file.
rem

rem set config environment variables locally
setlocal
call cprl_config.cmd

set CLASSPATH=%COMPILER_PROJECT_PATH%
java -ea -cp "%CLASSPATH%" edu.citadel.cvm.CVM %1

rem restore settings
endlocal
