package br.unisanta.View

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.unisanta.Dao.CEPDao
import br.unisanta.Dao.CEPDaoImplement
import br.unisanta.Models.CEP
import br.unisanta.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    val dao = CEPDaoImplement()

    private lateinit var progressBar: ProgressBar
    private lateinit var overlayView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val btn = findViewById<Button>(R.id.btnBusca)
        val btnLimpar = findViewById<Button>(R.id.btnLimpa)
        val edtCEP = findViewById<EditText>(R.id.edtCEP)
        progressBar = findViewById(R.id.progressBar)
        overlayView = findViewById(R.id.overlayView)

        btnLimpar.setOnClickListener {
            edtCEP.setText("")
        }


        edtCEP.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private var oldText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentText = s.toString().filter { it.isDigit() }
                if (isUpdating) return

                isUpdating = true

                if (currentText.length > 5 && !currentText.contains("-")) {
                    val formatted = currentText.substring(0, 5) + "-" + currentText.substring(5)
                    edtCEP.setText(formatted)
                    edtCEP.setSelection(formatted.length)
                } else if (currentText.length <= 5) {
                    edtCEP.setText(currentText)
                    edtCEP.setSelection(currentText.length)
                }

                if (currentText.length > 8) {
                    edtCEP.setText(oldText)
                    edtCEP.setSelection(oldText.length)
                } else {
                    oldText = edtCEP.text.toString()
                }

                isUpdating = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btn.setOnClickListener {
            val cep = edtCEP.text.toString()

            if (cep != "" && cep.length == 9) {
                val cepValue = CEP(cep)
                dao.salvarCEP(cepValue)

                showLoading(true)
                CoroutineScope(Dispatchers.IO).launch {
                    val resultado = dao.obterDadosCEP()
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        if (resultado.startsWith("Erro")) {
                            Toast.makeText(this@MainActivity, "Erro ao buscar dados", Toast.LENGTH_SHORT).show()
                        } else {
                            val intent = Intent(this@MainActivity, MainActivity2::class.java)
                            startActivity(intent)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, insira valores válidos para o CEP.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        overlayView.visibility = if (isLoading) View.VISIBLE else View.GONE

        findViewById<View>(R.id.edtCEP).isEnabled = !isLoading
        findViewById<View>(R.id.btnBusca).isEnabled = !isLoading
        findViewById<View>(R.id.btnLimpa).isEnabled = !isLoading
    }
}
