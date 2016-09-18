package aianonymous.commons.core

import java.net.URL


case class PageURL(host: String, port: Option[Int], path: Option[String], query: Option[String]) {

  override def toString =
    host +
    (if(!port.isEmpty) ":" + port.get else "") +
    path.getOrElse("") +
    (if(!query.isEmpty) "?" + query.get else "")

}


object PageURL {

  def apply(urlstr: String): PageURL = {
    val url = new URL(urlstr)

    val host     = url.getHost
    val port     = if(url.getPort != -1) Some(url.getPort) else None
    val path     = if(url.getPath != "") Some(url.getPath) else None
    val query    = Option(url.getQuery)

    PageURL(host, port, path, query)
  }

}