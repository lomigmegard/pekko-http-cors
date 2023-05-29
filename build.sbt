lazy val commonSettings = Seq(
  organization       := "ch.megard",
  version            := "0.0.0-SNAPSHOT",
  scalaVersion       := "2.13.10",
  crossScalaVersions := Seq(scalaVersion.value, "2.12.17", "3.3.0"),
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-unchecked",
    "-deprecation"
  ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n == 12 || n == 13 =>
      Seq(
        "-opt:l:inline",
        "-opt-inline-from:<sources>"
      )
    case Some((3, n)) =>
      Seq.empty // Inliner doesn't exist for Scala 3 yet
  }),
  javacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-source",
    "8",
    "-target",
    "8"
  ),
  // Temporary resolver to get Pekko snapshots
  resolvers += "Apache Nexus Snapshots".at("https://repository.apache.org/content/repositories/snapshots/"),
  homepage := Some(url("https://github.com/lomigmegard/pekko-http-cors")),
  licenses := Seq("Apache 2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/lomigmegard/pekko-http-cors"),
      "scm:git@github.com:lomigmegard/pekko-http-cors.git"
    )
  ),
  developers := List(
    Developer(id = "lomigmegard", name = "Lomig Mégard", email = "", url = url("https://lomig.ch"))
  )
)

lazy val publishSettings = Seq(
  publishMavenStyle      := true,
  Test / publishArtifact := false,
  pomIncludeRepository   := { _ => false },
  publishTo              := sonatypePublishToBundle.value
)

lazy val dontPublishSettings = Seq(
  publish / skip := true
)

lazy val root = (project in file("."))
  .aggregate(`pekko-http-cors`, `pekko-http-cors-example`, `pekko-http-cors-bench-jmh`)
  .settings(commonSettings)
  .settings(dontPublishSettings)

// Until stable look for latest version at https://repository.apache.org/content/groups/snapshots/org/apache/pekko/
lazy val pekkoVersion     = "0.0.0+26679-ca1ed993-SNAPSHOT"
lazy val pekkoHttpVersion = "0.0.0+4411-6fe04045-SNAPSHOT"

lazy val `pekko-http-cors` = project
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    // Java 9 Automatic-Module-Name (http://openjdk.java.net/projects/jigsaw/spec/issues/#AutomaticModuleNames)
    Compile / packageBin / packageOptions += Package.ManifestAttributes(
      "Automatic-Module-Name" -> "ch.megard.pekko.http.cors"
    ),
    libraryDependencies += "org.apache.pekko" %% "pekko-http"           % pekkoHttpVersion,
    libraryDependencies += "org.apache.pekko" %% "pekko-stream"         % pekkoVersion     % Provided,
    libraryDependencies += "org.apache.pekko" %% "pekko-http-testkit"   % pekkoHttpVersion % Test,
    libraryDependencies += "org.apache.pekko" %% "pekko-stream-testkit" % pekkoVersion     % Test,
    libraryDependencies += "org.scalatest"    %% "scalatest"            % "3.2.15"         % Test
  )

lazy val `pekko-http-cors-example` = project
  .dependsOn(`pekko-http-cors`)
  .settings(commonSettings)
  .settings(dontPublishSettings)
  .settings(
    libraryDependencies += "org.apache.pekko" %% "pekko-stream" % pekkoVersion
    // libraryDependencies += "ch.megard" %% "pekko-http-cors" % version.value
  )

lazy val `pekko-http-cors-bench-jmh` = project
  .dependsOn(`pekko-http-cors`)
  .enablePlugins(JmhPlugin)
  .settings(commonSettings)
  .settings(dontPublishSettings)
  .settings(
    libraryDependencies += "org.apache.pekko" %% "pekko-stream" % pekkoVersion
  )
