package com.phonefocusfarm.feature.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import androidx.core.content.FileProvider
import com.phonefocusfarm.common.constants.AppConstants
import com.phonefocusfarm.common.models.AnimalType
import android.graphics.RectF
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.File
import java.io.FileOutputStream

object ShareUtil {
    fun buildPoster(context: Context, title: String, lines: List<String>, width: Int = 1080, height: Int = 1350): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)
        val titlePaint = Paint()
        titlePaint.isAntiAlias = true
        titlePaint.textSize = 64f
        titlePaint.color = Color.BLACK
        canvas.drawText(title, 60f, 120f, titlePaint)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.textSize = 42f
        paint.color = Color.DKGRAY
        var y = 220f
        lines.forEach {
            canvas.drawText(it, 60f, y, paint)
            y += 64f
        }
        return bmp
    }

    fun saveBitmap(context: Context, bmp: Bitmap): Uri {
        val file = File(context.cacheDir, "share_poster.jpg")
        FileOutputStream(file).use { fos ->
            bmp.compress(Bitmap.CompressFormat.JPEG, AppConstants.SHARE_IMAGE_QUALITY, fos)
        }
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    }

    fun shareToWeChatOrSystem(context: Context, imageUri: Uri) {
        val api = WXAPIFactory.createWXAPI(context, AppConstants.WECHAT_APP_ID, true)
        val installed = api.registerApp(AppConstants.WECHAT_APP_ID)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "分享"))
    }

    fun buildFarmPoster(
        context: Context,
        title: String,
        totalDurationText: String,
        counts: Map<AnimalType, Int>,
        width: Int = 1080,
        height: Int = 1350
    ): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        // 草地背景
        canvas.drawColor(Color.parseColor("#C8E6C9"))
        val fencePaint = Paint().apply { color = Color.parseColor("#8D6E63"); strokeWidth = 8f }
        canvas.drawLine(40f, 40f, width - 40f, 40f, fencePaint)
        canvas.drawLine(40f, height - 300f, width - 40f, height - 300f, fencePaint)

        // 标题与总时长
        val titlePaint = Paint().apply { isAntiAlias = true; textSize = 64f; color = Color.BLACK }
        canvas.drawText(title, 60f, 120f, titlePaint)
        val subPaint = Paint().apply { isAntiAlias = true; textSize = 42f; color = Color.DKGRAY }
        canvas.drawText(totalDurationText, 60f, 180f, subPaint)

        // 在画布中随机分布动物emoji，根据counts数量
        val emojiPaint = Paint().apply { isAntiAlias = true; textSize = 72f }
        val margin = 120f
        val maxX = width - margin
        val maxY = height - 360f
        fun drawAnimal(type: AnimalType) {
            val x = margin + Math.random().toFloat() * (maxX - margin)
            val y = margin + Math.random().toFloat() * (maxY - margin)
            canvas.drawText(type.emoji, x, y, emojiPaint)
        }
        counts.forEach { (type, count) ->
            repeat(count) { drawAnimal(type) }
        }

        return bmp
    }

    fun buildFarmPosterWithImage(
        context: Context,
        title: String,
        totalDurationText: String,
        farmImage: Bitmap,
        width: Int = 1080,
        height: Int = 1350
    ): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)

        // 标题与总时长
        val titlePaint = Paint().apply { isAntiAlias = true; textSize = 64f; color = Color.BLACK }
        canvas.drawText(title, 60f, 120f, titlePaint)
        val subPaint = Paint().apply { isAntiAlias = true; textSize = 42f; color = Color.DKGRAY }
        canvas.drawText(totalDurationText, 60f, 180f, subPaint)

        // 绘制农场截图，居中缩放
        val margin = 60f
        val target = RectF(margin, 240f, width - margin, height - margin)
        val srcW = farmImage.width.toFloat()
        val srcH = farmImage.height.toFloat()
        val scale = kotlin.math.min((target.width()) / srcW, (target.height()) / srcH)
        val drawW = srcW * scale
        val drawH = srcH * scale
        val left = target.left + (target.width() - drawW) / 2f
        val top = target.top + (target.height() - drawH) / 2f
        val dest = RectF(left, top, left + drawW, top + drawH)
        canvas.drawBitmap(farmImage, null, dest, null)
        return bmp
    }
}