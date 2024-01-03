package com.myo.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.myo.whatsapp.databinding.ItemContatosBinding
import com.myo.whatsapp.databinding.ItemMensagensDestinatarioBinding
import com.myo.whatsapp.databinding.ItemMensagensRemetenteBinding
import com.myo.whatsapp.model.Mensagem
import com.myo.whatsapp.utils.Constantes

class ConversasAdapter : Adapter<ViewHolder>() {

    private var listaMensagens = emptyList<Mensagem>()

    fun adicionarLista(lista: List<Mensagem>) {

        listaMensagens = lista

        notifyDataSetChanged()

    }

    class MensagensRemetenteViewHolder(

        private val binding: ItemMensagensRemetenteBinding

    ) : ViewHolder(binding.root) {

        fun bind( mensagem: Mensagem ) {
            binding.tvMsgRemetente.text = mensagem.mensagem
        }

        companion object {
            fun inflarLayout( parent: ViewGroup ) : MensagensRemetenteViewHolder {

                val inflater = LayoutInflater.from( parent.context )
                val itemView = ItemMensagensRemetenteBinding.inflate(
                    inflater, parent, false
                )
                return MensagensRemetenteViewHolder( itemView )
            }
        }
    }

    class MensagensDestinatarioViewHolder(

        private val binding: ItemMensagensDestinatarioBinding

    ) : ViewHolder(binding.root){

        fun bind( mensagem: Mensagem ) {
            binding.tvMsgDestinatario.text = mensagem.mensagem
        }

        companion object {
            fun inflarLayout( parent: ViewGroup ) : MensagensDestinatarioViewHolder {
                val inflater = LayoutInflater.from( parent.context )
                val itemView = ItemMensagensDestinatarioBinding.inflate(
                    inflater, parent, false
                )
                return MensagensDestinatarioViewHolder( itemView )
            }
        }
    }


    override fun getItemViewType(position: Int): Int {

        val mensagem = listaMensagens[ position ]
        val idUsuario = FirebaseAuth.getInstance().currentUser?.uid.toString()

        return if ( idUsuario == mensagem.idUsuario ) {
                Constantes.TIPO_REMETENTE
        }else {
                Constantes.TIPO_DESTINATARIO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 2 tipos: esquerdo branco e direito verde
        if ( viewType == Constantes.TIPO_REMETENTE ) {
                return MensagensRemetenteViewHolder.inflarLayout( parent )
        }

        return MensagensDestinatarioViewHolder.inflarLayout( parent )
    }

    override fun getItemCount(): Int {
        return listaMensagens.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mensagem = listaMensagens[ position ]

        // Forma mais longa de solucao
        /*val mensagensRemetenteViewHolder = holder as MensagensRemetenteViewHolder
        mensagensRemetenteViewHolder.bind( mensagem )*/

        when( holder ) {
            is MensagensRemetenteViewHolder -> holder.bind( mensagem )
            is MensagensDestinatarioViewHolder -> holder.bind( mensagem )
        }

    }

}