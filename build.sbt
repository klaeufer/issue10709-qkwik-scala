name := "issue10709-scala"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"       % "3.0.5"  % Test,
  "org.mockito"   %  "mockito-inline"  % "2.23.4" % Test,
  "com.novocode"  %  "junit-interface" % "0.11"   % Test
)

enablePlugins(JavaAppPackaging)
