package io.bimo2.stackshare

object Language {

  val mapping = Map(
    "js" -> "JavaScript",
    "swift" -> "Swift",
    "go" -> "Go",
    "rb" -> "Ruby",
    "py" -> "Python",
    "scala" -> "Scala",
    "java" -> "Java",
    "kt" -> "Kotlin",
    "cpp" -> "C++",
    "m" -> "Objective-C",
    "cs" -> "C#",
    "php" -> "PHP",
    "sql" -> "SQL"
  )

  private val keywords = Map(
    "js" -> Seq("javascript", "typescript", "js", "node", "nodejs", "react", "angular"),
    "swift" -> Seq("swift", "ios", "xcode"),
    "go" -> Seq("go", "golang"),
    "rb" -> Seq("ruby", "rails"),
    "py" -> Seq("python", "flask", "django"),
    "scala" -> Seq("scala", "play", "akka", "spark"),
    "java" -> Seq("java"),
    "kt" -> Seq("kotlin", "android"),
    "cpp" -> Seq("c++", "c"),
    "m" -> Seq("objective-c"),
    "cs" -> Seq("c#", ".net"),
    "php" -> Seq("php", "laravel"),
    "sql" -> Seq("sql", "mysql", "postgresql")
  )

  def toDoubleMap(): Map[String, Double] = {
    Map(mapping.keySet.toSeq.map((_, 0.0)): _*)
  }
}
