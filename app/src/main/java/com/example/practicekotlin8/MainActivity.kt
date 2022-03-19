package com.example.practicekotlin8

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.net.ContentHandler

class MainActivity : AppCompatActivity() {

    private val goHomeButton: ImageButton by lazy {
        findViewById(R.id.goHomeButton)
    }

    private val goBackButton: ImageButton by lazy {
        findViewById(R.id.goBackButton)
    }

    private val goForwardButton: ImageButton by lazy {
        findViewById(R.id.goForwardButton)
    }

    private val addressBar: EditText by lazy {
        findViewById(R.id.addressBar)
    }

    private val webView: WebView by lazy {
        findViewById(R.id.webView)
    }

    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLayout)
    }

    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        bindViews()
    }

    override fun onBackPressed() {
        if(webView.canGoBack()) { //뒤로 갈 수 있는가?(히스토리에 쌓여있는가?)
            webView.goBack()
        } else {
            super.onBackPressed() // 액티비티 종료
        }
    }

    /* WebViewClient와 WebChromeClient의 차이점
        전자는 컨텐츠 로딩 관련
        후자는 브라우저 차원 : 자바 스크립트의 alert, 웹 컨텐츠의 타이틀 등등
     */

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true //사이트 내 자바 스크립트 사용 가능 여부
            loadUrl(DEFAULT_URL) // url 불러오기
        }
    }

    private fun bindViews() {
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        addressBar.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                val loadingUrl = v.text.toString()
                if(URLUtil.isNetworkUrl(loadingUrl)) {
                    webView.loadUrl(loadingUrl)
                }
                webView.loadUrl("http://$loadingUrl")
            }

            return@setOnEditorActionListener false
        }

        goBackButton.setOnClickListener {
            webView.goBack()
        }

        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    inner class WebViewClient: android.webkit.WebViewClient() { //상위 클래스에 접근하기 위해 inner class로 선언

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false
            progressBar.hide()
            goBackButton.isEnabled = webView.canGoBack()
            goForwardButton.isEnabled = webView.canGoForward()
            addressBar.setText(url)
        }
    }

    inner class WebChromeClient: android.webkit.WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }

    companion object {
        private const val DEFAULT_URL = "http://www.google.com"
    }
}


