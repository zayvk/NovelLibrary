package io.github.gmathi.novellibrary.network

import CloudFlareByPasser
import android.net.Uri
import io.github.gmathi.novellibrary.dataCenter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.regex.Pattern
import javax.net.ssl.SSLPeerUnverifiedException


object NovelApi {
    
    fun getDocument(url: String): Document {
        try {

            return Jsoup
                    .connect(url)
                    .cookies(CloudFlareByPasser.getCookieMap())
                    .referrer(url)
                    .ignoreHttpErrors(true)
                    .timeout(30000)
                    .get()
        } catch (e: SSLPeerUnverifiedException) {
            val p = Pattern.compile("Hostname\\s(.*?)\\snot", Pattern.DOTALL or Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE or Pattern.MULTILINE) // Regex for the value of the key
            val m = p.matcher(e.localizedMessage)
            if (m.find()) {
                val hostName = m.group(1)
                val hostNames = dataCenter.getVerifiedHosts()
                if (!hostNames.contains(hostName)) {
                    dataCenter.saveVerifiedHost(hostName)
                    return getDocument(url)
                }
            }
            throw e
        }
    }

    fun getDocumentWithUserAgent(url: String, canLoop: Boolean = true): Document {
        try {
            val doc = Jsoup
                    .connect(url)
                    .referrer(url)
                    .cookies(CloudFlareByPasser.getCookieMap())
                    .ignoreHttpErrors(true)
                    .timeout(30000)
                    .userAgent(HostNames.USER_AGENT)
                    .get()

            if (canLoop && doc != null && doc.location().contains("rssbook") && doc.location().contains(HostNames.QIDIAN)) {
                return getDocumentWithUserAgent(doc.location().replace("rssbook", "book"), false)
            }

            return doc

        } catch (e: SSLPeerUnverifiedException) {
            val p = Pattern.compile("Hostname\\s(.*?)\\snot", Pattern.DOTALL or Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE or Pattern.MULTILINE) // Regex for the value of the key
            val m = p.matcher(e.localizedMessage)
            if (m.find()) {
                val hostName = m.group(1)
                val hostNames = dataCenter.getVerifiedHosts()
                if (!hostNames.contains(hostName)) {
                    dataCenter.saveVerifiedHost(hostName)
                    return getDocumentWithUserAgent(url)
                }
            }
            throw e
        } catch (e: IOException) {
            if (e.localizedMessage != null && e.localizedMessage.contains("was not verified")) {
                val hostName = Uri.parse(url)?.host!!
                if (!HostNames.isVerifiedHost(hostName)) {
                    dataCenter.saveVerifiedHost(hostName)
                    return getDocumentWithUserAgent(url)
                }
            }
            throw e
        }
    }

    @Suppress("unused")
    fun getDocumentWithUserAgentIgnoreContentType(url: String): Document {
        try {
            return Jsoup
                    .connect(url)
                    .referrer(url)
                    .timeout(30000)
                    .cookies(CloudFlareByPasser.getCookieMap())
                    .userAgent(HostNames.USER_AGENT)
                    .ignoreContentType(true)
                    .get()
        } catch (e: SSLPeerUnverifiedException) {
            val p = Pattern.compile("Hostname\\s(.*?)\\snot", Pattern.DOTALL or Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE or Pattern.MULTILINE) // Regex for the value of the key
            val m = p.matcher(e.localizedMessage)
            if (m.find()) {
                val hostName = m.group(1)
                val hostNames = dataCenter.getVerifiedHosts()
                if (!hostNames.contains(hostName)) {
                    dataCenter.saveVerifiedHost(hostName)
                    return getDocumentWithUserAgent(url)
                }
            }
            throw e
        }
    }

}