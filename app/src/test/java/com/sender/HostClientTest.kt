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

        FileUtils.dir = "src/"
        val tfs = ArrayList<TransferFile>()

        val typeOfFile = arrayOf("apk","apk","image","audio","video")

        var pos = 0
        arrayOf("a.in","DualSpace.apk","ab.jpg","audio.mp3","video.mp4").forEach {
            val file = File("src/test/java/com/sender/${it}")

            tfs.add(
                TransferFile(
                    name = file.name,
                    uri = Uri.fromFile(file),
                    size = FileUtils.fetchFileSize(Uri.fromFile(file),context),
                    type = typeOfFile[pos],
                    mimeType = ""
                )
            )
            pos++
        }

        val result =ArrayList<TransferFile>()
        tfs.forEach {
            result.add(it)
        }

        Utils.testing = true
        val thread1 = Thread{
            val h = Host(tfs,context,"127.0.0.1",6789)
            h.send(result)
        }
        thread1.start()

        Thread.sleep(1000)
        val c = Client(tfs,context,"127.0.0.1",6789)

        val thread2=Thread{
            c.receive()
        }
        thread2.start()

        thread1.join()
        thread2.join()

        Assert.assertEquals("${c.getBroker()?.failedSending()!!}","false")

        arrayOf("a.in","DualSpace.apk","ab.jpg","audio.mp3","video.mp4").forEach {
            val file = File("src/test/java/com/sender/${it}")
            Utils.printItems(file.length(),File("src/${it}").length(),FileUtils.fetchFileSize(Uri.fromFile(file),context))
            Assert.assertTrue(file.length()==File("src/${it}").length())
        }

    }
}
