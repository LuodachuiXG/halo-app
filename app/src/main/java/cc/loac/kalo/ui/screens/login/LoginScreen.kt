package cc.loac.kalo.ui.screens.login

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.loac.kalo.R
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.repositories.ConfigKey
import cc.loac.kalo.data.repositories.ConfigRepo
import cc.loac.kalo.data.repositories.LoginRepo
import cc.loac.kalo.ui.components.Alert
import cc.loac.kalo.ui.components.ProgressAlert
import cc.loac.kalo.ui.theme.aliFontFamily
import cc.loac.kalo.utils.isUrl
import kotlinx.coroutines.launch

/**
 * 登录页面
 */
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel()) {
    var url by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 只执行一次，不参与 Composable 重组
    LaunchedEffect(Unit) {
        // 从数据库中获取保存的 Halo 站点数据
        ConfigRepo.apply {
            url = getByRoom(ConfigKey.HALO_URL)
            username = getByRoom(ConfigKey.USERNAME)
            password = getByRoom(ConfigKey.PASSWORD)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Title()
        Inputs(
            urlValue = url,
            onUrlChange = { url = it },
            usernameValue = username,
            onUsernameChange = { username = it },
            passwordValue = password,
            onPasswordChange = { password = it }
        )
        LoginBtn(
            url = url,
            username = username,
            password = password,
            loginViewModel = loginViewModel
        )
        BottomTips()
    }
}

/**
 * Kalo 大字标题
 */
@Composable
private fun Title() {
    // 控制动画的状态，从 false 变为 true，让动画在开屏就执行
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = state,
        enter = slideInVertically(
            initialOffsetY = { -850 }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 68.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontFamily = aliFontFamily,
                fontSize = 130.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )

        }
    }

}

/**
 * 输入框组
 * @param urlValue Halo 站点地址数据
 * @param onUrlChange Halo 站点地址改变事件
 * @param usernameValue 用户名数据
 * @param onUsernameChange 用户名改变事件
 * @param passwordValue 密码数据
 * @param onPasswordChange 密码改变事件
 */
@Composable
private fun Inputs(
    urlValue: String,
    onUrlChange: (String) -> Unit,
    usernameValue: String,
    onUsernameChange: (String) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
    ) {
        Input(
            label = stringResource(id = R.string.halo_website_address),
            placeholder = stringResource(R.string.need_https_or_http),
            value = urlValue,
            onValueChange = onUrlChange
        )

        Input(
            label = stringResource(R.string.username),
            placeholder = stringResource(R.string.halo_username),
            value = usernameValue,
            onValueChange = onUsernameChange
        )

        Input(
            label = stringResource(R.string.password),
            placeholder = stringResource(R.string.halo_password),
            value = passwordValue,
            onValueChange = onPasswordChange,
            isPassword = true
        )
    }
}

/**
 * 输入框
 * @param label 输入框标签
 * @param placeholder 输入框占位符（提示）
 * @param value 输入框值
 * @param onValueChange 输入框内容改变事件
 * @param isPassword 是否是密码框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Input(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    // 是否显示密码
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        value = value,
        onValueChange = onValueChange,
        visualTransformation = if (isPassword && !showPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.BrightnessHigh else Icons.Default.Brightness3,
                        contentDescription = stringResource(R.string.show_password),
                        tint = if (showPassword) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }

            }
        }
    )
}

/**
 * 登录按钮
 * @param url Halo 站点地址
 * @param username Halo 用户名
 * @param password Halo 密码
 * @param loginViewModel ViewModel
 */
@Composable
private fun LoginBtn(
    context: Context = LocalContext.current,
    url: String,
    username: String,
    password: String,
    loginViewModel: LoginViewModel
) {
    // 协程作用域
    val scope = rememberCoroutineScope()

    // 动画状态
    val animateState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    // 是否显示对话框
    var showAlert by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertText by remember { mutableStateOf("") }

    // 是否显示正在登录对话框
    var showLoadingAlert by remember { mutableStateOf(false) }

    // 登录状态
    val loginStatus by loginViewModel.loginStatus

    AnimatedVisibility(
        visibleState = animateState,
        enter = slideInVertically(
            initialOffsetY = { 1000 }
        )
    ) {
        Column(
            modifier = Modifier.padding(start = 30.dp, end = 30.dp)
        ) {
            Button(
                onClick = {
                    // 有空白信息
                    if (url.isEmpty() || username.isEmpty() || password.isEmpty()) {
                        alertTitle = context.getString(R.string.login_failure)
                        alertText = context.getString(R.string.have_blank_information)
                        showAlert = true
                        return@Button
                    }

                    if (!url.isUrl()) {
                        alertTitle = context.getString(R.string.login_failure)
                        alertText = context.getString(R.string.halo_site_address_is_incorrect)
                        showAlert = true
                        return@Button
                    }
                    showLoadingAlert = true
                    // 启动协程处理登录操作
                    scope.launch {
                        // 尝试登录
                        loginViewModel.login(url, username, password)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.login))
            }
        }
    }

    // 显示对话框话框
    if (showAlert) {
        // 显示对话框前先确认
        showLoadingAlert = false
        alertText.Alert(alertTitle) {
            showAlert = false
        }
    }


    // 登录状态改变
    if (loginStatus.isNotNone()) {
        if (loginStatus.isSuccessful()) {
            // 登录成功
            alertTitle = stringResource(R.string.login_successful)
            alertText = ""
        } else {
            // 登录失败，弹出对话框
            alertTitle = stringResource(R.string.login_failure)
            alertText = loginStatus.errMsg
        }
        showAlert = true
    }

    // 显示正在登录对话框
    if (showLoadingAlert) {
        stringResource(R.string.is_logging).ProgressAlert(5000) {
            showLoadingAlert = false
        }
    }
}

/**
 * 页面底部提示按钮
 */
@Composable
fun BottomTips() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var showAlert by remember { mutableStateOf(false) }
        TextButton(
            modifier = Modifier.padding(bottom = 5.dp),
            onClick = {
                showAlert = true
            }
        ) {
            Text(
                text = stringResource(R.string.login_bottom_title),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        if (showAlert) {
            stringResource(R.string.login_bottom_text).Alert("Haha") {
                showAlert = false
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun H() {
    LoginScreen()
}


/**
 * 登录界面的 ViewModel
 */
class LoginViewModel : ViewModel() {
    // 初始化数据操作类
    private lateinit var loginRepo: LoginRepo

    private val _loginStatus = mutableStateOf(MyResponse<String>())
    val loginStatus: State<MyResponse<String>> = _loginStatus

    /**
     * 登录
     * @param url Halo 站点地址
     * @param username 用户名
     * @param password 密码
     */
    fun login(
        url: String,
        username: String,
        password: String
    ) {
        // 初始化 LoginRepo
        loginRepo = LoginRepo(url)
        viewModelScope.launch {
            // 尝试登录
            _loginStatus.value = loginRepo.login(username, password)
        }
    }
}
