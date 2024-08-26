package br.unisanta.Dao

import br.unisanta.Models.CEP

interface CEPDao {
    fun salvarCEP(cep: CEP)
    suspend fun obterDadosCEP(): String
}