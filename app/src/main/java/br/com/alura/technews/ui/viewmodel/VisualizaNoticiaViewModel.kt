package br.com.alura.technews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.ResultTechNews

class VisualizaNoticiaViewModel(
  private val noticiaId: Long,
  private val repository: NoticiaRepository
) : ViewModel() {

  val noticiaEncontrada = repository.buscaPorId(noticiaId)

  fun remove(): LiveData<ResultTechNews<Void?>> {
    Log.i("VisualizaNoticiaViewModel", "remove: $noticiaId e título ${noticiaEncontrada.value?.titulo}")
    return noticiaEncontrada.value?.run {
      repository.remove(this)
    } ?: MutableLiveData<ResultTechNews<Void?>>().also {
      it.value = ResultTechNews.Error(Exception("Notícia não encontrada."))
    }
  }
}