name := "learning-scalaz"

version := "0.1"

scalaVersion in ThisBuild := "2.12.8"
scalacOptions in ThisBuild ++= Seq(
  "-language:_",
  "-Ypartial-unification"
)

libraryDependencies ++= Seq(
  "com.github.mpilquist" %% "simulacrum"         % "0.15.0",
  "org.scalaz"    %% "scalaz-core"               % "7.2.27",
  "org.scalaz"    %% "scalaz-effect"             % "7.2.27",
  "org.scalaz"    %% "scalaz-typelevel"          % "7.1.17",
  "org.scalaz"    %% "scalaz-zio"                % "0.6.1",
  "org.scalactic" %% "scalactic"                 % "3.0.5",
  "eu.timepit"    %% "refined-scalaz"            % "0.9.4",
  "xyz.driver"    %% "spray-json-derivation"     % "0.7.0",
  "org.scalatest" %% "scalatest"                 % "3.0.5"    % Test,
  "org.scalaz"    %% "scalaz-scalacheck-binding" % "7.2.27-scalacheck-1.14"   % Test
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

