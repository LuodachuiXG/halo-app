package cc.loac.kalo.ui.screens.plugin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.loac.kalo.R
import cc.loac.kalo.data.models.FormKit
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.PluginSetting
import cc.loac.kalo.data.models.PluginSettingFormSchema
import cc.loac.kalo.data.repositories.PluginRepo
import cc.loac.kalo.network.handle
import cc.loac.kalo.ui.components.Alert
import cc.loac.kalo.ui.components.EmptyContent
import cc.loac.kalo.ui.components.ShimmerCard
import cc.loac.kalo.ui.theme.MIDDLE
import cc.loac.kalo.ui.theme.MIDDLE_MIDDLE
import cc.loac.kalo.ui.theme.SMALL
import cc.loac.kalo.ui.theme.VERY_SMALL
import kotlinx.coroutines.launch

@Composable
fun PluginSetting(
    pluginName: String,
    vm: PluginSettingViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        // 读取插件设置信息
        vm.getPluginSetting(pluginName)
    }

    // 对话框内容，不为空就显示对话框
    var dialogText by remember {
        mutableStateOf("")
    }

    // 显示对话框
    if (dialogText.isNotEmpty()) {
        dialogText.Alert {
            dialogText = ""
        }
    }

    // 插件设置信息实体类
    var pluginSetting by remember {
        mutableStateOf(PluginSetting())
    }

    // 插件设置信息状态改变，从服务器得到数据
    LaunchedEffect(vm.pluginSetting.value) {
        vm.pluginSetting.handle(
            success = {
                // 插件设置信息获取成功
                pluginSetting = it
            },
            failure = {
                // 插件设置信息获取失败
                dialogText = "插件设置加载失败，$it"
            }
        )
    }

    Column {
        if (pluginSetting.isEmpty()) {
            // 插件设置信息实体类为空，正在加载中，显示骨架屏
            ShimmerCard()
        } else if (pluginSetting.spec!!.forms.isEmpty()) {
            // 插件设置信息实体类不为空，但是设置列表为空
            // 表明当前插件没有设置选项
            EmptyContent(
                text = "当前插件没有设置选项",
                modifier = Modifier.height(150.dp)
            )
        } else {
            // 显示插件设置选项
            PluginSettingItem(vm, pluginSetting)
        }
    }
}


@Composable
fun PluginSettingItem(
    vm: PluginSettingViewModel,
    pluginSetting: PluginSetting
) {
    // 所有设置页
    val settings = pluginSetting.spec?.forms
    // 当前设置页
    var current by remember {
        mutableIntStateOf(0)
    }
    TabRow(selectedTabIndex = current) {
        settings!!.forEachIndexed { index, pluginSetting ->
            Tab(
                selected = current == index,
                onClick = { current = index },
                text = {
                    Text(
                        text = pluginSetting.label,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
    // 展示插件设置选项
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        PluginSettingForm(vm, settings!![current].formSchema)
    }
}

/**
 * 渲染插件设置选项
 */
@Composable
fun PluginSettingForm(
    vm: PluginSettingViewModel,
    formSchema: List<PluginSettingFormSchema>
) {
    LaunchedEffect(Unit) {
        // 首先设置插件的设置项数据到 ViewModel 中
        val list = mutableListOf<String>()
        formSchema.forEach {
            list.add(it.value ?: "")
        }
        vm.setPluginSettingValues(list)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = SMALL)
            .scrollable(
                state = rememberScrollState(),
                orientation = Orientation.Vertical
            )
    ) {
        formSchema.forEachIndexed { index, formSchema ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MIDDLE_MIDDLE)
            ) {
                // 显示对应组件
                when (formSchema.formkit) {
                    FormKit.TEXT, FormKit.TEXTAREA -> {
                        OutlinedTextField(
                            value = vm.getPluginSettingValue(index),
                            onValueChange = { newValue ->
                                vm.setPluginSettingValue(index, newValue)
                            },
                            label = {
                                Text(text = formSchema.label)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    FormKit.SELECT -> {
                        // 是否显示下拉菜单
                        var showDropMenu by remember {
                            mutableStateOf(false)
                        }
                        OutlinedTextField(
                            value = vm.getPluginSettingValue(index),
                            onValueChange = {},
                            label = {
                                Text(text = formSchema.label)
                            },
                            readOnly = true,
                            trailingIcon = {
                                // 下拉菜单按钮
                                IconButton(
                                    onClick = {
                                        showDropMenu = true
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_down), 
                                        contentDescription = "显示下拉菜单",
                                        modifier = Modifier.size(MIDDLE)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // 选项下拉菜单
                        DropdownMenu(
                            expanded = showDropMenu,
                            onDismissRequest = { showDropMenu = false }
                        ) {
                            formSchema.options.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = option.label)
                                    },
                                    onClick = {
                                        vm.setPluginSettingValue(index, option.value)
                                        showDropMenu = false
                                    }
                                )
                            }
                        }


                    }
                }
                // 显示帮助
                if (formSchema.help != null) {
                    Text(
                        text = formSchema.help,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = VERY_SMALL)

                    )
                }
            }
        }
    }
}


/**
 * 插件设置页面 ViewModel
 */
class PluginSettingViewModel : ViewModel() {
    // 初始化插件数据操作类
    private val pluginRepo = PluginRepo()

    // 插件设置实体类
    private val _pluginSetting = mutableStateOf(MyResponse<PluginSetting>())
    val pluginSetting: State<MyResponse<PluginSetting>> = _pluginSetting

    // 插件设置选项的数据
    private val _pluginSettingValues = mutableStateListOf<String>()

    /**
     * 设置全部插件设置项数据
     * @param values 数据集合
     */
    fun setPluginSettingValues(values: List<String>) {
        _pluginSettingValues.clear()
        _pluginSettingValues.addAll(values)
    }

    /**
     * 设置某个插件设置项数据
     * @param index 插件索引
     * @param value 要设置的数据
     */
    fun setPluginSettingValue(index: Int, value: String) {
        _pluginSettingValues[index] = value
    }

    /**
     * 获取某个插件设置项数据
     * @param index 插件索引
     */
    fun getPluginSettingValue(index: Int): String {
        return if (index < 0 || index >= _pluginSettingValues.size) {
            ""
        } else {
            _pluginSettingValues[index]
        }
    }

    /**
     * 获取插件设置信息
     * @param pluginName 插件名
     */
    fun getPluginSetting(pluginName: String) {
        viewModelScope.launch {
            _pluginSetting.value = pluginRepo.getPluginSetting(pluginName)
        }
    }
}