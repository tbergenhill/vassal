@ECHO OFF
REM Execute this file to launch VASSAL on Windows

REM Determine if java exists
set JAVA_LOCATION=
for /f "tokens=*" %%i in ('where java') do set JAVA_LOCATION=%%i
if NOT "%JAVA_LOCATION%" == "" (
  IF EXIST "%~dp0\lib\Vengine.jar" (
    java -client -cp lib/Vengine.jar VASSAL.chat.node.Server %*
  ) ELSE (
    ECHO MsgBox "VASSAL was unable to start because the file Vengine.jar could not be found. If you are trying to run VASSAL from within its ZIP archive, please unzip this archive first before running VASSAL.", vbCritical, "VASSAL Could Not Start" >"%~dp0\msg.vbs"
    WSCRIPT "%~dp0\msg.vbs"
  )
) ELSE (
  ECHO MsgBox "VASSAL was unable to start. Please ensure that you have Java installed.", vbCritical, "VASSAL Could Not Start" >"%~dp0\msg.vbs"
  WSCRIPT "%~dp0\msg.vbs"
)

