package br.com.alura.technews.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.retrofit.AppRetrofit
import br.com.alura.technews.retrofit.service.NoticiaService
import br.com.alura.technews.retrofit.webclient.NoticiaWebClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketTimeoutException

class NoticiaRepository(
  private val dao: NoticiaDAO,
  private val webclient: NoticiaWebClient = NoticiaWebClient(),
  private val service: NoticiaService = AppRetrofit().noticiaService
) {

  private val mediador = MediatorLiveData<ResultTechNews<List<Noticia>>>()

  fun buscaTodos(): LiveData<ResultTechNews<List<Noticia>>> {
    mediador.addSource(buscaInterno()) {
      mediador.value = ResultTechNews.Success(data = it)
    }

/*
    val liveDataApi = MutableLiveData<ResultTechNews<List<Noticia>>>()
    buscaNaApiInterfaceCall(quandoFalha = { mensagemErro ->
      liveDataApi.value = ResultTechNews.Error(exception = Exception(mensagemErro))
    })
    mediador.addSource(liveDataApi) {
      val listaAtual = mediador.value
      if (listaAtual is ResultTechNews.Success) {
        mediador.value = ResultTechNews.Success(listaAtual.data)
      }
      if (it is ResultTechNews.Error) {
        mediador.value = ResultTechNews.Error(Exception(it.exception.message))
      }
    }
*/

    mediador.addSource(buscaNaApiInterfaceResponse()) {
      val listaAtual = mediador.value
      if (listaAtual is ResultTechNews.Success) {
        mediador.value = ResultTechNews.Success(listaAtual.data)
      }
      if (it is ResultTechNews.Error) {
        mediador.value = ResultTechNews.Error(Exception(it.exception.message))
      }
    }

    return mediador
  }

  private fun buscaNaApiInterfaceCall(quandoFalha: (erro: String?) -> Unit) {
    webclient.buscaTodas(
      quandoSucesso = { noticiasNovas ->
        noticiasNovas?.let { salvaInterno(noticiasNovas) }
      },
      quandoFalha = quandoFalha
    )
  }

  private fun buscaNaApiInterfaceResponse(): LiveData<ResultTechNews<List<Noticia>?>> {
    val liveDataApi = MutableLiveData<ResultTechNews<List<Noticia>?>>()

    CoroutineScope(Dispatchers.IO).launch {
      try {
        val response = service.buscaTodas()
        if (response.isSuccessful) {
          val noticias = response.body()
          noticias?.let { salvaInterno(it) }
        } else {
          liveDataApi.postValue(ResultTechNews.Error(Exception("ERRO de resposta do BD.")))
        }
      } catch (e: ConnectException) {
        liveDataApi.postValue(ResultTechNews.Error(Exception("ERRO CONN: ${e.message}")))
      } catch (e: SocketTimeoutException) {
        liveDataApi.postValue(ResultTechNews.Error(Exception("ERRO SOCKET: ${e.message}")))
      } catch (e: Exception) {
        liveDataApi.postValue(ResultTechNews.Error(Exception("ERRO GERAL: ${e.message}")))
      }
    }
    return liveDataApi
  }

  fun salva(noticia: Noticia): LiveData<ResultTechNews<Void?>> {
    val liveData = MutableLiveData<ResultTechNews<Void?>>()
    salvaNaApi(
      noticia,
      quandoSucesso = {
        liveData.value = ResultTechNews.Success(null)
      },
      quandoFalha = {
        liveData.value = ResultTechNews.Error(Exception(it))
      })
    return liveData
  }

  fun remove(noticia: Noticia): LiveData<ResultTechNews<Void?>> {
    val liveData = MutableLiveData<ResultTechNews<Void?>>()
    removeNaApi(noticia, quandoSucesso = {
      liveData.value = ResultTechNews.Success(null)
    }, quandoFalha = {
      liveData.value = ResultTechNews.Error(Exception(it))
    })
    return liveData
  }

  fun edita(noticia: Noticia): LiveData<ResultTechNews<Void?>> {
    val liveData = MutableLiveData<ResultTechNews<Void?>>()
    editaNaApi(noticia, quandoSucesso = {
      liveData.value = ResultTechNews.Success(null)
    }, quandoFalha = {
      liveData.value = ResultTechNews.Error(Exception(it))
    })
    return liveData
  }

  fun buscaPorId(noticiaId: Long): LiveData<Noticia?> {
    return dao.buscaPorId(noticiaId)
  }

  private fun buscaInterno(): LiveData<List<Noticia>> {
    return dao.buscaTodos()
  }

  private fun salvaNaApi(
    noticia: Noticia,
    quandoSucesso: () -> Unit,
    quandoFalha: (erro: String?) -> Unit
  ) {
    webclient.salva(
      noticia,
      quandoSucesso = {
        it?.let { noticiaSalva ->
          salvaInterno(noticiaSalva, quandoSucesso)
        }
      }, quandoFalha = quandoFalha
    )
  }

  private fun salvaInterno(noticias: List<Noticia>) {
    CoroutineScope(Dispatchers.IO).launch {
      dao.salva(noticias)
    }
  }

  private fun salvaInterno(
    noticia: Noticia,
    quandoSucesso: () -> Unit
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      dao.salva(noticia)
      withContext(Dispatchers.Main) {
        quandoSucesso()
      }
    }
  }

  private fun removeNaApi(
    noticia: Noticia,
    quandoSucesso: () -> Unit,
    quandoFalha: (erro: String?) -> Unit
  ) {
    webclient.remove(
      noticia.id,
      quandoSucesso = {
        removeInterno(noticia, quandoSucesso)
      },
      quandoFalha = quandoFalha
    )
  }

  private fun removeInterno(
    noticia: Noticia,
    quandoSucesso: () -> Unit
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      dao.remove(noticia)
      withContext(Dispatchers.Main) {
        quandoSucesso()
      }
    }
  }

  private fun editaNaApi(
    noticia: Noticia,
    quandoSucesso: () -> Unit,
    quandoFalha: (erro: String?) -> Unit
  ) {
    webclient.edita(
      noticia.id, noticia,
      quandoSucesso = { noticiaEditada ->
        noticiaEditada?.let {
          salvaInterno(noticiaEditada, quandoSucesso)
        }
      }, quandoFalha = quandoFalha
    )
  }
}