package br.unisanta.Dao

import android.util.Log
import br.unisanta.Dao.CEPDao
import br.unisanta.Models.CEP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class CEPDaoImplement: CEPDao {
    companion object {
        private var cep: CEP? = null
        private const val VIACEP_URL = "https://viacep.com.br/ws/"
        private val client = OkHttpClient()
    }

    override fun salvarCEP(cep: CEP) {
        Companion.cep = cep
    }

    override suspend fun obterDadosCEP(): String {
        val cep = Companion.cep?.toString() ?: throw IllegalArgumentException("CEP não pode ser nulo")
        val cleanCep = cep.replace(Regex("[^0-9]"), "")

        Log.i("DAO", "CEP: $cep")
        Log.i("DAO", "CEP Limpo: $cleanCep")

        val url = "$VIACEP_URL$cleanCep/json/"
        Log.i("DAO", "URL da requisicao: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response: Response = client.newCall(request).execute()
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        Log.i("DAO", "Resposta: $responseBody")
                        responseBody ?: "Resposta vazia"
                    } else {
                        val errorBody = it.body?.string()
                        Log.e("DAO", "Erro: ${it.message}, Codigo: ${it.code}, Corpo do Erro: $errorBody")
                        "Erro: ${it.message}"
                    }
                }
            } catch (e: IOException) {
                Log.e("DAO", "Erro de requisicao: ${e.message}")
                "Erro de requisicao: ${e.message}"
            }
        }
    }
}
