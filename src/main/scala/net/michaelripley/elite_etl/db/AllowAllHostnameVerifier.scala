package net.michaelripley.elite_etl.db

import javax.net.ssl.{HostnameVerifier, SSLSession}

class AllowAllHostnameVerifier extends HostnameVerifier {
  override def verify(s: String, sslSession: SSLSession): Boolean = true
}
