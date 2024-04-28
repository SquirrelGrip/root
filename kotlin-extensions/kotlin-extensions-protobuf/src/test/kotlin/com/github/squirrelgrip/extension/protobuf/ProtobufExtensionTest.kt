package com.github.squirrelgrip.extension.protobuf

import com.github.squirrelgrip.extension.encode.fromBase64
import com.github.squirrelgrip.extension.encode.fromHex
import com.github.squirrelgrip.extension.encode.toBase64
import com.github.squirrelgrip.extension.encode.toHex
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class ProtobufExtensionTest {
    @Test
    fun `toProtobuf`() {
        assertThat(Sample().toProtobuf().toHex()).isEqualTo("0800120F412053696D706C6520537472696E671A01311A03414141")
        assertThat(Sample().toProtobuf().toBase64()).isEqualTo("CAASD0EgU2ltcGxlIFN0cmluZxoBMRoDQUFB")
        assertThat("CAASD0EgU2ltcGxlIFN0cmluZxoBMRoDQUFB".fromBase64().toInstance<Sample>()).isEqualTo(Sample())
        assertThat("0800120F412053696D706C6520537472696E671A01311A03414141".fromHex().toInstance<Sample>()).isEqualTo(Sample())
    }
}
