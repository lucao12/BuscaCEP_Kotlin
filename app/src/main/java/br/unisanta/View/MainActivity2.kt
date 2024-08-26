package br.unisanta.View

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.unisanta.Dao.CEPDaoImplement
import br.unisanta.Models.CEPResponse
import br.unisanta.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.gson.Gson

class MainActivity2 : AppCompatActivity(R.layout.activity_main2) {
    val dao = CEPDaoImplement()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val txtCEP = findViewById<TextView>(R.id.txtCEP)
        val txtLogradouro = findViewById<TextView>(R.id.txtLogradouro)
        val txtComplemento = findViewById<TextView>(R.id.txtComplemento)
        val txtBairro = findViewById<TextView>(R.id.txtBairro)
        val txtLocalidade = findViewById<TextView>(R.id.txtLocalidade)
        val txtUF = findViewById<TextView>(R.id.txtUF)
        val btnVolta = findViewById<Button>(R.id.btnVolta)

        btnVolta.setOnClickListener {
            finish()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val resultado = dao.obterDadosCEP()

            withContext(Dispatchers.Main) {
                if (resultado.startsWith("Erro")) {
                    Toast.makeText(this@MainActivity2, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                } else {
                    val gson = Gson()
                    try {
                        val cepResponse = gson.fromJson(resultado, CEPResponse::class.java)
                        Log.i("toString", cepResponse.toString())

                        txtCEP.text = "${txtCEP.text} ${cepResponse.cep}"
                        txtLogradouro.text = "${txtLogradouro.text} ${cepResponse.logradouro}"
                        txtComplemento.text = "${txtComplemento.text} ${cepResponse.complemento}"
                        txtBairro.text = "${txtBairro.text} ${cepResponse.bairro}"
                        txtLocalidade.text = "${txtLocalidade.text} ${cepResponse.localidade}"
                        txtUF.text = "${txtUF.text} ${cepResponse.uf}"
                    } catch (e: Exception) {
                        Log.e("MainActivity2", "Erro ao converter JSON: ${e.message}")
                    }
                }
            }
        }
    }
}
