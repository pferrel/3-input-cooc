name := "cooccurrence-driver"

organization := "com.finderbots"

version := "0.1"

scalaVersion := "2.10.4"

val sparkVersion = "1.1.1"

libraryDependencies ++= Seq(
  "log4j" % "log4j" % "1.2.17",
  // Mahout's Spark code
  "commons-io" % "commons-io" % "2.4",
  "org.apache.mahout" % "mahout-math-scala_2.10" % "0.10.0",
  "org.apache.mahout" % "mahout-spark_2.10" % "0.10.0",
  "org.apache.mahout" % "mahout-math" % "0.10.0",
  "org.apache.mahout" % "mahout-hdfs" % "0.10.0")

resolvers += "typesafe repo" at " http://repo.typesafe.com/typesafe/releases/"

//resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += Resolver.mavenLocal

packSettings

packMain := Map("cooc" -> "CooccurrenceDriver")
