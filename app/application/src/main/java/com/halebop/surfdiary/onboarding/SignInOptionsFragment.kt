package com.halebop.surfdiary.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.halebop.surfdiary.theme.SurfDiaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInOptionsFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SurfDiaryTheme {
                    SignInOptionsPage()
                }
            }
        }
    }
}

@Composable
fun SignInOptionsPage() {
    Surface {
        Column() {

        }
    }
}