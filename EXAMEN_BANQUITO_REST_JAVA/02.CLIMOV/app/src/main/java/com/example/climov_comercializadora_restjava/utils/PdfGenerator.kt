package com.example.climov_comercializadora_restjava.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.climov_comercializadora_restjava.models.FacturaResponseDTO
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {
    
    fun generarPdfAmortizacion(context: Context, factura: FacturaResponseDTO): Result<File> {
        return try {
            val credito = factura.infoCredito
            if (credito == null || credito.tablaAmortizacion.isNullOrEmpty()) {
                return Result.failure(IllegalStateException("No hay tabla de amortización disponible"))
            }
            
            // Crear directorio de descargas
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            
            // Nombre del archivo
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Amortizacion_Factura_${factura.idFactura}_$timestamp.pdf"
            val file = File(downloadsDir, fileName)
            
            // Crear PDF
            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument, PageSize.A4)
            document.setMargins(40f, 40f, 40f, 40f)
            
            // Color morado
            val purpleColor = DeviceRgb(102, 126, 234)
            
            // Título
            val titulo = Paragraph("TABLA DE AMORTIZACIÓN")
                .setFontSize(20f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(purpleColor)
                .setMarginBottom(10f)
            document.add(titulo)
            
            // Subtítulo
            val subtitulo = Paragraph("Comercializadora Monster")
                .setFontSize(14f)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setMarginBottom(20f)
            document.add(subtitulo)
            
            // Información de la factura
            val infoTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
                .useAllAvailableWidth()
                .setMarginBottom(15f)
            
            infoTable.addCell(createInfoCell("Factura #:", "${factura.idFactura}"))
            infoTable.addCell(createInfoCell("Fecha:", factura.fecha ?: "-"))
            infoTable.addCell(createInfoCell("Cliente:", factura.nombreCliente ?: "-"))
            infoTable.addCell(createInfoCell("Cédula:", factura.cedulaCliente ?: "-"))
            infoTable.addCell(createInfoCell("Crédito #:", "${credito.idCredito}"))
            infoTable.addCell(createInfoCell("Monto:", "$${credito.montoCredito}"))
            infoTable.addCell(createInfoCell("Cuotas:", "${credito.numeroCuotas}"))
            infoTable.addCell(createInfoCell("Tasa:", "${credito.tasaInteres}%"))
            
            document.add(infoTable)
            
            // Tabla de amortización
            val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f, 2f, 2f, 2f)))
                .useAllAvailableWidth()
                .setMarginTop(10f)
            
            // Encabezados
            val headers = listOf("Cuota", "Valor", "Interés", "Capital", "Saldo")
            headers.forEach { header ->
                val cell = Cell()
                    .add(Paragraph(header).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(purpleColor)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8f)
                cell.setBorder(SolidBorder(ColorConstants.WHITE, 1f))
                table.addHeaderCell(cell)
            }
            
            // Datos
            var totalInteres = BigDecimal.ZERO
            var totalCapital = BigDecimal.ZERO
            
            credito.tablaAmortizacion.forEach { cuota ->
                totalInteres = totalInteres.add(cuota.interesPagado)
                totalCapital = totalCapital.add(cuota.capitalPagado)
                
                table.addCell(createDataCell("${cuota.numeroCuota}", TextAlignment.CENTER))
                table.addCell(createDataCell("$${cuota.valorCuota}", TextAlignment.RIGHT))
                table.addCell(createDataCell("$${cuota.interesPagado}", TextAlignment.RIGHT))
                table.addCell(createDataCell("$${cuota.capitalPagado}", TextAlignment.RIGHT))
                table.addCell(createDataCell("$${cuota.saldoRestante}", TextAlignment.RIGHT))
            }
            
            document.add(table)
            
            // Resumen
            val totalTable = Table(UnitValue.createPercentArray(floatArrayOf(3f, 2f)))
                .useAllAvailableWidth()
                .setMarginTop(15f)
            
            totalTable.addCell(createTotalCell("Total Intereses:", false))
            totalTable.addCell(createTotalCell("$${totalInteres.setScale(2, RoundingMode.HALF_UP)}", true))
            totalTable.addCell(createTotalCell("Total Capital:", false))
            totalTable.addCell(createTotalCell("$${totalCapital.setScale(2, RoundingMode.HALF_UP)}", true))
            totalTable.addCell(createTotalCell("Total a Pagar:", false))
            totalTable.addCell(createTotalCell("$${totalInteres.add(totalCapital).setScale(2, RoundingMode.HALF_UP)}", true))
            
            document.add(totalTable)
            
            // Footer
            val footer = Paragraph("Documento generado el ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}")
                .setFontSize(8f)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY)
                .setMarginTop(30f)
            document.add(footer)
            
            document.close()
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun createInfoCell(label: String, value: String): Cell {
        val cell = Cell()
        cell.add(Paragraph("$label $value").setFontSize(10f))
        cell.setPadding(5f)
        cell.setBorder(SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
        return cell
    }
    
    private fun createDataCell(text: String, alignment: TextAlignment): Cell {
        val cell = Cell()
        cell.add(Paragraph(text).setFontSize(9f).setTextAlignment(alignment))
        cell.setPadding(6f)
        cell.setBorder(SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
        return cell
    }
    
    private fun createTotalCell(text: String, isBold: Boolean): Cell {
        val paragraph = Paragraph(text).setFontSize(11f).setTextAlignment(TextAlignment.RIGHT)
        if (isBold) paragraph.setBold()
        
        val cell = Cell()
        cell.add(paragraph)
        cell.setPadding(8f)
        cell.setBorder(SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
        if (isBold) {
            cell.setBackgroundColor(DeviceRgb(240, 240, 240))
        }
        return cell
    }
    
    fun abrirPdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Si no hay app para abrir PDF, compartir el archivo
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(shareIntent, "Abrir PDF con..."))
        }
    }
}
