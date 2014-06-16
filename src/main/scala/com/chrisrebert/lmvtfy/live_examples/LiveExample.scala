package com.chrisrebert.lmvtfy.live_examples

import scala.util.Try
import spray.http.Uri
import spray.http.Uri.{Path, NamedHost}

sealed trait LiveExample {
  def url: Uri
}
class JsFiddleExample private(val url: Uri) extends LiveExample {
  override def toString = s"JsFiddleExample(${url})"
  override def hashCode = url.hashCode
  override def equals(other: Any) = other.isInstanceOf[JsFiddleExample] && other.asInstanceOf[JsFiddleExample].url == url
}
object JsFiddleExample {
  private object Revision {
    def unapply(intStr: String): Option[String] = Try{ intStr.toInt }.toOption.map{ _ => intStr }
  }
  def apply(uri: Uri): Option[JsFiddleExample] = canonicalize(uri).map{ new JsFiddleExample(_) }
  def unapply(uri: Uri): Option[JsFiddleExample] = {
    uri.authority.host match {
      case NamedHost("jsfiddle.net") => JsFiddleExample(uri)
      case _ => None
    }
  }
  private def canonicalize(uri: Uri) = {
    val newPath = uri.path.toString.split('/') match {
      case Array("", identifier)                       => Some(Path / identifier / "show" / "")
      case Array("", identifier, "show")               => Some(Path / identifier / "show" / "")
      case Array("", identifier, "embedded", "result") => Some(Path / identifier / "show" / "")
      case Array("", identifier, Revision(revision))                       => Some(Path / identifier / revision / "show" / "")
      case Array("", identifier, Revision(revision), "show")               => Some(Path / identifier / revision / "show" / "")
      case Array("", identifier, Revision(revision), "embedded", "result") => Some(Path / identifier / revision / "show" / "")

      case Array("", username, identifier)                       => Some(Path / username / identifier / "show" / "")
      case Array("", username, identifier, "show")               => Some(Path / username / identifier / "show" / "")
      case Array("", username, identifier, "embedded", "result") => Some(Path / username / identifier / "show" / "")

      case Array("", username, identifier, Revision(revision))                       => Some(Path / username / identifier / revision / "show" / "")
      case Array("", username, identifier, Revision(revision), "show")               => Some(Path / username / identifier / revision / "show" / "")
      case Array("", username, identifier, Revision(revision), "embedded", "result") => Some(Path / username / identifier / revision / "show" / "")
      case _ => None
    }
    newPath.map{ uri.withPath(_) }
  }
}

class JsBinExample private(val url: Uri) extends LiveExample {
  override def toString = s"JsBinExample(${url})"
  override def hashCode = url.hashCode
  override def equals(other: Any) = other.isInstanceOf[JsBinExample] && other.asInstanceOf[JsBinExample].url == url
}
object JsBinExample {
  def apply(uri: Uri): Option[JsBinExample] = canonicalize(uri).map{ new JsBinExample(_) }
  def unapply(uri: Uri): Option[JsBinExample] = {
    uri.authority.host match {
      case NamedHost("jsbin.com") => JsBinExample(uri)
      case _ => None
    }
  }
  private def canonicalize(uri: Uri) = {
    val newPath = uri.path.toString.split('/') match {
      case Array("", identifier)         => Some(Path / identifier)
      case Array("", identifier, "edit") => Some(Path / identifier)
      case Array("", identifier, revision)         => Some(Path / identifier / revision)
      case Array("", identifier, revision, "edit") => Some(Path / identifier / revision)
      case _ => None
    }
    newPath.map{ uri.withPath(_) }
  }
}


class BootplyExample private(val url: Uri) extends LiveExample {
  override def toString = s"BootplyExample(${url})"
  override def hashCode = url.hashCode
  override def equals(other: Any) = other.isInstanceOf[BootplyExample] && other.asInstanceOf[BootplyExample].url == url
}
object BootplyExample {
  def apply(uri: Uri): Option[BootplyExample] = canonicalize(uri).map{ new BootplyExample(_) }
  def unapply(uri: Uri): Option[BootplyExample] = BootplyExample(uri)
  private def canonicalize(uri: Uri) = {
    canonicalizedHost(uri.authority.host).flatMap{ newHost =>
      canonicalizedPath(uri.path).map { newPath =>
        uri.withHost(newHost).withPath(newPath)
      }
    }
  }
  private def canonicalizedHost(host: Uri.Host) = {
    host match {
      case NamedHost("bootply.com") | NamedHost("www.bootply.com") | NamedHost("s.bootply.com") => Some("s.bootply.com")
      case _ => None
    }
  }
  private def canonicalizedPath(path: Uri.Path) = {
    path.toString.split('/') match {
      case Array("", "render", identifier) => Some(Path / "render" / identifier)
      case Array("", identifier)           => Some(Path / "render" / identifier)
      case _ => None
    }
  }
}
