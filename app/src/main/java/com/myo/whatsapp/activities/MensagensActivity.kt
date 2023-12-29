package com.myo.whatsapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.myo.whatsapp.databinding.ActivityMensagensBinding
import com.myo.whatsapp.model.Mensagem
import com.myo.whatsapp.model.Usuario
import com.myo.whatsapp.utils.Constantes
import com.myo.whatsapp.utils.exibirMensagem
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var dadosDestinatario : Usuario? = null
    private lateinit var listenerRegistration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        recuperarDadosUsuarioDestinatario()

        inicializarToolbar()

        inicializarEventosClique()

        inicializarListeners()

    }

    override fun onDestroy() {
        super.onDestroy()

        listenerRegistration.remove()
    }

    private fun inicializarListeners() {

        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id

        if ( idUsuarioRemetente != null && idUsuarioDestinatario != null ) {

               listenerRegistration = firestore
                .collection( Constantes.MENSAGENS )
                .document( idUsuarioRemetente )
                .collection( idUsuarioDestinatario )
                .orderBy( "data", Query.Direction.ASCENDING )
                .addSnapshotListener { querySnapshot, erro ->

                    if ( erro != null ) {

                        exibirMensagem( "Erro ao recuperar mensagens!" )

                    }

                    val listaMensagens = mutableListOf<Mensagem>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach { documentSnapshot ->

                        val mensagem = documentSnapshot.toObject( Mensagem::class.java )

                        if ( mensagem != null ) {

                            listaMensagens.add( mensagem )

                            Log.i( "exibicao_mensagens", mensagem.mensagem)

                        }
                    }

                    // Lista
                    if ( listaMensagens.isNotEmpty() ) {

                        // Carregar os dados adapter

                    }

                }

        }

    }

    private fun inicializarEventosClique() {

        binding.fabEnviar.setOnClickListener {

            val mensagem = binding.tietMensagens.text.toString()

            salvarMensagem( mensagem )

        }
    }

    private fun salvarMensagem( textoMensagem: String) {

        if ( textoMensagem.isNotEmpty() ) {

            val idUsuarioRemetente = firebaseAuth.currentUser?.uid
            val idUsuarioDestinatario = dadosDestinatario?.id

            if ( idUsuarioRemetente != null && idUsuarioDestinatario != null ) {

                val mensagem = Mensagem(
                    idUsuarioRemetente, textoMensagem
                )
                // Salvar para o remetente
                salvarMensagemFirestore(
                    idUsuarioRemetente, idUsuarioDestinatario, mensagem
                )

                // Salvar para o destinatario
                salvarMensagemFirestore(
                    idUsuarioDestinatario, idUsuarioRemetente, mensagem
                )

                binding.tietMensagens.setText( "" )
            }
        }
    }

    private fun salvarMensagemFirestore(
        idUsuarioRemetente: String,
        idUsuarioDestinatario: String,
        mensagem: Mensagem
    ) {

        firestore
            .collection( Constantes.MENSAGENS )
            .document( idUsuarioRemetente )
            .collection( idUsuarioDestinatario )
            .add( mensagem )
            .addOnFailureListener {

                exibirMensagem( "Erro ao enviar mensagem!" )

            }
    }


    private fun  recuperarDadosUsuarioDestinatario() {

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

    private fun inicializarToolbar() {

        val toolbar = binding.mtbMensagens

        setSupportActionBar( toolbar )

        supportActionBar?.apply {
            title = ""

            if ( dadosDestinatario != null ) {

                binding.tvMTBMensagens.text = dadosDestinatario!!.nome

                Picasso.get()
                    .load( dadosDestinatario!!.foto )
                    .into( binding.ivMTBMensagens )

            }
            setDisplayHomeAsUpEnabled( true )
        }
    }
}