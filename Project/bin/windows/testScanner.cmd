@echo off

rem
rem Run TestScanner on the specified file.
rem

rem set config environment variables locally
setlocal
call cprl_config.cmd

set CLASSPATH=%COMPILER_PROJECT_PATH%
java -ea -cp "%CLASSPATH%" test.cprl.TestScanner %1

rem restore settings
endlocal
