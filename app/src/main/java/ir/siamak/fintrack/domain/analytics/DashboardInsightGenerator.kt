package ir.siamak.fintrack.domain.analytics

class DashboardInsightGenerator {

    fun generate(

        income: Double,

        expense: Double

    ): String {

        return when {

            income == 0.0 ->
                "هنوز درآمدی ثبت نشده است."

            expense == 0.0 ->
                "در این ماه هزینه‌ای ثبت نشده است."

            expense > income ->
                "هزینه‌های این ماه بیشتر از درآمد بوده است."

            expense > income * .8 ->
                "مراقب هزینه‌های این ماه باشید."

            else ->
                "وضعیت مالی این ماه مناسب است."

        }

    }

}