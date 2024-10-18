@echo off

rem
rem Run the CVM Assembler on one or more ".asm" files.
rem

rem set config environment variables locally
setlocal
call cprl_config.cmd

rem The assembler permits the command-line switch -opt:off/-opt:on.

set CLASSPATH=%COMPILER_PROJECT_PATH%
java -ea -cp "%CLASSPATH%" edu.citadel.assembler.Assembler %*

rem restore settings
endlocal
