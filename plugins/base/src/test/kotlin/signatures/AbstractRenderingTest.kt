package signatures

import org.jetbrains.dokka.base.testApi.testRunner.BaseAbstractTest
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import utils.TestOutputWriterPlugin
import java.nio.file.Path
import java.nio.file.Paths

abstract class AbstractRenderingTest : BaseAbstractTest() {
    val testDataDir: Path = getTestDataDir("multiplatform/basicMultiplatformTest").toAbsolutePath()

    val configuration = dokkaConfiguration {
        moduleName = "example"
        sourceSets {
            val common = sourceSet {
                name = "common"
                displayName = "common"
                analysisPlatform = "common"
                sourceRoots = listOf(Paths.get("$testDataDir/commonMain/kotlin").toString())
            }
            val jvmAndJsSecondCommonMain = sourceSet {
                name = "jvmAndJsSecondCommonMain"
                displayName = "jvmAndJsSecondCommonMain"
                analysisPlatform = "common"
                dependentSourceSets = setOf(common.value.sourceSetID)
                sourceRoots = listOf(Paths.get("$testDataDir/jvmAndJsSecondCommonMain/kotlin").toString())
            }
            val js = sourceSet {
                name = "js"
                displayName = "js"
                analysisPlatform = "js"
                dependentSourceSets = setOf(common.value.sourceSetID, jvmAndJsSecondCommonMain.value.sourceSetID)
                sourceRoots = listOf(Paths.get("$testDataDir/jsMain/kotlin").toString())
            }
            val jvm = sourceSet {
                name = "jvm"
                displayName = "jvm"
                analysisPlatform = "jvm"
                dependentSourceSets = setOf(common.value.sourceSetID, jvmAndJsSecondCommonMain.value.sourceSetID)
                sourceRoots = listOf(Paths.get("$testDataDir/jvmMain/kotlin").toString())
            }
        }
    }

    fun TestOutputWriterPlugin.renderedContent(path: String): Element = writer.contents.getValue(path)
        .let { Jsoup.parse(it) }.select("#content").single()

    fun TestOutputWriterPlugin.renderedDivergentContent(path: String): Elements =
        renderedContent(path).select("div.divergent-group")

    fun TestOutputWriterPlugin.renderedSourceDepenentContent(path: String): Elements =
        renderedContent(path).select("div.sourceset-depenent-content")

    val Element.brief: String
        get() = children().select("p").text()

    val Element.rawBrief: String
        get() = children().select("p").html()
}