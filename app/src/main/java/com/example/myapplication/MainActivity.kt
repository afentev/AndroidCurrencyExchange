package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


fun Double.round(n: Int = 3): String {
    var s = BigDecimal(this).setScale(n, RoundingMode.HALF_UP).toString()
    if (n > 0) {
        while (s[s.length - 1] == '0') {
            s = s.substring(0, s.length - 1)
        }
        if (s[s.length - 1] == '.') {
            s = s.substring(0, s.length - 1)
        }
    }
    return s
}


class MainActivity : AppCompatActivity() {
    var ok = true
    var updating = false
    var bzkMap: HashMap<String, String> = hashMapOf("RUB" to "₽", "USD" to "$", "EUR" to "€",
                                                    "GBP" to "£", "JPY" to "¥")

    var defrate: HashMap<Pair<String, String>, Double?> = hashMapOf(
        ("RUB" to "USD") to 74.0,
        ("RUB" to "EUR") to 80.0,
        ("RUB" to "GBP") to 90.0,
        ("JPY" to "GBP") to 131.0,
        ("JPY" to "USD") to 107.53,
        ("JPY" to "RUB") to 1.46,
        ("JPY" to "EUR") to 115.0,
        ("USD" to "EUR") to 1.1,
        ("USD" to "GBP") to 1.23,
        ("EUR" to "GBP") to 1.13
    )
    var settings: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settings = applicationContext.getSharedPreferences("ConverterData", Context.MODE_PRIVATE)
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        var rt = 74.0
        var ch1 = false
        var last: Boolean? = null
        var finit = true

        val actionBar: ActionBar? = supportActionBar

        val colorDrawable = ColorDrawable(Color.parseColor("#f5c400"))
        actionBar!!.setBackgroundDrawable(colorDrawable)

        editTextNumber.isSingleLine = true
        editTextNumber.setHorizontallyScrolling(true)
        updater(textView)  // rate update with starting application

