package com.pinelabs.pluralsdk.utils

import android.content.Context
import android.provider.Settings
import com.clevertap.android.sdk.CleverTapAPI
import com.pinelabs.pluralsdk.utils.Constants.Companion.APP_VERSION

class CleverTapUtil {
    companion object {
        const val EVENT_SDK_INITIALISED = "SDK_Initialised"
        const val EVENT_PAYMENT_PAGE_LOADED = "Payment_Page_Loaded"
        const val EVENT_PAYMENT_METHOD = "payment_method"
        const val EVENT_PAYMENT_STATUS_SUCCESS = "payment_status_success"
        const val EVENT_PAYMENT_STATUS_FAILURE = "payment_status_failure"
        const val EVENT_PAYMENT_CANCELLED = "payment_cancelled"
        const val EVENT_PAYMENT_COMPLETION_TIME = "payment_completion_time"
        const val EVENT_SDK_ERROR = "sdk_error"

        const val PARAM_SDK_TYPE = "sdk_type"
        const val PARAM_PLATFORM = "platform"
        const val PARAM_SDK_VERSION = "sdk_version"
        const val PARAM_DEVICE_ID = "device_id"

        const val PARAM_LOAD_TIME_MS = "load_time_ms"
        const val PARAM_MERCHANT_ID = "merchant_id"
        const val PARAM_AMOUNT = "amount"
        const val PARAM_USER_PHONE_NO = "user_phoneno"
        const val PARAM_USER_EMAIL = "user_email"
        const val PARAM_PAYMENT_METHOD = "payment_method"
        const val PARAM_PAYMENT_STATUS = "payment_status"

        const val PARAM_PAYMENT_METHOD_CARD = "payment_method_card"
        const val PARAM_PAYMENT_METHOD_UPI = "payment_method_upi"
        const val PARAM_PAYMENT_METHOD_NB = "payment_method_nb"
        const val PARAM_PAYMENT_METHOD_EMI = "payment_method_emi"
        const val PARAM_PAYMENT_METHOD_WALLET = "payment_method_wallets"
        const val PARAM_PAY_BUTTON = "pay_button"

        const val PARAM_SUCCESS_ORDER_ID = "success_order_id"
        const val PARAM_SUCCESS_PAYMENT_ID = "success_payment_id"

        const val PARAM_FAILURE_ORDER_ID = "failure_order_id"
        const val PARAM_FAILURE_PAYMENT_ID = "failure_payment_id"
        const val PARAM_FAILURE_ERROR_CODE = "failure_error_code"
        const val PARAM_FAILURE_ERROR_MESSAGE = "failure_error_message"

        const val PARAM_CANCEL = "cancel"
        const val PARAM_BACKPRESS = "backpress"

        const val PARAM_LOAD_TIME = "load_time"

        const val PARAM_ERROR_CODE = "error_code"
        const val PARAM_ERROR_MESAGE = "error_message"

        const val SDK_TYPE_PLATRFORM_ANDROID = "android"

        const val PROFILE_EMAIL = "Email"
        const val PROFILE_PHONE = "Phone"

        fun CT_PROFILE(cleverTapAPI: CleverTapAPI?, email: String?, phone: String?) {
            val profile = mapOf(
                PROFILE_EMAIL to email,
                PROFILE_PHONE to phone
            )
            cleverTapAPI?.onUserLogin(profile)
        }

        fun CT_EVENT_SDK_INITIALISED(cleverTapAPI: CleverTapAPI?, context: Context) {
            val sdkInitialzedData = mapOf(
                PARAM_SDK_TYPE to SDK_TYPE_PLATRFORM_ANDROID,
                PARAM_PLATFORM to SDK_TYPE_PLATRFORM_ANDROID,
                PARAM_SDK_VERSION to APP_VERSION,
                PARAM_DEVICE_ID to Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID
                )
            )
            cleverTapAPI?.pushEvent(EVENT_SDK_INITIALISED, sdkInitialzedData)
        }

