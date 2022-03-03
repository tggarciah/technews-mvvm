package br.com.alura.technews.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.ResultTechNews

class ListaNoticiasViewModel(
  private val repository: NoticiaRepository
) : ViewModel() {

  fun buscaTodos(): LiveData<ResultTechNews<List<Noticia>>> {
    return repository.buscaTodos()
  }
}