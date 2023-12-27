package com.myo.whatsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.myo.whatsapp.R
import com.myo.whatsapp.databinding.ActivityPerfilBinding
import com.myo.whatsapp.databinding.FragmentContatosBinding
import com.myo.whatsapp.model.Usuario


class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    /*private val storage by lazy {
        FirebaseStorage.getInstance()
    }*/

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContatosBinding.inflate( inflater, container, false )

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

                    val usuario = documentSnapshot.toObject( Usuario::class.java )

                    if ( usuario != null ) {

                        val idUsuario = firebaseAuth.currentUser?.uid

                        if ( idUsuario != null ) {
                            if ( idUsuario != usuario.id )

                            listaContatos.add( usuario )

                            Log.i( "fragment_contatos", "Lista contatos: ${usuario.nome}" )
                        }
                    }
                }
            }

        // Lista de contatos - atualizar recycler view



    }

    override fun onDestroy() {
        super.onDestroy()

        eventoSnapshot.remove()

    }

}