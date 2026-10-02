package ir.siamak.fintrack.personalaccountant.data.security

/**
 * Interface for hashing and verifying PINs.
 */
interface PinHasher {
    fun hash(pin: String): String
    fun verify(pin: String, hash: String): Boolean
}