        fun CT_EVENT_PAYMENT_PAGE_LOADED(
            cleverTapAPI: CleverTapAPI?, loadTime: Int?, merchantId: Int?,
            amount: String?, phone: String?, email: String?
        ) {
            val paymentPageLoadedData = mapOf(
                PARAM_LOAD_TIME_MS to loadTime,
                PARAM_MERCHANT_ID to merchantId,
                PARAM_AMOUNT to amount,
                PARAM_USER_PHONE_NO to phone,
                PARAM_USER_EMAIL to email
            )
            cleverTapAPI?.pushEvent(EVENT_PAYMENT_PAGE_LOADED, paymentPageLoadedData)

        }

        fun CT_EVENT_PAYMENT_METHOD(
            cleverTapAPI: CleverTapAPI?,
            paymentMethod: String, paymentStatus: String,
            cardNumber: String?, upiMethod: String?, netBank: String?
        ) {
            val paymentMethodData = mapOf(
                PARAM_PAYMENT_METHOD to paymentMethod,
                PARAM_PAYMENT_STATUS to paymentStatus,
                PARAM_PAYMENT_METHOD_CARD to cardNumber,
                PARAM_PAYMENT_METHOD_UPI to upiMethod,
                PARAM_PAYMENT_METHOD_NB to netBank,
                PARAM_PAY_BUTTON to true
            )
            cleverTapAPI?.pushEvent(EVENT_PAYMENT_METHOD, paymentMethodData)
        }

        fun CT_EVENT_PAYMENT_STATUS_SUCCESS(
            cleverTapAPI: CleverTapAPI?,
            orderId: String?,
            paymentId: String?
        ) {
            val paymentStatusSuccessData = mapOf(
                PARAM_SUCCESS_ORDER_ID to orderId,
                PARAM_SUCCESS_PAYMENT_ID to paymentId
            )
            cleverTapAPI?.pushEvent(EVENT_PAYMENT_STATUS_SUCCESS, paymentStatusSuccessData)

        }

        fun CT_EVENT_PAYMENT_STATUS_FAILURE(
            cleverTapAPI: CleverTapAPI?, orderId: String?, paymentId: String?,
            errorCode: String?, errorMessage: String?
        ) {
            val paymentStatusFailureData = mapOf(
                PARAM_FAILURE_ORDER_ID to orderId,
                PARAM_FAILURE_PAYMENT_ID to paymentId,
                PARAM_FAILURE_ERROR_CODE to errorCode,
                PARAM_FAILURE_ERROR_MESSAGE to errorMessage
            )
            cleverTapAPI?.pushEvent(EVENT_PAYMENT_STATUS_FAILURE, paymentStatusFailureData)

        }

        fun CT_EVENT_PAYMENT_CANCELLED(
            cleverTapAPI: CleverTapAPI,
            cancel: Boolean?,
            backpress: Boolean?
        ) {
            val paymentStatusCancelledData = mapOf(
                PARAM_CANCEL to cancel,
                PARAM_BACKPRESS to backpress
            )
            cleverTapAPI?.pushEvent(EVENT_PAYMENT_CANCELLED, paymentStatusCancelledData)
        }

        fun CT_EVENT_PAYMENT_COMPLETION_TIME(cleverTapAPI: CleverTapAPI, loadTime: Long) {
            val paymentCompletionTimeData = mapOf(
                PARAM_LOAD_TIME to loadTime
            )
            cleverTapAPI?.pushEvent(EVENT_PAYMENT_COMPLETION_TIME, paymentCompletionTimeData)
        }

        fun CT_EVENT_SDK_ERROR(
            cleverTapAPI: CleverTapAPI?,
            errorCode: String?,
            errorMessage: String?
        ) {
            val sdkError = mapOf(
                PARAM_ERROR_CODE to errorCode,
                PARAM_ERROR_MESAGE to errorMessage
            )
            cleverTapAPI?.pushEvent(EVENT_SDK_ERROR, sdkError)
        }

    }

}