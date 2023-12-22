package com.myo.whatsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myo.whatsapp.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilBinding.inflate( layoutInflater )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        inicializarToolbar()

    }

    private fun inicializarToolbar() {

        val toolbar = binding.includeMTBPerfil.mtbPrincipal

        setSupportActionBar( toolbar )

        supportActionBar?.apply {
            title = "Editar Perfil"
            setDisplayHomeAsUpEnabled( true )
        }
    }
}