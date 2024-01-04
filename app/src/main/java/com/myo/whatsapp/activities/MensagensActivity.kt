package com.myo.whatsapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.myo.whatsapp.adapters.ConversasAdapter
import com.myo.whatsapp.databinding.ActivityMensagensBinding
import com.myo.whatsapp.model.Conversa
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
    private var dadosRemetente : Usuario? = null // Usuario logado
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var conversasAdapter: ConversasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        recuperarDadosUsuarios()

        inicializarToolbar()

        inicializarEventosClique()

        inicializarRecyclerView()

        inicializarListeners()

    }

    private fun inicializarRecyclerView() {

        with( binding ) {
            conversasAdapter = ConversasAdapter()
            rvMensagens.adapter = conversasAdapter
            rvMensagens.layoutManager = LinearLayoutManager( applicationContext )
        }
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
                        conversasAdapter.adicionarLista( listaMensagens )

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

                // Salvar conversa remetente

                val conversaRemetente = Conversa(
                    idUsuarioRemetente, idUsuarioDestinatario,
                    dadosDestinatario!!.foto, dadosDestinatario!!.nome,
                    textoMensagem
                )

                salvarConversasFirestore( conversaRemetente )



                // Salvar para o destinatario
                salvarMensagemFirestore(
                    idUsuarioDestinatario, idUsuarioRemetente, mensagem
                )

                // Salvar conversa destinatario

                val conversaDestinatario = Conversa(
                    idUsuarioDestinatario, idUsuarioRemetente,
                    dadosRemetente!!.foto, dadosRemetente!!.nome,
                    textoMensagem
                )

                salvarConversasFirestore( conversaDestinatario )

                binding.tietMensagens.setText( "" )
            }
        }
    }

    private fun salvarConversasFirestore(conversa: Conversa) {

        firestore
            .collection( Constantes.CONVERSAS )
            .document( conversa.idUsuarioRemetente )
            .collection( Constantes.ULTIMAS_CONVERSAS )
            .document( conversa.idUsuarioDestinatario )
            .set( conversa )
            .addOnFailureListener {
                exibirMensagem( "Erro ao salvar conversa!" )
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


    private fun  recuperarDadosUsuarios() {

        // Recuperando dados remetente
        val idUsuarioRemetente = firebaseAuth.currentUser?.uid

        if ( idUsuarioRemetente != null ) {

            firestore
                .collection( Constantes.USUARIOS )
                .document( idUsuarioRemetente )
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val usuario = documentSnapshot.toObject( Usuario::class.java )

                    if ( usuario != null ) {

                        dadosRemetente = usuario

                    }
                }
        }

        // Recuperando dados destinatario
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