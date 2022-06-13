package com.iartr.smartmirror.ui.weather

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException

class VolleyUTF8EncodingStringRequest(
    method: Int, url: String, private val mListener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : Request<String>(method, url, errorListener) {

    override fun deliverResponse(response: String) {
        mListener.onResponse(response)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
        var parsed = ""

        val encoding = charset(HttpHeaderParser.parseCharset(response.headers))

        try {
            parsed = String(response.data, encoding)
            val bytes = parsed.toByteArray(encoding)
            parsed = String(bytes, charset("UTF-8"))

            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            return Response.error(ParseError(e))
        }
    }
}