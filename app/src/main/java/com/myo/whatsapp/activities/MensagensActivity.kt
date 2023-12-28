package com.myo.whatsapp.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myo.whatsapp.R
import com.myo.whatsapp.databinding.ActivityCadastroBinding
import com.myo.whatsapp.databinding.ActivityMensagensBinding
import com.myo.whatsapp.model.Usuario
import com.myo.whatsapp.utils.Constantes

class MensagensActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate( layoutInflater )
    }

    private var dadosDestinatario : Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        recuperarDadosUsuarioDestino()

    }

    private fun  recuperarDadosUsuarioDestino() {

        val extras = intent.extras

        if ( extras != null ) {

            val origem = extras.getString( "origem" )

            if ( origem == Constantes.ORIGEM_CONTATO ) {

                dadosDestinatario = extras.getParcelable(
                        "dadosDestinatario",
                        Usuario::class.java
                    )

            }else if ( origem == Constantes.ORIGEM_CONVERSA ) {
                        // REcuperar os dados da conversa
            }
        }
    }
}