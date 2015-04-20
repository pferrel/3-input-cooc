name := "cooccurrence-driver"

organization := "com.finderbots"

version := "0.1"

scalaVersion := "2.10.4"

val sparkVersion = "1.1.1"

// library dependencies. (orginization name) % (project name) % (version)
libraryDependencies ++= Seq(
  // for Finderbots beacon parsing
//  "commons-beanutils" % "commons-beanutils" % "1.9.2",
//  "commons-lang" % "commons-lang" % "2.2",
//  "org.apache.httpcomponents" % "httpclient" % "4.2.5",
//  "org.apache.httpcomponents" % "httpcore" % "4.2.4",
//  "com.maxmind.geoip" % "geoip-api" % "1.2.10",
//  "net.sf.opencsv" % "opencsv" % "2.3",
//  "com.maxmind.geoip" % "geoip-api" % "1.2.10",
  "log4j" % "log4j" % "1.2.17",
  // Mahout's Spark code
  "commons-io" % "commons-io" % "2.4",
  "org.apache.mahout" % "mahout-math-scala_2.10" % "0.10.0",
  "org.apache.mahout" % "mahout-spark_2.10" % "0.10.0",
  "org.apache.mahout" % "mahout-math" % "0.10.0",
  "org.apache.mahout" % "mahout-hdfs" % "0.10.0",
  // Google collections, AKA Guava
  "com.google.guava" % "guava" % "16.0")

resolvers += "typesafe repo" at " http://repo.typesafe.com/typesafe/releases/"

//resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += Resolver.mavenLocal

packSettings

packMain := Map(
  "cooc" -> "CooccurrenceDriver"
)
