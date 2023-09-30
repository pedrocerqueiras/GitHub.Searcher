package com.example.githubsearcher.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        getAllReposByUserName()
        setupListeners()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        // 1- Recuperar os Id's da tela para a Activity com o findViewById

        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)

        //Inicializa a SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        // 2- colocar a acao de click do botao confirmar

        btnConfirmar.setOnClickListener {
            saveUserLocal()
        }
    }

    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {
        // 3 - Persistir o usuario preenchido na editText com a SharedPref no listener do botao salvar

        val userName = nomeUsuario.text.toString()
        val editor = sharedPreferences.edit()
        editor.putString("user_name", userName)
        editor.apply()
    }

    private fun showUserName() {
        /*
        4- depois de persistir o usuario exibir sempre as informacoes no EditText  se a sharedpref possuir algum valor,
        exibir no proprio editText o valor salvo
        */

        val savedUserName = sharedPreferences.getString("user_name", "")
        nomeUsuario.setText(savedUserName)
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        /*
           5 -  realizar a Configuracao base do retrofit
           Documentacao oficial do retrofit - https://square.github.io/retrofit/
           URL_BASE da API do  GitHub= https://api.github.com/
           lembre-se de utilizar o GsonConverterFactory mostrado no curso
        */

        val baseUrl = "https://api.github.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubService::class.java)

    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {
        // 6 - realizar a implementacao do callback do retrofit e chamar o metodo setupAdapter se retornar os dados com sucesso

        val user = nomeUsuario.text.toString()

        val call = githubApi.getAllRepositoriesByUser(user)

        call.enqueue(object : Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful) {
                    val repositories = response.body()
                    if (repositories != null) {
                        //dados retornados com sucesso, chama setupAdapter para configurar o RecyclerView
                        setupAdapter(repositories)
                    } else {
                        Toast.makeText(this@MainActivity, "O usuário não possui repositórios.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Erro ao buscar repositórios.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        val adapter = RepositoryAdapter(list,
            repositoryItemClickListener = { repository ->

                // Lidar com o clique no item do repositório aqui

                val urlRepository = repository.htmlUrl
                openBrowser(urlRepository)
            },
            btnShareItemClickListener = { repository ->
                shareRepositoryLink(repository.htmlUrl)
            }
        )

        listaRepositories.adapter = adapter
        listaRepositories.layoutManager = LinearLayoutManager(this)
    }

    // Metodo responsavel por compartilhar o link do repositorio selecionado
    // 11 - Colocar esse metodo no click do share item do adapter
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio

    // 12 - Colocar esse metodo no click item do adapter
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}
