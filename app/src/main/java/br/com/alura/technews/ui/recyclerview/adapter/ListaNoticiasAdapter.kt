package br.com.alura.technews.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.alura.technews.databinding.ItemNoticiaBinding
import br.com.alura.technews.model.Noticia

class ListaNoticiasAdapter(
  private val context: Context,
  private val noticias: MutableList<Noticia> = mutableListOf(),
  var quandoItemClicado: (noticia: Noticia) -> Unit = {}
) : RecyclerView.Adapter<ListaNoticiasAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(context)
    val binding = ItemNoticiaBinding.inflate(inflater, parent, false)
    return ViewHolder(binding)
  }

  override fun getItemCount() = noticias.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val noticia = noticias[position]
    holder.vincula(noticia)
  }

  fun atualiza(noticias: List<Noticia>) {
    notifyItemRangeRemoved(0, this.noticias.size)
    this.noticias.clear()
    this.noticias.addAll(noticias)
    notifyItemRangeInserted(0, this.noticias.size)
  }

  inner class ViewHolder(
    private val binding: ItemNoticiaBinding
  ) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var noticia: Noticia

    init {
      itemView.setOnClickListener {
        if (::noticia.isInitialized) {
          quandoItemClicado(noticia)
        }
      }
    }

    fun vincula(noticia: Noticia) {
      this.noticia = noticia
      binding.itemNoticiaTitulo.text = noticia.titulo
      binding.itemNoticiaTexto.text = noticia.texto
    }
  }
}
