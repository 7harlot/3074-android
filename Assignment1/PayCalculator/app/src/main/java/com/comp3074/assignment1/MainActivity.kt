package com.comp3074.assignment1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    // Input fields
    private lateinit var hoursInput: EditText
    private lateinit var rateInput: EditText
    private lateinit var taxRateInput: EditText

    // Output labels
    private lateinit var payOutput: TextView
    private lateinit var overtimeOutput: TextView
    private lateinit var totalPayOutput: TextView
    private lateinit var taxOutput: TextView

    // Formats numbers as money for the phone's locale, e.g. 1234.5 -> "$1,234.50".
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hoursInput = findViewById(R.id.hoursInput)
        rateInput = findViewById(R.id.rateInput)
        taxRateInput = findViewById(R.id.taxRateInput)

        payOutput = findViewById(R.id.payOutput)
        overtimeOutput = findViewById(R.id.overtimeOutput)
        totalPayOutput = findViewById(R.id.totalPayOutput)
        taxOutput = findViewById(R.id.taxOutput)

        findViewById<Button>(R.id.calculateButton).setOnClickListener { onCalculateClicked() }
    }

    /** Reads the three inputs, runs the math, and shows the results. */
    private fun onCalculateClicked() {
        val hours = readNumber(hoursInput) ?: return
        val rate = readNumber(rateInput) ?: return
        val taxRate = readNumber(taxRateInput) ?: return

        if (taxRate > 100) {
            taxRateInput.error = getString(R.string.error_tax_over_100)
            return
        }

        val result = PayCalculator.calculate(hours, rate, taxRate)

        payOutput.text = getString(R.string.label_pay, currencyFormat.format(result.pay))
        overtimeOutput.text = getString(R.string.label_overtime, currencyFormat.format(result.overtimePay))
        totalPayOutput.text = getString(R.string.label_total_pay, currencyFormat.format(result.totalPay))
        taxOutput.text = getString(R.string.label_tax, currencyFormat.format(result.tax))
    }

    /**
     * Turns a field's text into a number.
     * Returns null and shows an error on the field if it is empty, not a number, or negative,
     * so a bad input never crashes the app.
     */
    private fun readNumber(field: EditText): Double? {
        val text = field.text.toString().trim()
        if (text.isEmpty()) {
            field.error = getString(R.string.error_required)
            return null
        }
        val number = text.toDoubleOrNull()
        if (number == null || number < 0) {
            field.error = getString(R.string.error_invalid_number)
            return null
        }
        return number
    }

    // ---- ActionBar menu: holds the "About" item ----

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            startActivity(Intent(this, AboutActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
