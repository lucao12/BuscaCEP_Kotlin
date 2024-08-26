package br.unisanta.Models

data class CEPResponse(
    val cep: String,
    val logradouro: String,
    val complemento: String,
    val bairro: String,
    val localidade: String,
    val uf: String
) {
    override fun toString(): String {
        return "CEP: $cep\n" +
                "Logradouro: $logradouro\n" +
                "Complemento: $complemento\n" +
                "Bairro: $bairro\n" +
                "Localidade: $localidade\n" +
                "UF: $uf"
    }
}
