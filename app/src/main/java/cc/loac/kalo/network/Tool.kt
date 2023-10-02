package cc.loac.kalo.network

import androidx.compose.runtime.State
import cc.loac.kalo.data.models.ErrorResponse
import cc.loac.kalo.data.models.MyResponse
import com.google.gson.Gson
import retrofit2.Response

/**
 * 网络请求处理方法
 * @param success 请求成功，回调 T
 * @param failure 请求失败，回调 ErrorResponse
 * @see ErrorResponse Halo 站点网络请求异常类
 */
fun <T> Response<T>.handle(
    success: (T) -> Unit,
    failure: (ErrorResponse) -> Unit
) {
    if (this.isSuccessful) {
        success(this.body()!!)
    } else {
        try {
            val gson = Gson()
            val errorResponse =
                gson.fromJson(this.errorBody()?.string(), ErrorResponse::class.java)
            failure(errorResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            failure(ErrorResponse(detail = "未知错误"))
        }
    }
}

fun <T> State<MyResponse<T>>.handle(
    success: (T) -> Unit,
    failure: (errMsg: String) -> Unit
) {
    if (this.value.isNotNone()) {
        if (this.value.isSuccessful()) {
            val data = this.value.data
            if (data == null) {
                failure("响应数据为空")
                return
            }
            success(data)
        } else {
            failure(this.value.errMsg)
        }
    }
}
