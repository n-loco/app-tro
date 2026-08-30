package br.edu.ifsul.apptro.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.edu.ifsul.apptro.DevsTab
import br.edu.ifsul.apptro.HomeTab
import br.edu.ifsul.apptro.ResistorTab
import br.edu.ifsul.apptro.SubjectsTab

class TabsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    // Define o número total de abas
    override fun getItemCount(): Int {
        return 4 // Número de abas
    }

    // Cria o Fragment para a posição da aba dada
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeTab() // 1° aba (posição 0), mostra a página de início
            1 -> SubjectsTab() // 2° aba (posição 1), mostra a página de disciplinas
            2 -> ResistorTab() // 3° aba (posição 2), mostra a página de códigos de cores
            3 -> DevsTab() // 4° aba (posição 3), mostra a página de desenvolvedores
            else -> throw IllegalStateException("Posição de aba inválida: $position")
        }
    }
}