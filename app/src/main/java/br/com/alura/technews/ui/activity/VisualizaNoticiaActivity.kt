package br.com.alura.technews.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.alura.technews.R
import br.com.alura.technews.database.AppDatabase
import br.com.alura.technews.databinding.ActivityVisualizaNoticiaBinding
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.ResultTechNews
import br.com.alura.technews.ui.activity.extensions.mostraErro
import br.com.alura.technews.ui.viewmodel.VisualizaNoticiaViewModel
import br.com.alura.technews.ui.viewmodel.factory.VisualizaNoticiaViewModelFactory

private const val NOTICIA_NAO_ENCONTRADA = "Notícia não encontrada"
private const val TITULO_APPBAR = "Notícia"
private const val MENSAGEM_FALHA_REMOCAO = "Não foi possível remover notícia"

class VisualizaNoticiaActivity : AppCompatActivity() {

  private val binding by lazy {
    ActivityVisualizaNoticiaBinding.inflate(layoutInflater)
  }
  private val noticiaId: Long by lazy {
    intent.getLongExtra(NOTICIA_ID_CHAVE, 0)
  }
  private val viewModel by viewModels<VisualizaNoticiaViewModel> {
    val repository = NoticiaRepository(AppDatabase.getInstance(this).noticiaDAO)
    VisualizaNoticiaViewModelFactory(noticiaId, repository)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    title = TITULO_APPBAR
    verificaIdDaNoticia()
    buscaNoticiaSelecionada()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.visualiza_noticia_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.visualiza_noticia_menu_edita -> abreFormularioEdicao()
      R.id.visualiza_noticia_menu_remove -> remove()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun buscaNoticiaSelecionada() {
    viewModel.noticiaEncontrada.observe(this) { noticiaEncontrada ->
      noticiaEncontrada?.let {
        preencheCampos(it)
      }
    }
  }

  private fun verificaIdDaNoticia() {
    if (noticiaId == 0L) {
      mostraErro(NOTICIA_NAO_ENCONTRADA)
      finish()
    }
  }

  private fun preencheCampos(noticia: Noticia) {
    binding.activityVisualizaNoticiaTitulo.text = noticia.titulo
    binding.activityVisualizaNoticiaTexto.text = noticia.texto
  }

  private fun remove() {
    viewModel.remove().observe(this) {
      when (it) {
        is ResultTechNews.Success -> finish()
        is ResultTechNews.Error -> mostraErro(it.exception.message!!)
      }
    }
  }

  private fun abreFormularioEdicao() {
    val intent = Intent(this, FormularioNoticiaActivity::class.java)
    intent.putExtra(NOTICIA_ID_CHAVE, noticiaId)
    startActivity(intent)
  }

}
