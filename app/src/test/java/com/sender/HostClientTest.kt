package com.sender

import android.content.ContentResolver
import android.content.Context
import android.content.pm.ProviderInfo
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.sender.client.Client
import com.sender.host.Host
import com.sender.models.FileReader
import com.sender.models.TransferFile
import com.sender.util.FileUtils
import com.sender.util.Utils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ContentProviderController
import org.robolectric.annotation.Config
import java.io.File


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HostClientTest {
    @Before
    fun setup() {

    }
    @Test
    fun testAll() {

        val context: Context = ApplicationProvider.getApplicationContext()

        val file = File("src/test/java/com/sender/a.in")
        val tfs = ArrayList<TransferFile>()

        tfs.add(
            TransferFile(
                name = "a.in",
                uri = Uri.fromFile(file),
                size = FileUtils.fetchFileSize(Uri.fromFile(file),context),
                type = "doc",
                mimeType = ""
            )
        )

        Thread{
            val h = Host(tfs,context,"127.0.0.1",6789)
            h.send()
            h.receive()
        }.start()
        Thread.sleep(500)

        val c = Client(tfs,context,"127.0.0.1",6789)

        Thread{
            c.receive()
        }.start()

        Thread.sleep(2000)
        Assert.assertEquals("${c.getBroker()?.failedSending()!!}","false")
    }
}
