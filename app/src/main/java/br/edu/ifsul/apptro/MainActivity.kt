package br.edu.ifsul.apptro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import br.edu.ifsul.apptro.adapters.TabsAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    // Componentes de navegação
    private lateinit var viewPager: ViewPager2 // Gerencia o deslize entre telas
    private lateinit var tabLayout: TabLayout // A barra de abas superior/inferior
    private lateinit var adapter: TabsAdapter // O adaptador que define quais fragments aparecem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Inicializa as views
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        // Configura o adapter e vincula ao ViewPager
        adapter = TabsAdapter(this)
        viewPager.adapter = adapter

        // TabLayoutMediator conecta o TabLayout com o ViewPager2
        // Define o texto e ícone de cada aba baseado na posição
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> { tab.text = "Início"; tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_home) }
                1 -> { tab.text = "Disciplinas"; tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_subjetcs) }
                2 -> { tab.text = "Resistores"; tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_resistor) }
                3 -> { tab.text = "Devs"; tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_devs) }
            }
        }.attach()

        // Ajusta o padding do app para não ficar atrás das barras do sistema (status bar/navegação)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}