package br.com.alura.technews.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import br.com.alura.technews.database.AppDatabase
import br.com.alura.technews.databinding.ActivityListaNoticiasBinding
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.ResultTechNews
import br.com.alura.technews.ui.recyclerview.adapter.ListaNoticiasAdapter
import br.com.alura.technews.ui.viewmodel.ListaNoticiasViewModel
import br.com.alura.technews.ui.viewmodel.factory.ListaNoticiasViewModelFactory

private const val TITULO_APPBAR = "Notícias"
private const val MENSAGEM_FALHA_CARREGAR_NOTICIAS = "Não foi possível carregar as novas notícias"

class ListaNoticiasActivity : AppCompatActivity() {

  private val binding by lazy {
    ActivityListaNoticiasBinding.inflate(layoutInflater)
  }
  private val adapter by lazy {
    ListaNoticiasAdapter(context = this)
  }
  private val viewModel: ListaNoticiasViewModel by viewModels {
    val repository = NoticiaRepository(AppDatabase.getInstance(this).noticiaDAO)
    ListaNoticiasViewModelFactory(repository)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    title = TITULO_APPBAR
    configuraRecyclerView()
    configuraFabAdicionaNoticia()
    buscaNoticias()
  }

  private fun configuraFabAdicionaNoticia() {
    binding.activityListaNoticiasFabSalvaNoticia.setOnClickListener {
      abreFormularioModoCriacao()
    }
  }

  private fun configuraRecyclerView() {
    val divisor = DividerItemDecoration(this, VERTICAL)
    binding.activityListaNoticiasRecyclerview.addItemDecoration(divisor)
    binding.activityListaNoticiasRecyclerview.adapter = adapter
    configuraAdapter()
  }

  private fun configuraAdapter() {
    adapter.quandoItemClicado = this::abreVisualizadorNoticia
  }

  private fun buscaNoticias() {
    viewModel.buscaTodos().observe(this) {
      Log.i("teste", "atualizando notícias")
      when (it) {
        is ResultTechNews.Success -> adapter.atualiza(it.data)
        is ResultTechNews.Error ->
          Toast.makeText(this, it.exception.message, Toast.LENGTH_LONG).show()
      }
    }
  }

  private fun abreFormularioModoCriacao() {
    val intent = Intent(this, FormularioNoticiaActivity::class.java)
    startActivity(intent)
  }

  private fun abreVisualizadorNoticia(it: Noticia) {
    val intent = Intent(this, VisualizaNoticiaActivity::class.java)
    intent.putExtra(NOTICIA_ID_CHAVE, it.id)
    startActivity(intent)
  }
}