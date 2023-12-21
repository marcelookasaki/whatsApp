package com.myo.whatsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import com.google.firebase.auth.FirebaseAuth
import com.myo.whatsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        inicializarToolbar()
    }

    private fun inicializarToolbar() {

        val toolbar = binding.includeMTBMain.mtbPrincipal

        setSupportActionBar( toolbar )

        supportActionBar?.apply {
            title = "WhatsApp"
        }

        addMenuProvider(

            object : MenuProvider {

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)

                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                    when (menuItem.itemId) {

                        R.id.menuItemPerfil -> {
                            startActivity(
                                Intent(applicationContext, PerfilActivity::class.java)
                            )
                        }

                        R.id.menuItemSair -> {
                            deslogarUsuario()
                        }
                    }
                    return true

                }
            }
        )
    }

    private fun deslogarUsuario() {

        AlertDialog.Builder( this )
            .setTitle( "Deslogar" )
            .setMessage( "Deseja realmente sair?" )
            .setNegativeButton( "Não" ) { dialog, posicao -> }
            .setPositiveButton( "Sim" ) { dialog, posicao ->
                firebaseAuth.signOut()

                startActivity(
                    Intent( applicationContext, LoginActivity::class.java )
                )
            }
            .create()
            .show()
    }
}