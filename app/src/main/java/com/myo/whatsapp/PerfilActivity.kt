package com.myo.whatsapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.myo.whatsapp.databinding.ActivityPerfilBinding
import com.myo.whatsapp.utils.exibirMensagem

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false

    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        if ( uri != null ) {

            binding.ivPerfil.setImageURI( uri )
            uploadImagemStorage( uri )

        }else {

            exibirMensagem( "Nenhuma mensagem selecionada!" )

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        inicializarToolbar()
        solicitarPermissoes()
        inicializarEventosClique()
    }

    private fun uploadImagemStorage(uri: Uri) {

        val idUsuario = firebaseAuth.currentUser?.uid

        if ( idUsuario != null ) {

            // fotos -> usuarios -> idUsuario -> perfil.jpg
            storage
                .getReference( "fotos" )
                .child( "usuarios" )
                .child( idUsuario )
                .child( "perfil.jpg" )
                .putFile( uri )
                .addOnSuccessListener { task ->

                    exibirMensagem( "Sucesso ao fazer o upload da imagem!" )
                }
                .addOnFailureListener {
                    exibirMensagem( "Erro ao fazer o upload da imagem!" )
                }
        }
    }

    private fun inicializarEventosClique() {

        binding.fabPerfil.setOnClickListener {

            if ( temPermissaoGaleria ) {

                gerenciadorGaleria.launch( "image/*")

            }else {

                exibirMensagem( "Não tem permissão para acessar galeria!" )

                solicitarPermissoes()
            }

        }

    }

    private fun solicitarPermissoes() {

        // Verificar se usuário possui permissão
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        // Lista de permissões negadas
        val listaPermissoesNegadas = mutableListOf<String>()

        if ( !temPermissaoCamera ) {
            listaPermissoesNegadas.add( Manifest.permission.CAMERA )
        }

        if ( !temPermissaoGaleria ) {
            listaPermissoesNegadas.add( Manifest.permission.READ_MEDIA_IMAGES )
        }

        if ( listaPermissoesNegadas.isNotEmpty() ) {

            // Solicitar múltiplas permissões
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) {permissoes ->
                temPermissaoCamera = permissoes[ Manifest.permission.CAMERA ]
                    ?: temPermissaoCamera
                temPermissaoGaleria = permissoes[ Manifest.permission.READ_MEDIA_IMAGES ]
                    ?: temPermissaoGaleria
            }
            gerenciadorPermissoes.launch( listaPermissoesNegadas.toTypedArray() )
        }
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