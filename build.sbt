name := "twitter-web-client"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  filters,
  "securesocial" %% "securesocial" % "2.1.2",
  "org.twitter4j" % "twitter4j-core" % "3.0.5"
)

resolvers += Resolver.url(
  "SBT Plugin Releases",
  url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/")
)(Resolver.ivyStylePatterns)

play.Project.playScalaSettings
