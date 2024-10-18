@echo off

rem
rem Run the CVM disassembler on one or more ".obj" files.
rem

rem set config environment variables locally
setlocal
call cprl_config.cmd

set CLASSPATH=%COMPILER_PROJECT_PATH%
java -ea -cp "%CLASSPATH%" edu.citadel.cvm.Disassembler %*

rem restore settings
endlocal
