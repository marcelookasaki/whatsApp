package com.myo.whatsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.myo.whatsapp.databinding.ActivityCadastroBinding
import com.myo.whatsapp.utils.exibirMensagem

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        inicializarToolbar()

        inicializarEventosClique()

    }

    private fun inicializarEventosClique() {
        binding.btnCadastrar.setOnClickListener {
            if ( validarCampos() ) {
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
        firebaseAuth.createUserWithEmailAndPassword(
            email, senha
        ).addOnCompleteListener {resultado ->

            if ( resultado.isSuccessful ) {

                exibirMensagem( "Sucesso ao cadastrar!" )

                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }
        }.addOnFailureListener {erro ->
            try {
                throw erro
            }catch ( erroSenhaFraca: FirebaseAuthWeakPasswordException) {
                erroSenhaFraca.printStackTrace()
                exibirMensagem(
                    "Senha fraca, utilize letras maiúsculas e " +
                            "minúsculas, números e caracteres especiais!")

            }catch ( erroUsuarioExistente: FirebaseAuthUserCollisionException) {
                erroUsuarioExistente.printStackTrace()
                exibirMensagem("E-mail já está em uso!")

            }catch ( erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                erroCredenciaisInvalidas.printStackTrace()
                exibirMensagem( "E-mail inválido, digite um outro e-mail!" )

            }
        }
    }

    private fun validarCampos(): Boolean {

        nome = binding.tietCadastroNome.text.toString()
        email = binding.tietCadastroEmail.text.toString()
        senha = binding.tietCadastroSenha.text.toString()

        if ( nome.isNotEmpty() ) {
            binding.tilCadastroNome.error = null

            if ( email.isNotEmpty() ) {
                binding.tilCadastroEmail.error = null

                if ( senha.isNotEmpty() ) {
                    binding.tilCadastroSenha.error = null
                    return true
                }else {
                    binding.tilCadastroSenha.error = "Preencha com sua senha!"
                    return false
                }
            }else {
                binding.tilCadastroEmail.error = "Preencha com seu e-mail!"
                return false
            }
        }else {
            binding.tilCadastroNome.error = "Preencha com seu nome!"
            return false
        }
    }

    private fun inicializarToolbar() {

        val toolbar = binding.includeMTBPrincipal.mtbPrincipal

        setSupportActionBar( toolbar )

        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled( true )
        }
    }
}