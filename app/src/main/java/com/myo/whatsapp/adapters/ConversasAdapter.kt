package com.myo.whatsapp.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.RecyclerView
import com.myo.whatsapp.databinding.ItemContatosBinding
import com.myo.whatsapp.databinding.ItemConversasBinding
import com.myo.whatsapp.model.Conversa
import com.squareup.picasso.Picasso

class ConversasAdapter(
    private val onClick: (Conversa) -> Unit
) : Adapter<ConversasAdapter.ConversasViewHolder>(){

    private var listaConversas = emptyList<Conversa>()

    fun adicionarLista( lista: List<Conversa> ) {

        listaConversas = lista
        notifyDataSetChanged()

    }

    inner class ConversasViewHolder(

        private val binding: ItemConversasBinding

    ): RecyclerView.ViewHolder( binding.root ) {

        fun bind(conversa: Conversa) {

            binding.tvItemConversasNome.text = conversa.nome
            binding.tvItemConversaUltMsg.text = conversa.ultimaMensagem

            Picasso.get()
                .load(conversa.foto)
                .into(binding.ivItemConversas)

            // Evento de clique
            binding.clItemConversas.setOnClickListener {
                onClick( conversa )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversasViewHolder {
        val inflater = LayoutInflater.from( parent.context )
        val itemView = ItemConversasBinding.inflate(
            inflater, parent, false
        )
        return ConversasViewHolder( itemView )
    }

    override fun getItemCount(): Int {
        return listaConversas.size
    }

    override fun onBindViewHolder(holder: ConversasViewHolder, position: Int) {
        val conversa = listaConversas[ position ]
        holder.bind( conversa )
    }
}

