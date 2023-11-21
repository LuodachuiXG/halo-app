package cc.loac.kalo.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.zj.shimmer.ShimmerConfig
import com.zj.shimmer.shimmer

/**
 * 自定义骨架屏颜色
 * @param visible 是否显示骨架屏
 * @param config 骨架屏配置[ShimmerConfig]，这里进行了自定义
 */
@Composable
fun Modifier.mShimmer(
    visible: Boolean,
    config: ShimmerConfig = ShimmerConfig(
        contentColor = MaterialTheme.colorScheme.surfaceVariant,
        higLightColor = Color.LightGray
    )
) = shimmer(visible, config)