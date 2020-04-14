package io.bimo2.stackshare

object Errors {

  private val messages = Map(
    400 -> "something was not understood",
    401 -> "something was not authorized",
    403 -> "something was not allowed",
    404 -> "something was not found",
    409 -> "something was not ok",
    415 -> "something was not supported",
    500 -> "we broke something"
  )

  def defaultMessage(status: Int): String = {
    messages.getOrElse(status, "!@#$%")
  }
}
