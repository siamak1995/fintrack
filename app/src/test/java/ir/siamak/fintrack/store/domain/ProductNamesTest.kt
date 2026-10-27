package ir.siamak.fintrack.store.domain
import ir.siamak.fintrack.store.domain.model.productNameKey
import org.junit.Assert.*
import org.junit.Test
class ProductNamesTest {
    @Test fun spacingAndArabicLetterVariantsCannotCreateDuplicateNames() {
        assertEquals(productNameKey(" ماگ   سفالی "),productNameKey("ماگ سفالی"))
        assertEquals(productNameKey("كاسه چينی"),productNameKey("کاسه چینی"))
        assertEquals(productNameKey("ماگ\u200cسبز"),productNameKey("ماگ سبز"))
    }
    @Test fun differentProductsRetainDifferentIdentities() { assertNotEquals(productNameKey("ماگ"),productNameKey("بشقاب")) }
}
