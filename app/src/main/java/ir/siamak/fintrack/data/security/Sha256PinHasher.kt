package ir.siamak.fintrack.data.security

import java.security.MessageDigest
import javax.inject.Inject

/**
 * SHA-256 implementation of [PinHasher].
 */
class Sha256PinHasher @Inject constructor() : PinHasher {

    override fun hash(pin: String): String {
        val bytes = pin.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    override fun verify(pin: String, hash: String): Boolean {
        return hash(pin) == hash
    }
}
