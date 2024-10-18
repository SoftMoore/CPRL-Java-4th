@echo off

rem
rem Run CPRL TestParser on one or more ".cprl" files.
rem

rem set config environment variables locally
setlocal
call cprl_config.cmd

set CLASSPATH=%COMPILER_PROJECT_PATH%
java -ea -cp "%CLASSPATH%" test.cprl.TestParser %*

rem restore settings
endlocal
