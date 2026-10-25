package ir.siamak.fintrack.store.core.pdf

/** QR Model 2, version 2-L, byte mode, mask 0; encodes only the public tracking reference. */
internal object TrackingQr {
    fun encode(text: String): Array<BooleanArray> {
        val bytes = text.toByteArray(Charsets.US_ASCII)
        require(bytes.size <= 32)
        val bits = mutableListOf<Boolean>()
        fun append(value: Int, count: Int) { for(i in count - 1 downTo 0) bits += ((value ushr i) and 1) != 0 }
        append(4,4); append(bytes.size,8); bytes.forEach { append(it.toInt() and 255,8) }
        append(0,minOf(4,272-bits.size)); while(bits.size % 8 != 0) bits += false
        var pad = 0xec
        while(bits.size < 272) { append(pad,8); pad = pad xor 0xfd }
        val data = IntArray(34) { n -> (0..7).fold(0) { value, bit -> (value shl 1) or if(bits[n*8+bit]) 1 else 0 } }
        fun multiply(a: Int, b: Int): Int {
            var x = a; var y = b; var result = 0
            repeat(8) { if(y and 1 != 0) result = result xor x; x = (x shl 1) xor if(x and 128 != 0) 0x11d else 0; y = y ushr 1 }
            return result
        }
        var generator = intArrayOf(1); var root = 1
        repeat(10) {
            val next = IntArray(generator.size+1)
            generator.forEachIndexed { j, value -> next[j] = next[j] xor value; next[j+1] = next[j+1] xor multiply(value, root) }
            generator = next; root = multiply(root,2)
        }
        val remainder = IntArray(10)
        data.forEach { value ->
            val factor = value xor remainder[0]
            for(j in 0..8) remainder[j] = remainder[j+1]
            remainder[9] = 0
            for(j in 0..9) remainder[j] = remainder[j] xor multiply(generator[j+1],factor)
        }
        val payload = data + remainder
        val matrix = Array(25) { BooleanArray(25) }; val reserved = Array(25) { BooleanArray(25) }
        fun function(x: Int,y: Int,dark: Boolean) { if(x in 0..24 && y in 0..24) { matrix[y][x] = dark; reserved[y][x] = true } }
        for(i in 0..24) { function(6,i,i%2 == 0); function(i,6,i%2 == 0) }
        for((cx,cy) in listOf(3 to 3,21 to 3,3 to 21)) for(dy in -4..4) for(dx in -4..4) {
            val distance = maxOf(kotlin.math.abs(dx),kotlin.math.abs(dy))
            function(cx+dx,cy+dy,distance != 2 && distance != 4)
        }
        for(dy in -2..2) for(dx in -2..2) function(18+dx,18+dy,maxOf(kotlin.math.abs(dx),kotlin.math.abs(dy)) != 1)
        val format = 0x77c4
        fun bit(index: Int) = (format ushr index) and 1 != 0
        for(i in 0..5) function(8,i,bit(i))
        function(8,7,bit(6)); function(8,8,bit(7)); function(7,8,bit(8))
        for(i in 9..14) function(14-i,8,bit(i))
        for(i in 0..7) function(24-i,8,bit(i))
        for(i in 8..14) function(8,10+i,bit(i))
        function(8,17,true)
        var index = 0; var right = 24
        while(right >= 1) {
            if(right == 6) right--
            for(vertical in 0..24) for(j in 0..1) {
                val x = right-j; val y = if((right+1) and 2 == 0) 24-vertical else vertical
                if(!reserved[y][x]) {
                    val value = if(index < payload.size*8) (payload[index/8] ushr (7-index%8)) and 1 != 0 else false
                    matrix[y][x] = value xor ((x+y)%2 == 0); index++
                }
            }
            right -= 2
        }
        return matrix
    }
}
