package com.myo.whatsapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.myo.whatsapp.databinding.ActivityLoginBinding
import com.myo.whatsapp.utils.exibirMensagem

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var email: String
    private lateinit var senha: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        inicializarEventosClique()

        //firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()

        verificarUsuarioLogado()

    }

    private fun verificarUsuarioLogado() {

        val usuarioAtual = firebaseAuth.currentUser

        if ( usuarioAtual != null ) {

            startActivity(
                Intent( this, MainActivity::class.java )
            )
        }
    }


    private fun inicializarEventosClique() {

        binding.tvCadastreSe.setOnClickListener {

            startActivity(
                Intent( this, CadastroActivity::class.java )
            )
        }

        binding.btnLogar.setOnClickListener {

            if ( validarCampos() ) {
                logarUsuario()
            }else {

                exibirMensagem( "" )
            }
        }
    }

    private fun logarUsuario() {

        firebaseAuth
            .signInWithEmailAndPassword( email, senha )
            .addOnSuccessListener {
                exibirMensagem( "Usuário logado com sucesso!" )

                startActivity(
                    Intent( this, MainActivity::class.java )
                )
            }
            .addOnFailureListener {erro ->
                try {
                    throw erro
                }catch ( erroUsuarioInvalido: FirebaseAuthInvalidUserException ) {
                    erroUsuarioInvalido.printStackTrace()
                    exibirMensagem(
                        "E-mail não cadastrado!")
                }catch ( erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                    erroCredenciaisInvalidas.printStackTrace()
                    exibirMensagem(
                        "E-mail ou senha estão incorretos!")
                }
            }
    }

    private fun validarCampos(): Boolean {

        email = binding.tietEmail.text.toString()
        senha = binding.tietSenha.text.toString()

        if ( email.isNotEmpty() ) {
            binding.tilEmail.error = null

            if ( senha.isNotEmpty() ) {
                binding.tilSenha.error = null
                return true
            }else {
                binding.tilSenha.error = "Preencha com sua senha!"
                return false
            }

        }else {
            binding.tilEmail.error = "Preencha com seu e-mail"
            return false
        }
    }
}