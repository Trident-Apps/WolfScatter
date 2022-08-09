package co.spiritbomb.skyc.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import co.spiritbomb.skyc.databinding.WebViewAcitivtyBinding
import co.spiritbomb.skyc.repository.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebViewActivity : AppCompatActivity() {
    private var _binding: WebViewAcitivtyBinding? = null
    private val binding get() = _binding!!
    private lateinit var webView: WebView
    private var messageAb: ValueCallback<Array<Uri?>>? = null
    private val rep = Repo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = WebViewAcitivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val urlFromIntent = intent.getStringExtra(INTENT_EXTRA)
        webView = binding.webView
        webView.loadUrl(urlFromIntent!!)
        webView.webViewClient = LocalClient()
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = false
        webView.settings.userAgentString = USER_AGENT
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                messageAb = filePathCallback
                selectImageIfNeed()
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(applicationContext)
                newWebView.webChromeClient = this
                newWebView.settings.javaScriptEnabled = true
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                newWebView.settings.domStorageEnabled = true
                newWebView.settings.setSupportMultipleWindows(true)
                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }
    }

    private inner class LocalClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            CookieManager.getInstance().flush()
            url?.let {
                if (url == BASE_URL) {
                    startActivity(Intent(this@WebViewActivity, GameHostActivity::class.java))
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (rep.checkDataStoreValue(KEY, applicationContext).isNullOrEmpty()) {
                            val dataStoreKey = stringPreferencesKey(KEY)
                            applicationContext.dataStore.edit { pref ->
                                pref[dataStoreKey] = DATASTORE_KEY
                            }
                            rep.saveUrl(url, applicationContext)
                        }
                    }
                }
            }
        }
    }

    private fun selectImageIfNeed() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = IMAGE_TYPE
        startActivityForResult(
            Intent.createChooser(i, IMAGE_TITLE),
            RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            messageAb?.onReceiveValue(null)
            return
        } else if (resultCode == Activity.RESULT_OK) {
            if (messageAb == null) return

            messageAb!!.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    data
                )
            )
            messageAb = null
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val BASE_URL = "https://wolfscatter.world/"
        const val INTENT_EXTRA = "url"
        const val RESULT_CODE = 1
        const val IMAGE_TITLE = "Image Chooser"
        const val IMAGE_TYPE = "image/*"
        const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 7.0; SM-G930V Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36"
        const val KEY = "sharedPref"
        const val DATASTORE_KEY = "final"

    }
}