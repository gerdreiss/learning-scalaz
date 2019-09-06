name := "learning-scalaz"

version := "0"

scalaVersion in ThisBuild := "2.12.9"
scalacOptions in ThisBuild ++= Seq(
  "-language:_",
  "-Ypartial-unification"
)

libraryDependencies ++= Seq(
  "com.github.mpilquist" %% "simulacrum"         % "0.19.0",
  "org.scalaz"    %% "scalaz-core"               % "7.2.28",
  "org.scalaz"    %% "scalaz-effect"             % "7.2.28",
  "org.scalaz"    %% "scalaz-zio"                % "0.19",
  "org.scalactic" %% "scalactic"                 % "3.0.8",
  "eu.timepit"    %% "refined-scalaz"            % "0.9.9",
  "xyz.driver"    %% "spray-json-derivation"     % "0.7.0",
  "org.scalatest" %% "scalatest"                 % "3.0.8"    % Test,
  "org.scalaz"    %% "scalaz-scalacheck-binding" % "7.2.28-scalacheck-1.14"   % Test
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.10")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

