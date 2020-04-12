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

  def toDoubleMap(): Map[String, Double] = {
    Map(mapping.keySet.toSeq.map((_, 0.0)): _*)
  }
}
