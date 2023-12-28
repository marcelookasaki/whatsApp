package com.myo.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.myo.whatsapp.R
import com.myo.whatsapp.activities.MensagensActivity
import com.myo.whatsapp.adapters.ContatosAdapter
import com.myo.whatsapp.databinding.ActivityPerfilBinding
import com.myo.whatsapp.databinding.FragmentContatosBinding
import com.myo.whatsapp.model.Usuario
import com.myo.whatsapp.utils.Constantes


class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var contatosAdapter: ContatosAdapter


    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContatosBinding.inflate( inflater, container, false )

        contatosAdapter = ContatosAdapter { usuario ->

            val intent = Intent( context, MensagensActivity::class.java )

            intent.putExtra( "dadosDestinatario", usuario )
            intent.putExtra( "origem", Constantes.ORIGEM_CONTATO )
            startActivity( intent )

        }
        binding.rvContatosFrag.adapter = contatosAdapter
        binding.rvContatosFrag.layoutManager = LinearLayoutManager( context )
        binding.rvContatosFrag.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {

        eventoSnapshot = firestore
            .collection( "usuarios" )
            .addSnapshotListener { querySnapshot, error ->


                val listaContatos = mutableListOf<Usuario>()
                val documentos = querySnapshot?.documents

                documentos?.forEach{ documentSnapshot ->

                    val idUsuario = firebaseAuth.currentUser?.uid
                    val usuario = documentSnapshot.toObject( Usuario::class.java )

                    if ( usuario != null && idUsuario != null) {

                        if ( idUsuario != usuario.id ) {

                            listaContatos.add( usuario )

                            //Log.i( "fragment_contatos", "Lista contatos: ${usuario.nome}" )
                        }
                    }
                }
                // Lista de contatos - atualizar recycler view
                if ( listaContatos.isNotEmpty() ) {
                    contatosAdapter.adicionarLista( listaContatos )
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        eventoSnapshot.remove()

    }

}