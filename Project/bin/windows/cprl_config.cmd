@echo off

rem
rem Configuration settings for the CPRL compiler project.
rem

rem These settings assume an Eclipse workspace named "workspace" with a project
rem named "compiler".  Class files are placed in the Eclipse default "bin" directory.


rem set EXAMPLES_HOME to the directory for the test examples
set EXAMPLES_HOME=C:\Compilers\examples

rem set PROJECT_HOME to the directory for your compiler project
set PROJECT_HOME=C:\Compilers\workspace

set COMPILER_HOME=%PROJECT_HOME%\compiler

rem set BIN to the directory name used for compiled Java classes (e.g., bin)
set BIN=bin

rem Add project-related class directory to COMPILER_PROJECT_PATH.
set COMPILER_PROJECT_PATH=%COMPILER_HOME%\%BIN%
