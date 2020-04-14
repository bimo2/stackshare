package io.bimo2.stackshare

import scala.util.control.Breaks._

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

  def getDoubleMapping(): Map[String, Double] = {
    Map(mapping.keySet.toSeq.map((_, 0.toDouble)): _*)
  }

  def frequencyAnalysis(title: String, description: String, text: Seq[String]): Map[String, Double] = {
    val titleSeq = title.split("\\s+")
    val descriptionSeq = description.split("\\s+")
    val content = titleSeq ++: descriptionSeq ++: text
    var frequencies = Map(mapping.keySet.toSeq.map((_, Seq[Int]())): _*)

    var i = 0
    var count = 0

    for (term <- content) {
      breakable {
        for ((key, seq) <- keywords) {
          if (seq.contains(term)) {
            val nextSeq = frequencies(key) :+ i
            frequencies = frequencies.updated(key, nextSeq)
            count += 1

            break
          }
        }
      }

      i += 1
    }

    var attributes = Map[String, Double]()
    var total = 0.toDouble

    for ((key, seq) <- frequencies) {
      val frequencyScore = seq.length / count.toDouble
      val indexScore = seq.map(content.length - _).sum
      val score = frequencyScore * indexScore

      total += score

      attributes += (key -> score)
    }

    attributes.filter(_._2 > 0).map {
      case (key, value) => {
        (key, value / total)
      }
    }
  }
}
