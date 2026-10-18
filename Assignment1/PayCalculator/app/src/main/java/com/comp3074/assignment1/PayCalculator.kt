package com.comp3074.assignment1

/**
 * The four numbers we show the user after a calculation.
 */
data class PayResult(
    val pay: Double,
    val overtimePay: Double,
    val totalPay: Double,
    val tax: Double,
)

/**
 * All the pay math lives here, with no Android code, so it is easy to read
 * and easy to unit test (see PayCalculatorTest).
 *
 * Formulas come straight from the assignment spec:
 *   hours <= 40 : pay = hours * rate,         overtime = 0
 *   hours  > 40 : pay = 40 * rate,            overtime = (hours - 40) * rate * 1.5
 *   totalPay = pay + overtime
 *   tax = pay * taxRate      <- the spec taxes base pay only, NOT totalPay
 */
object PayCalculator {

    const val REGULAR_HOURS_LIMIT = 40.0
    const val OVERTIME_MULTIPLIER = 1.5

    /**
     * @param hoursWorked     hours worked this period, 0 or more
     * @param hourlyRate      pay per hour, 0 or more
     * @param taxRatePercent  tax as a percentage, 0 to 100 (13 means 13%)
     */
    fun calculate(hoursWorked: Double, hourlyRate: Double, taxRatePercent: Double): PayResult {
        // The screen validates input first; these are a safety net if someone calls this directly.
        require(hoursWorked >= 0) { "hoursWorked must be 0 or more" }
        require(hourlyRate >= 0) { "hourlyRate must be 0 or more" }
        require(taxRatePercent in 0.0..100.0) { "taxRatePercent must be between 0 and 100" }

        val pay: Double
        val overtimePay: Double
        if (hoursWorked <= REGULAR_HOURS_LIMIT) {
            pay = hoursWorked * hourlyRate
            overtimePay = 0.0
        } else {
            pay = REGULAR_HOURS_LIMIT * hourlyRate
            overtimePay = (hoursWorked - REGULAR_HOURS_LIMIT) * hourlyRate * OVERTIME_MULTIPLIER
        }

        val totalPay = pay + overtimePay
        // The user types 13 for 13%, so divide by 100 to get 0.13.
        val tax = pay * (taxRatePercent / 100)

        return PayResult(pay, overtimePay, totalPay, tax)
    }
}
