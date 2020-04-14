package io.bimo2.stackshare

import net.ruippeixotog.scalascraper.browser.HtmlUnitBrowser
import net.ruippeixotog.scalascraper.model.TextNode

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

object WebContentService {

  private val browser = HtmlUnitBrowser()

  def fetchSeq(url: String): (Option[String], Option[String], Seq[String]) = {
    val html = browser.get(url)
    val title = html.extract(texts("title")).headOption
    val description = html.tryExtract(attr("content")("meta[property=\"og:description\"]"))
    val elements = html.extract(elementList("body *:not(script):not(style)"))

    val text = elements.flatMap(_.childNodes.collect {
      case TextNode(content) => {
        redactSeq(content)
      }
    })

    val sequence = text.flatten.filter(!_.isEmpty)

    (title, description, sequence)
  }

  def fetchText(url: String): (Option[String], Option[String], String) = {
    val html = browser.get(url)
    val title = html.extract(texts("title")).headOption
    val description = html.tryExtract(attr("content")("meta[property=\"og:description\"]"))
    val elements = html.extract(elementList("body *:not(script):not(style)"))

    val text = elements.flatMap(_.childNodes.collect {
      case TextNode(content) => {
        redactText(content)
      }
    })

    val string = text.filter(!_.isEmpty).mkString(" ")

    (title, description, string)
  }

  private def redactSeq(text: String): Seq[String] = {
    text.toLowerCase().replaceAll("(\\.|,|/|!|\\?|;)", " ").trim().split("\\s+")
  }

  private def redactText(text: String): String = {
    redactSeq(text).mkString(" ")
  }
}
