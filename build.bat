@ECHO off
set version=%1
set build=%2
set jarname=NoiseTweaker-%version%+build.%build%.jar
if not exist "./build" (
  mkdir build
)
cd bin
jar cvfm %jarname% ../MANIFEST.MF *
move %jarname% ../build/
cd ..