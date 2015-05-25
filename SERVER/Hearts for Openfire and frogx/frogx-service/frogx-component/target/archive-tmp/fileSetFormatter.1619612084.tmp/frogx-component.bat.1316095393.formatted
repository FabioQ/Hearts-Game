@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM frogx-component start script
@REM
@REM This is a modified version of the maven2 start script.
@REM
@REM Required environment variable:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional variables:
@REM FROGX_HOME - location of frogx-component's installed home dir
@REM FROGX_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM FROGX_BATCH_PAUSE - set to 'on' to wait for a key stroke before ending
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with '@' in case FROGX_BATCH_ECHO is 'on'
@echo off
@REM enable echoing my setting FROGX_BATCH_ECHO to 'on'
@if "%FROGX_BATCH_ECHO%" == "on"  echo %FROGX_BATCH_ECHO%

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set "HOME=%HOMEDRIVE%%HOMEPATH%")

set ERROR_CODE=0

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto chkHome

echo.
echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = "%JAVA_HOME%"
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:chkHome
if not "%FROGX_HOME%"=="" goto valMHome

if "%OS%"=="Windows_NT" SET "FROGX_HOME=%~dp0.."
if "%OS%"=="WINNT" SET "FROGX_HOME=%~dp0.."
if not "%FROGX_HOME%"=="" goto valHome

echo.
echo ERROR: FROGX_HOME not found in your environment.
echo Please set the FROGX_HOME variable in your environment to match the
echo location of the frogx-component installation
echo.
goto error

:valHome

:stripHome
if not "_%FROGX_HOME:~-1%"=="_\" goto checkBat
set "FROGX_HOME=%FROGX_HOME:~0,-1%"
goto stripHome

:checkBat
if exist "%FROGX_HOME%\bin\frogx-component.bat" goto init

echo.
echo ERROR: FROGX_HOME is set to an invalid directory.
echo FROGX_HOME = "%FROGX_HOME%"
echo Please set the FROGX_HOME variable in your environment to match the
echo location of the frogx-component installation
echo.
goto error
@REM ==== END VALIDATION ====

:init
@REM Decide how to startup depending on the version of windows

@REM -- Windows NT with Novell Login
if "%OS%"=="WINNT" goto WinNTNovell

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

:WinNTNovell

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set FROGX_CMD_LINE_ARGS=%*
goto endInit

@REM The 4NT Shell from jp software
:4NTArgs
set FROGX_CMD_LINE_ARGS=%$
goto endInit

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of agruments (up to the command line limit, anyway).
set FROGX_CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto endInit
set FROGX_CMD_LINE_ARGS=%FROGX_CMD_LINE_ARGS% %1
shift
goto Win9xApp

@REM Reaching here means variables are defined and arguments have been captured
:endInit
SET FROGX_JAVA_EXE="%JAVA_HOME%\bin\java.exe"

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTCWJars

@REM -- Regular WinNT shell
set FROGX_JARS=
for %%i in ("%FROGX_HOME%"\lib\frogx-component*.jar) do set FROGX_JARS="%%i"
goto run

@REM The 4NT Shell from jp software
:4NTCWJars
for %%i in ("%FROGX_HOME%"\lib\frogx-component*.jar) do set FROGX_JARS="%%i"
)
goto run

@REM Start frogx-component
:run
@REM echo %FROGX_JAVA_EXE% %FROGX_OPTS% "-Dfrogx.component.home=%FROGX_HOME%" -jar %FROGX_JARS% %FROGX_COMPONENT% %FROGX_CMD_LINE_ARGS%
%FROGX_JAVA_EXE% %FROGX_OPTS% "-Dfrogx.component.home=%FROGX_HOME%" -jar %FROGX_JARS% %FROGX_COMPONENT% %FROGX_CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT
if "%OS%"=="WINNT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set FROGX_JAVA_EXE=
set FROGX_CMD_LINE_ARGS=
goto postExec

:endNT
@endlocal & set ERROR_CODE=%ERROR_CODE%

:postExec
@REM pause the batch file if FROGX_BATCH_PAUSE is set to 'on'
if "%FROGX_BATCH_PAUSE%" == "on" pause

if "%FROGX_TERMINATE_CMD%" == "on" exit %ERROR_CODE%

cmd /C exit /B %ERROR_CODE%

