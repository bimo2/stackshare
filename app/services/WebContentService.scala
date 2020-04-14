package io.bimo2.stackshare

import net.ruippeixotog.scalascraper.browser.HtmlUnitBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.elementList
import net.ruippeixotog.scalascraper.model.TextNode

import net.ruippeixotog.scalascraper.dsl.DSL._

object WebContentService {

  private val browser = HtmlUnitBrowser()

  def fetchSeq(url: String): Seq[String] = {
    val html = browser.get(url)
    val elements = html.extract(elementList("body *:not(script):not(style)"))

    val text = elements.flatMap(_.childNodes.collect {
      case TextNode(content) => {
        redactSeq(content)
      }
    })

    text.flatten.filter(!_.isEmpty)
  }

  def fetchText(url: String): String = {
    val html = browser.get(url)
    val elements = html.extract(elementList("body *:not(script):not(style)"))

    val text = elements.flatMap(_.childNodes.collect {
      case TextNode(content) => {
        redactText(content)
      }
    })

    text.filter(!_.isEmpty).mkString(" ")
  }

  private def redactSeq(text: String): Seq[String] = {
    text.toLowerCase().replaceAll("(\\.|,|/|!|\\?|;)", " ").trim().split("\\s+")
  }

  private def redactText(text: String): String = {
    redactSeq(text).mkString(" ")
  }
}
