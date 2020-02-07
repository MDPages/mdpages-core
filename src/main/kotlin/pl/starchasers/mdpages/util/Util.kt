package pl.starchasers.mdpages.util

import java.security.MessageDigest
import java.util.*

class Util {

    companion object {
        fun randomString(length: Int): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
            val random = Random()
            val out = StringBuilder()
            for (i in 0 until length) {
                out.append(chars[random.nextInt(chars.size)])
            }
            return out.toString()
        }

        fun sha256(string: String): String {
            val bytes = string.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }
    }

}
