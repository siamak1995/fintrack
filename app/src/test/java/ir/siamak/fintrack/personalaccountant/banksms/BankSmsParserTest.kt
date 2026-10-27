package ir.siamak.fintrack.personalaccountant.banksms
import org.junit.Assert.*
import org.junit.Test
class BankSmsParserTest {
    @Test fun automaticFilterRejectsPricesAdsOtpAndPaymentRequests() {
        listOf("خرید: 100000 تومان", "بانک نمونه خرید 100 ریال", "حساب 1234 خرید 100 ریال با تخفیف ویژه",
            "بانک نمونه برای پرداخت مبلغ 100000 ریال به حساب 1234 مراجعه کنید",
            "بانک نمونه رمز پویا 123456 خرید 100000 ریال حساب 1234",
            "حساب 1234 واریز 100000 ریال جشنواره قرعه‌کشی", "حساب 1234 خرید 100000 ریال ناموفق بود",
            "رمز دوم 123456 خرید 100 ریال حساب 1234", "سلام حالت خوبه؟").forEach {
            assertFalse(it,BankSmsParser.isBankTransaction(it))
        }
    }
    @Test fun automaticFilterAcceptsBankStatementsWithoutSenderAllowlist() {
        listOf("بانک نمونه\nحساب: 12345678\nواریز: 100000 ریال\nمانده: 900000 ریال",
            "حساب 1234 برداشت 2500", "301170011584956001\n500,000+\nمانده:54,498,936",
            "بلو\nالهه عزیز، 220,000 ریال بابت خرید شارژ از حساب شما پرید.\nموجودی: 12,222,569 ریال",
            "بانک نمونه\nواریز 100000 ریال انجام شد").forEach {
            assertTrue(it,BankSmsParser.isBankTransaction(it))
        }
    }
    @Test fun distinguishesDepositFromBalanceAndAccountNumber() {
        val parsed = BankSmsParser.parse("بانک نمونه\nحساب: 1234567890\nواریز: ۱,۲۵۰,۰۰۰ ریال\nمانده: ۹,۰۰۰,۰۰۰ ریال")!!
        assertEquals("1250000",parsed.amount); assertEquals(SmsCurrency.RIAL,parsed.currency)
        assertEquals(BankSmsKind.INCOME,parsed.kind); assertEquals(listOf("1234567890"),parsed.accountHints)
    }
    @Test fun missingUnitRequiresExplicitUserSelection() {
        val parsed = BankSmsParser.parse("حساب: 1234\nبرداشت: 2,500\nمانده: 8,000")!!
        assertEquals("2500",parsed.amount); assertNull(parsed.currency); assertEquals(BankSmsKind.EXPENSE,parsed.kind)
    }
    @Test fun neverInterpretsBalanceAsOperationAmount() {
        val parsed = BankSmsParser.parse("بانک نمونه\nخرید انجام شد\nموجودی 100000 ریال")!!
        assertNull(parsed.amount)
    }
    @Test fun transfersAndMaskedCardTailAreHintsOnly() {
        val parsed = BankSmsParser.parse("بانک نمونه\nکارت: 6037****1234\nانتقال وجه مبلغ 50000 تومان\nبرداشت")!!
        assertEquals(BankSmsKind.TRANSFER,parsed.kind); assertEquals(false,parsed.incoming)
        assertEquals(listOf("1234"),parsed.accountHints); assertEquals("50000",parsed.amount)
    }
    @Test fun ignoresOtpAndUnrelatedTexts() {
        assertNull(BankSmsParser.parse("بانک نمونه رمز پویا 123456 خرید 20000 ریال"))
        assertNull(BankSmsParser.parse("سلام فردا می‌بینمت"))
        assertNull(BankSmsParser.parse("بانک نمونه موجودی حساب 500000 ریال"))
    }
    @Test fun equivalentDigitsAndWhitespaceHaveSameFingerprint() {
        assertEquals(BankSmsParser.fingerprint("واریز: ۱۰۰ ریال\nحساب 1234"),BankSmsParser.fingerprint("  واریز: 100 ریال حساب 1234 "))
    }
    @Test fun ambiguousDirectionMustBeChosenByUser() {
        assertNull(BankSmsParser.parse("بانک نمونه مبلغ 1000 ریال واریز/برداشت")!!.kind)
    }

    @Test fun signedDepositWithUnlabeledAccountAndInvisibleCharacters() {
        val parsed = BankSmsParser.parse("‌301170011584956001‌ \n500,000+\n1405/8/4-23:39\nمانده:54,498,936")!!
        assertEquals("500000",parsed.amount); assertEquals(BankSmsKind.INCOME,parsed.kind)
        assertEquals("54498936",parsed.balance); assertNull(parsed.currency)
        assertEquals(listOf("301170011584956001"),parsed.accountHints)
        assertEquals(java.time.LocalDateTime.of(2026,10,26,23,39).atZone(java.time.ZoneId.of("Asia/Tehran")).toInstant().toEpochMilli(),parsed.occurredAt)
    }
    @Test fun blueConversationalPurchaseSeparatesAmountAndBalance() {
        val parsed = BankSmsParser.parse("بلو\nشارژ شدی\n الهه عزیز، 220,000 ریال بابت خرید شارژ از حساب شما پرید.\nموجودی: 12,222,569 ریال\n۲۳:۴۴\n۱۴۰۵.۰۸.۰۴")!!
        assertEquals("220000",parsed.amount); assertEquals(SmsCurrency.RIAL,parsed.currency)
        assertEquals(BankSmsKind.EXPENSE,parsed.kind); assertEquals("12222569",parsed.balance)
        assertEquals(SmsCurrency.RIAL,parsed.balanceCurrency); assertEquals("بلو",parsed.bankName)
        assertEquals(java.time.LocalDateTime.of(2026,10,26,23,44).atZone(java.time.ZoneId.of("Asia/Tehran")).toInstant().toEpochMilli(),parsed.occurredAt)
    }
    @Test fun signedDebitAndRtlMarks() {
        val parsed = BankSmsParser.parse("\u200fحساب:۱۲۳۴\n−۵۰٬۰۰۰\nمانده:۰ ریال")!!
        assertEquals("50000",parsed.amount); assertEquals(BankSmsKind.EXPENSE,parsed.kind)
        assertEquals("0",parsed.balance)
    }
    @Test fun balanceClauseIsNeverAnOperation() {
        assertNull(BankSmsParser.parse("بانک نمونه موجودی: 50000 ریال بابت خرید" )!!.amount)
        assertNull(BankSmsParser.parse("بانک نمونه\nمانده:50000\n1405/8/4-23:39"))
    }
    @Test fun invalidSmsDateDoesNotCrashOrGuess() {
        assertNull(BankSmsParser.parse("برداشت 100 ریال حساب 1234\n1405/99/40-25:99")!!.occurredAt)
    }
    @Test fun bankAndAccountMatchingAvoidsAmbiguityAndCashNames() {
        val parsed = BankSmsParser.parse("بلو\nخرید 100 ریال")!!
        assertTrue(BankSmsAccounts.matches(parsed,"بانک بلو",null,null))
        assertFalse(BankSmsAccounts.matches(parsed,"بانک ملی",null,null))
        val numbered = parsed.copy(accountHints = listOf("1234"))
        assertFalse(BankSmsAccounts.matches(numbered,"بانک بلو",null,"987654321"))
        assertTrue(BankSmsAccounts.matches(numbered,"بانک بلو","۶۰۳۷۹۹۱۲۳۴",null))
        assertTrue(BankSmsAccounts.matches(numbered,"حساب پیامک 1234",null,null))
    }
}
