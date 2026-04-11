package com.sameerasw.essentials.utils

import android.graphics.Matrix
import android.graphics.Path
import androidx.compose.material3.MaterialShapes
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import java.util.Random

object AmbientMusicShapeHelper {

    private val allShapes = listOf(
        MaterialShapes.Circle,
        MaterialShapes.Square,
        MaterialShapes.Slanted,
        MaterialShapes.Arch,
        MaterialShapes.Oval,
        MaterialShapes.Pill,
        MaterialShapes.Triangle,
        MaterialShapes.Arrow,
        MaterialShapes.Diamond,
        MaterialShapes.ClamShell,
        MaterialShapes.Pentagon,
        MaterialShapes.Gem,
        MaterialShapes.Sunny,
        MaterialShapes.VerySunny,
        MaterialShapes.Cookie4Sided,
        MaterialShapes.Cookie6Sided,
        MaterialShapes.Cookie7Sided,
        MaterialShapes.Cookie9Sided,
        MaterialShapes.Cookie12Sided,
        MaterialShapes.Clover4Leaf,
        MaterialShapes.Clover8Leaf,
        MaterialShapes.SoftBurst,
        MaterialShapes.Flower,
        MaterialShapes.PuffyDiamond,
        MaterialShapes.Ghostish,
        MaterialShapes.PixelCircle,
        MaterialShapes.Bun,
        MaterialShapes.Heart
    )

    fun getShapePath(seed: String?, size: Float): Path {
        val hash = seed?.hashCode() ?: 0
        val random = Random(hash.toLong())
        val shape = allShapes[random.nextInt(allShapes.size)]
        
        return shape.toAndroidPath(size)
    }

    fun getRandomShapePath(size: Float): Path {
        val random = Random()
        val shape = allShapes[random.nextInt(allShapes.size)]
        return shape.toAndroidPath(size)
    }

    private fun RoundedPolygon.toAndroidPath(size: Float): Path {
        val path = Path()
        val composePath = this.toPath()
        val matrix = Matrix()
        
        matrix.postScale(size, size)
        
        val androidPath = Path()
        val resultPath = this.toPath()
        val matrixObj = android.graphics.Matrix()
        matrixObj.postScale(size, size)
        resultPath.transform(matrixObj)
        
        return resultPath
    }
}
