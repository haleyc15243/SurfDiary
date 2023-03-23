package com.halebop.surfdiary.entries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.halebop.surfdiary.theme.SurfDiaryTheme
import com.halebop.surfdiary.ui.AppCardListItem
import com.halebop.surfdiary.ui.CircularBackgroundIcon

class EntryFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SurfDiaryTheme {

                }
            }
        }
    }
}

@Composable
private fun DiaryEntryListFragmentContent() {
    Surface(modifier = Modifier.fillMaxSize()) {
        DiaryEntryList()
    }
}

@Composable
private fun DiaryEntryList() {
    val sampleData = listOf(Pair("Spot 1", "Some info"), Pair("Spot 2", "More information"), Pair("Spot 3", "A bit more information"))
    LazyColumn {
        items(sampleData) { item ->
            DiaryEntry(item.first, item.second)
        }
    }
}

@Composable
private fun DiaryEntry(
    title: String,
    subtitle: String
) {
    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        AppCardListItem {
            CircularBackgroundIcon(
                iconRes = android.R.drawable.ic_menu_compass
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(subtitle)
            }
        }
    }
}

@Preview
@Composable
private fun DiaryEntryListFragmentContentPreview() {
    DiaryEntryListFragmentContent()
}

@Preview
@Composable
private fun DiaryEntryListPreview() {
    DiaryEntryList()
}

@Preview
@Composable
private fun DiaryEntryPreview() {
    DiaryEntry("Title", "Subtitle")
}