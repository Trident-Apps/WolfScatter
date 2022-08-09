package co.spiritbomb.skyc.util

import android.app.Activity
import android.app.Application
import androidx.core.net.toUri
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import java.util.*

class UriBuilder {

    fun createUrl(
        deepLink: String,
        data: MutableMap<String, Any>?,
        activity: Activity
    ): String {
        val gadid =
            AdvertisingIdClient.getAdvertisingIdInfo(activity.applicationContext).id.toString()

        val url = Consts.BASE_URL.toUri().buildUpon().apply {
            appendQueryParameter(Consts.SECURE_GET_PARAMETR, Consts.SECURE_KEY)
            appendQueryParameter(Consts.DEV_TMZ_KEY, TimeZone.getDefault().id)
            appendQueryParameter(Consts.GADID_KEY, gadid)
            appendQueryParameter(Consts.DEEPLINK_KEY, deepLink)
            appendQueryParameter(Consts.SOURCE_KEY, data?.get("media_source").toString())
            appendQueryParameter(
                Consts.AF_ID_KEY,
                AppsFlyerLib.getInstance().getAppsFlyerUID(activity.applicationContext)
            )
            appendQueryParameter(Consts.ADSET_ID_KEY, data?.get(DATA_ADSET_ID).toString())
            appendQueryParameter(Consts.CAMPAIGN_ID_KEY, data?.get(DATA_CAMPAIGN_ID).toString())
            appendQueryParameter(Consts.APP_COMPAIGN_KEY, data?.get(DATA_CAMPAIGN).toString())
            appendQueryParameter(Consts.ADSET_KEY, data?.get(DATA_ADSET).toString())
            appendQueryParameter(Consts.ADGROUP_KEY, data?.get(DATA_ADGROUP).toString())
            appendQueryParameter(Consts.ORIG_COST_KEY, data?.get(DATA_ORIG_COST).toString())
            appendQueryParameter(Consts.AF_SITEID_KEY, data?.get(DATA_AF_SITEID).toString())

        }.toString()
        return url
    }

    companion object {
        val DATA_ADSET_ID = "adset_id"
        val DATA_CAMPAIGN_ID = "campaign_id"
        val DATA_CAMPAIGN = "campaign"
        val DATA_ADSET = "adset"
        val DATA_ADGROUP = "adgroup"
        val DATA_ORIG_COST = "orig_cost"
        val DATA_AF_SITEID = "af_siteid"
    }
}