        findViewById<EditText>(R.id.editTextNumber).addTextChangedListener(object :
            TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (ch1) {
                    ch1 = false
                } else {
                    last = false
                    ch1 = true
                    if (s.toString() != "" && s.toString() != "." && finit) {
                        editTextNumber2.setText((s.toString().toDouble() * rt).round())
                    } else {
                        editTextNumber2.setText("0")
                        last = if (!finit) false else null
                    }
                }
            }
        })

        findViewById<EditText>(R.id.editTextNumber2).addTextChangedListener(object :
            TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (ch1) {
                    ch1 = false
                } else {
                    last = true
                    ch1 = true
                    if (s.toString() != "" && s.toString() != "." && finit) {
                        editTextNumber.setText((s.toString().toDouble() / rt).round())
                    } else {
                        editTextNumber.setText("0")
                        last = if (!finit) true else null
                    }
                }
            }
        })

        findViewById<EditText>(R.id.editTextNumber5).addTextChangedListener(object :
            TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                val x = editTextNumber3.text.toString()
                if (s.toString() != "" && s.toString() != "." && x != "" && x != ".") {
                    rt = s.toString().toDouble() / x.toDouble()
                    finit = !(rt.isNaN() || rt.isInfinite() || rt == 0.0)
                    if (last == false) {
                        ch1 = true
                        if (editTextNumber.toString() != "" && editTextNumber.toString() != "." && finit) {
                            editTextNumber2.setText((editTextNumber.text.toString().toDouble() * rt).round())
                        } else {
                            editTextNumber2.setText("0")
                            last = if (!finit) false else null
                        }
                    } else if (last == true) {
                        ch1 = true
                        if (editTextNumber2.toString() != "" && editTextNumber2.toString() != "." && finit) {
                            editTextNumber.setText((editTextNumber2.text.toString().toDouble() / rt).round())
                        } else {
                            editTextNumber.setText("0")
                            last = if (!finit) true else null
                        }
                    }
                }
            }
        })

        findViewById<EditText>(R.id.editTextNumber3).addTextChangedListener(object :
            TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                val x = editTextNumber5.text.toString()
                if (s.toString() != "" && s.toString() != "." && x != "" && x != ".") {
                    rt = x.toDouble() / s.toString().toDouble()
                    finit = !(rt.isNaN() || rt.isInfinite() || rt == 0.0)
                    if (last == false) {
                        ch1 = true
                        if (editTextNumber.toString() != "" && editTextNumber.toString() != "." && finit) {
                            editTextNumber2.setText((editTextNumber.text.toString().toDouble() * rt).round())
                        } else {
                            editTextNumber2.setText("0")
                            last = if (!finit) false else null
                        }
                    } else if (last == true) {
                        ch1 = true
                        if (editTextNumber2.toString() != "" && editTextNumber2.toString() != "." && finit) {
                            editTextNumber.setText((editTextNumber2.text.toString().toDouble() / rt).round())
                        } else {
                            editTextNumber.setText("0")
                            last = if (!finit) true else null
                        }
                    }
                }
            }
        })

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies_ar,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val v = spinner.selectedView
                (v as TextView).setTextColor(Color.WHITE)
                updater(textView)
                val selectedItem = parent.getItemAtPosition(position).toString()
                finit = true

                textView5.text = selectedItem
                if (defrate.containsKey(selectedItem to textView6.text)) {
                    editTextNumber3.setText(defrate[selectedItem to textView6.text]?.round())
                    editTextNumber5.setText("1")
                } else if (defrate.containsKey(textView6.text to selectedItem)) {
                    editTextNumber5.setText(defrate[textView6.text to selectedItem]?.round())
                    editTextNumber3.setText("1")
                } else {
                    editTextNumber5.setText("1")
                    editTextNumber3.setText("1")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val spL: Spinner = findViewById(R.id.spinner)
        spL.setSelection(0)

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies_ar,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner2.adapter = adapter
        }
        spinner2.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val v = spinner2.selectedView
                (v as TextView).setTextColor(Color.WHITE)
                updater(textView)
                val selectedItem = parent.getItemAtPosition(position).toString()
                finit = true

                textView6.text = selectedItem
                if (defrate.containsKey(selectedItem to textView5.text)) {
                    editTextNumber5.setText(defrate[selectedItem to textView5.text]?.round())
                    editTextNumber3.setText("1")
                } else if (defrate.containsKey(textView5.text to selectedItem)) {
                    editTextNumber3.setText(defrate[textView5.text to selectedItem]?.round())
                    editTextNumber5.setText("1")
                } else {
                    editTextNumber5.setText("1")
                    editTextNumber3.setText("1")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val spL2: Spinner = findViewById(R.id.spinner2)
        spL2.setSelection(1)
    }

    fun updater(view: View) {
        val c = 86400  // seconds in day

        val saved = settings?.getInt("last", -1)?.toLong()
        val time: Long = Calendar.getInstance().timeInMillis / 1000
        val r = (time - 55200) / c

        /* 55200 because EuSB updates coming at 15:00 GMT. */

//        val editor = settings?.edit()
//        editor?.putInt("last", 0)
//        editor?.apply()
        if (r - saved!! >= 1 && time % c >= 55200) {
            decor(false)

            val editor = settings?.edit()
            if (editor != null) {
                editor.putInt("last", r.toInt())
                editor.apply()
            }
        } else {
            defrate["RUB" to "GBP"] = settings?.getString("GBP-RUB", "1")?.toDouble()
            defrate["JPY" to "GBP"] = settings?.getString("GBP-JPY", "1")?.toDouble()
            defrate["USD" to "GBP"] = settings?.getString("GBP-USD", "1")?.toDouble()
            defrate["EUR" to "GBP"] = settings?.getString("GBP-EUR", "1")?.toDouble()
            defrate["RUB" to "EUR"] = settings?.getString("EUR-RUB", "1")?.toDouble()
            defrate["JPY" to "EUR"] = settings?.getString("EUR-JPY", "1")?.toDouble()
            defrate["USD" to "EUR"] = settings?.getString("EUR-USD", "1")?.toDouble()
            defrate["RUB" to "USD"] = settings?.getString("USD-RUB", "1")?.toDouble()
            defrate["JPY" to "USD"] = settings?.getString("USD-JPY", "1")?.toDouble()
            defrate["RUB" to "JPY"] = settings?.getString("JPY-RUB", "1")?.toDouble()
        }

        if (defrate.containsKey(textView5.text to textView6.text)) {
            editTextNumber3.setText(defrate[textView5.text to textView6.text]?.round())
            editTextNumber5.setText("1")
        } else if (defrate.containsKey(textView6.text to textView5.text)) {
            editTextNumber5.setText(defrate[textView6.text to textView5.text]?.round())
            editTextNumber3.setText("1")
        } else {
            editTextNumber5.setText("1")
            editTextNumber3.setText("1")
        }
    }

    private fun decor(first: Boolean) {
        try {
            updateRates("GBP")
            updateRates("EUR")
            updateRates("USD")
            updateRates("JPY")

            if (defrate.containsKey(textView5.text to textView6.text)) {
                editTextNumber3.setText(defrate[textView5.text to textView6.text]?.round())
                editTextNumber5.setText("1")
            } else if (defrate.containsKey(textView6.text to textView5.text)) {
                editTextNumber5.setText(defrate[textView6.text to textView5.text]?.round())
                editTextNumber3.setText("1")
            } else {
                editTextNumber5.setText("1")
                editTextNumber3.setText("1")
            }
        } catch (e: java.lang.Exception) /* I'm doing my best here :( */ {
            if (!first) {
                val myToast = Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT)
                myToast.show()
            }
        }
    }

    private fun updateRates(base: String) {
        val x = upd(base)
        val gson = Gson()

        val mp: Map<String, Any> = gson.fromJson(x,
            object : TypeToken<Map<String, Any>>() {}.type)

        val rates: Map<String, Double> = gson.fromJson(mp["rates"].toString(),
            object: TypeToken<Map<String, Double>>() {}.type)

        val editor = settings?.edit()

        if (base == "GBP") {
            defrate["RUB" to base] = rates["RUB"]
            editor?.putString("GBP-RUB", rates["RUB"].toString())

            defrate["JPY" to base] = rates["JPY"]
            editor?.putString("GBP-JPY", rates["JPY"].toString())

            defrate["USD" to base] = rates["USD"]
            editor?.putString("GBP-USD", rates["USD"].toString())

            defrate["EUR" to base] = rates["EUR"]
            editor?.putString("GBP-EUR", rates["EUR"].toString())
        } else if (base == "EUR") {
            defrate["RUB" to base] = rates["RUB"]
            editor?.putString("EUR-RUB", rates["RUB"].toString())

            defrate["JPY" to base] = rates["JPY"]
            editor?.putString("EUR-JPY", rates["JPY"].toString())

            defrate["USD" to base] = rates["USD"]
            editor?.putString("EUR-USD", rates["USD"].toString())
        } else if (base == "USD") {
            defrate["RUB" to base] = rates["RUB"]
            editor?.putString("USD-RUB", rates["RUB"].toString())

            defrate["JPY" to base] = rates["JPY"]
            editor?.putString("USD-JPY", rates["JPY"].toString())
        } else if (base == "JPY") {
            defrate[base to "RUB"] = 1.0 / (rates["RUB"] ?: 1.0)
            editor?.putString("JPY-RUB", (1.0 / (rates["RUB"] ?: 1.0)).toString())
        }
        editor?.apply()
    }

    private fun upd(base: String): String {
        val url = URL("https://api.exchangeratesapi.io/latest?base=$base")
        val con: HttpURLConnection = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.connectTimeout = 1000
        con.readTimeout = 1000
        //val status = con.responseCode
        val in_ = BufferedReader(
            InputStreamReader(con.inputStream)
        )
        var inputLine: String?
        val content = StringBuffer()
        while (in_.readLine().also { inputLine = it } != null) {
            content.append(inputLine)
        }
        in_.close()
        con.disconnect()
        return content.toString()
    }

    /*
    fun toastMe(view: View) {
        val myToast = Toast.makeText(this, "Hello Toast!", Toast.LENGTH_SHORT)
        myToast.show()
    }
     */
}
