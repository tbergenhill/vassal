@ECHO OFF
REM Execute this file to launch VASSAL on Windows

REM Determine if java exists
set JAVAW_LOCATION=
for /f "tokens=*" %%i in ('where javaw') do set JAVAW_LOCATION=%%i
if NOT "%JAVAW_LOCATION%" == "" (
  IF EXIST "%~dp0\lib\Vengine.jar" (
    START javaw -client -Xmx2G -cp lib/Vengine.jar VASSAL.launch.ModuleManager %*
  ) ELSE (
    ECHO MsgBox "VASSAL was unable to start because the file Vengine.jar could not be found. If you are trying to run VASSAL from within its ZIP archive, please unzip this archive first before running VASSAL.", vbCritical, "VASSAL Could Not Start" >"%~dp0\msg.vbs"
    WSCRIPT "%~dp0\msg.vbs"
  )
) ELSE (
  ECHO MsgBox "VASSAL was unable to start. Please ensure that you have Java installed.", vbCritical, "VASSAL Could Not Start" >"%~dp0\msg.vbs"
  WSCRIPT "%~dp0\msg.vbs"
)

