package cc.loac.kalo.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotApplyResult
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import cc.loac.kalo.MainActivity
import cc.loac.kalo.data.models.Plugin
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
    modifier: Modifier = Modifier,
    pluginItem: PluginItem,
    onClick: (PluginItem) -> Unit
) {
    // 图片是否加载失败
    var imageFailed by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardDefaults.shape)
            .clickable {
                onClick(pluginItem)
            }
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
                    // 如果插件图片加载失败，就显示插件名第一个字
                    if (imageFailed) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .size(MIDDLE_IMAGE),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = pluginItem.spec.displayName.first().toString().uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .decoderFactory(SvgDecoder.Factory())
                                .data(pluginItem.status.logo)
                                .build(),
                            contentDescription = pluginItem.spec.displayName,
                            modifier = Modifier.size(MIDDLE_IMAGE),
                            onError = {
                                // 插件图片加载失败
                                imageFailed = true
                            }
                        )
                    }
                }

                Column(
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