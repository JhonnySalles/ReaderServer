package br.com.fenix.readerserver.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.Scanner

object PasswordGenerator {

    @JvmStatic
    fun main(args: Array<String>) {
        val encoder = BCryptPasswordEncoder(10)

        val rawPassword = if (args.isNotEmpty() && args[0].isNotBlank()) {
            args[0].trim()
        } else {
            val scanner = Scanner(System.`in`)
            print("Digite a nova senha a ser criptografada: ")
            if (scanner.hasNextLine()) scanner.nextLine().trim() else ""
        }

        if (rawPassword.isBlank()) {
            println("\n[ERRO] A senha não pode ser vazia.")
            return
        }

        val username = if (args.size > 1 && args[1].isNotBlank()) args[1].trim() else "admin"
        val hash = encoder.encode(rawPassword)

        println("\n===============================================================================")
        println("                    GERADOR DE SENHA BCRYPT (READERSERVER)")
        println("===============================================================================")
        println(" Usuário alvo:    $username")
        println(" Senha informada: $rawPassword")
        println(" Hash BCrypt:     $hash")
        println("===============================================================================")
        println(" Comando SQL para atualizar diretamente no banco PostgreSQL:")
        println("")
        println(" UPDATE usuarios SET password = '$hash' WHERE username = '$username';")
        println("===============================================================================\n")
    }
}

