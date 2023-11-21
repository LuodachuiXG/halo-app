package cc.loac.kalo.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotApplyResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import cc.loac.kalo.MainActivity
import cc.loac.kalo.data.models.PluginItem
import cc.loac.kalo.ui.theme.LARGE_IMAGE
import cc.loac.kalo.ui.theme.MIDDLE_IMAGE
import cc.loac.kalo.ui.theme.NINETY_NINE
import cc.loac.kalo.ui.theme.SMALL
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size

/**
 * 插件卡片
 */
@Composable
fun PluginItemCard(
    pluginItem: PluginItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SMALL)
            .clip(CardDefaults.shape)
            .clickable {  }
    ) {
        Column(
            modifier = Modifier.padding(SMALL)
        ) {
            Row {
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(NINETY_NINE))
                        .wrapContentSize()
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .decoderFactory(SvgDecoder.Factory())
                            .data(pluginItem.status.logo)
                            .build(),
                        contentDescription = pluginItem.spec.displayName,
                        modifier = Modifier.size(MIDDLE_IMAGE)
                    )
                }

                Column (
                    modifier = Modifier.padding(start = SMALL)
                ) {
                    Text(
                        text = pluginItem.spec.displayName,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = pluginItem.spec.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}