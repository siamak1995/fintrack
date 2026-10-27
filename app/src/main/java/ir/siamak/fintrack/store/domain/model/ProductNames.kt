package ir.siamak.fintrack.store.domain.model

/** One identity for spelling variants; keep the user's display spelling. */
fun productNameKey(name: String): String = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFKC)
    .replace('ك', 'ک').replace('ي', 'ی').replace('ى', 'ی').replace("\u200c", " ")
    .trim().replace(Regex("\\s+"), " ").lowercase(java.util.Locale.ROOT)
