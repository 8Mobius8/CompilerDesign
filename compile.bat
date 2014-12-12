@echo off
echo Compiling the jjt
echo.
call compile-jjtree.bat

echo.
echo.
echo Compiling using javacc
echo.
call compile-javacc.bat

echo.
echo.
echo Compiling using javac
echo.
call compile-java.bat


echo.
echo.
echo Compiling jasmin files
echo.
call compile-jasmin-to-class.bat


pause