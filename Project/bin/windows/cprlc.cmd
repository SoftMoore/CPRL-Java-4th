@echo off

rem
rem Run the CPRL compiler on one or more ".cprl" files.
rem

rem set config environment variables locally
setlocal
call cprl_config.cmd

set CLASSPATH=%COMPILER_PROJECT_PATH%
java -ea -cp "%CLASSPATH%" edu.citadel.cprl.Compiler %*

rem restore settings
endlocal
