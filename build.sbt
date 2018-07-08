name := "learning-scalaz"

version := "0.1"

scalaVersion in ThisBuild := "2.12.6"
scalacOptions in ThisBuild ++= Seq(
  "-language:_",
  "-Ypartial-unification",
  "-Xfatal-warnings"
)

libraryDependencies ++= Seq(
  "com.github.mpilquist" %% "simulacrum"            % "0.12.0",
  "org.scalaz"           %% "scalaz-core"           % "7.2.22",
  "org.scalactic"        %% "scalactic"             % "3.0.5",
  "eu.timepit"           %% "refined-scalaz"        % "0.8.7",
  "xyz.driver"           %% "spray-json-derivation" % "0.4.1",
  "org.scalatest"        %% "scalatest"             % "3.0.5"   % "test"
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

