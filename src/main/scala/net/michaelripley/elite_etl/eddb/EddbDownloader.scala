package net.michaelripley.elite_etl.eddb

import java.io._
import java.net.{HttpURLConnection, URL}
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.util.Properties
import java.util.zip.GZIPInputStream

import scala.io.Source

object EddbDownloader {
  val systems = Resource("systems_populated.json", "https://eddb.io/archive/v6/systems_populated.json")
  val stations = Resource("stations.json", "https://eddb.io/archive/v6/stations.json")
  val factions = Resource("factions.json", "https://eddb.io/archive/v6/factions.json")

  private val etagFilename = "etags.properties"

  private[eddb] lazy val etags = {
    val properties = new Properties()

    // try to load the properties
    try {
      properties.load(new FileInputStream(etagFilename))
    } catch {
      case _: FileNotFoundException => // do nothing
    }

    properties
  }

  private[eddb] def writeProps(): Unit = {
    etags.store(new FileOutputStream(etagFilename), "etags of eddb api files")
  }
}


case class Resource(filename: String, uri: String) {

  def get(skipDownload: Boolean = false): ResourceData = {
    if (skipDownload) {
      println(s"skipped download for file $filename due to skipDownload flag")
      ResourceData(new FileInputStream(filename), cached = true)
    } else {

      import EddbDownloader.etags
      val etag = Option(etags.getProperty(filename))

      val request = new URL(uri).openConnection().asInstanceOf[HttpURLConnection]
      request.setRequestProperty("Accept", "application/json")
      request.setRequestProperty("Accept-Encoding", "gzip")
      etag.foreach(request.setRequestProperty("If-None-Match", _))
      request.connect()

      val code = request.getResponseCode
      code match {
        case 304 => // handle not modified
          println(s"skipped download for cached file $filename")
          ResourceData(new FileInputStream(filename), cached = true)
        case 200 => // handle new data
          val etag = Option(request.getHeaderField("ETag"))
            .map(_.replace("-gzip", ""))

          etag match {
            case Some(value) => etags.setProperty(filename, value)
            case None => etags.remove(filename)
          }
          EddbDownloader.writeProps() // update stored etags

          val encoding = request.getHeaderField("Content-Encoding")
          val stream = encoding match {
            case "gzip" => new GZIPInputStream(request.getInputStream)
            case _ => request.getInputStream
          }

          val file = Paths.get(filename)
          val bytes = Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING)
          println(s"downloaded ${bytes / 1024}kiB to $filename")
          ResourceData(Files.newInputStream(file), cached = false)
        case _ => // handle unknown response codes
          val stream = {
            val inputStream = request.getInputStream
            if (inputStream != null) {
              Some(inputStream)
            } else {
              Option(request.getErrorStream)
            }
          }

          val string = stream.fold("")(s => Source.fromInputStream(s).mkString)

          throw new IOException(s"$code: ${request.getResponseMessage} $string")
      }
    }
  }
}

case class ResourceData(inputStream: InputStream, cached: Boolean)
