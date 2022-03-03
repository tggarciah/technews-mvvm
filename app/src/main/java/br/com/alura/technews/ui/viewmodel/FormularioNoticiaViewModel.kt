package br.com.alura.technews.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.ResultTechNews
import kotlinx.coroutines.launch

class FormularioNoticiaViewModel(
  private val repository: NoticiaRepository
) : ViewModel() {

  fun salvar(noticia: Noticia): LiveData<ResultTechNews<Void?>> {
    return if (noticia.id > 0) {
      repository.edita(noticia)
    } else {
      repository.salva(noticia)
    }
  }

  fun buscaPorId(id: Long) = repository.buscaPorId(id)
}