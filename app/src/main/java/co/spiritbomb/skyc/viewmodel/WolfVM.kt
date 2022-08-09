package co.spiritbomb.skyc.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import co.spiritbomb.skyc.util.Consts
import co.spiritbomb.skyc.util.TagSender
import co.spiritbomb.skyc.util.UriBuilder
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData


class WolfVM(app: Application) : AndroidViewModel(app) {
    private val urlBuilder = UriBuilder()
    private val oneSignal = TagSender()

    val urlLivedata: MutableLiveData<String> = MutableLiveData()

    fun getDeeplink(activity: Activity) {
        AppLinkData.fetchDeferredAppLinkData(activity.applicationContext) {
            val deeplink = it?.targetUri.toString()
            if (deeplink == "null") {
                getApps(activity)
            } else {
                urlLivedata.postValue(urlBuilder.createUrl(deeplink, null, activity))
                oneSignal.sendTag(deeplink, null)
            }
        }
    }

    private fun getApps(activity: Activity) {
        AppsFlyerLib.getInstance().init(
            Consts.APPS_DEV_KEY,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                    oneSignal.sendTag("null", data)
                    urlLivedata.postValue(
                        urlBuilder.createUrl(
                            "null",
                            data,
                            activity
                        )
                    )
                }

                override fun onConversionDataFail(p0: String?) {
                    TODO("Not yet implemented")
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    TODO("Not yet implemented")
                }

                override fun onAttributionFailure(p0: String?) {
                    TODO("Not yet implemented")
                }

            }, activity
        )
        AppsFlyerLib.getInstance().start(activity)
    }
}
