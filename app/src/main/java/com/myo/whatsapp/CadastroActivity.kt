package com.myo.whatsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myo.whatsapp.databinding.ActivityCadastroBinding

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate( layoutInflater )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        inicializarToolbar()

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