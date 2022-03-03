package br.com.alura.technews.repository

import java.lang.Exception

sealed class ResultTechNews<out R> {
  data class Success<out T>(val data: T) : ResultTechNews<T>()
  data class Error(val exception: Exception) : ResultTechNews<Nothing>()
}