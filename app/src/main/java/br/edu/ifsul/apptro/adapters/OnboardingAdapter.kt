package br.edu.ifsul.apptro.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.edu.ifsul.apptro.OnboardingPage1Fragment
import br.edu.ifsul.apptro.OnboardingPage2Fragment
import br.edu.ifsul.apptro.OnboardingPage3Fragment

private const val NUM_PAGES = 3

// Gerencia a navegação e criação das páginas do Onboarding
class OnboardingAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    // Retorna o número total de páginas
    override fun getItemCount(): Int {
        return NUM_PAGES
    }

    // Instancia o fragmento correspondente à posição atual
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingPage1Fragment()
            1 -> OnboardingPage2Fragment()
            2 -> OnboardingPage3Fragment()
            else -> OnboardingPage1Fragment()
        }
    }
}