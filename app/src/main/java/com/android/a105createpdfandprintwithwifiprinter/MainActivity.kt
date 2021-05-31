package com.android.a105createpdfandprintwithwifiprinter

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.widget.Toast
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    val file_name:String = "test_pdf.pdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    btn_create_pdf.setOnClickListener {
                        createPDFFile(Common.getAppPath(this@MainActivity)+file_name)
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    TODO("Not yet implemented")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    TODO("Not yet implemented")
                }

            }).check()

    }

    private fun createPDFFile(path: String) {
        if (File(path).exists()){
            File(path).delete()
        }

        try {
            val document = Document()

            //save
            PdfWriter.getInstance(document,FileOutputStream(path))

            //open to write
            document.open()

            //setting
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("Fahmi")
            document.addCreator("Abdul")

            //font setting
            val colorAccent = BaseColor(0,153,204,255)
            val headingFontSize = 20.0f
            val valueFontSize = 26.0f

            //custom font
            val fontName = BaseFont.createFont("assets/fonts/poppins_reguler.ttf","UTF-8",BaseFont.EMBEDDED)

            //Add title to document
            val titleStyle = Font(fontName,36.0f,Font.NORMAL,BaseColor.BLACK)
            addNewItem(document,"Order Details",Element.ALIGN_CENTER,titleStyle)

            val headingStyle = Font(fontName,headingFontSize,Font.NORMAL, colorAccent)
            addNewItem(document,"Order No",Element.ALIGN_LEFT,headingStyle)
            val valueStyle = Font(fontName,headingFontSize,Font.NORMAL, BaseColor.BLACK)
            addNewItem(document,"#123123",Element.ALIGN_LEFT,valueStyle)

            addLineSeperator(document)
            addNewItem(document,"Order Date",Element.ALIGN_LEFT,headingStyle)
            addNewItem(document,"03/08/2021",Element.ALIGN_LEFT,valueStyle)

            addLineSeperator(document)
            addNewItem(document,"Account Name",Element.ALIGN_LEFT,headingStyle)
            addNewItem(document,"Fahmi Abdul Aziz",Element.ALIGN_LEFT,valueStyle)

            addLineSeperator(document)

            //Product detail
            addLineSpace(document)
            addNewItem(document,"Product Details",Element.ALIGN_CENTER,titleStyle)

            addLineSeperator(document)

            //item 1
            addNewItemWithLeftAndRight(document,"Pizza 25","(0.0%)",titleStyle,valueStyle)
            addNewItemWithLeftAndRight(document,"12.0*1000","12000.0",titleStyle,valueStyle)

            addLineSeperator(document)

            //item 2
            addNewItemWithLeftAndRight(document,"Pizza 26","(0.0%)",titleStyle,valueStyle)
            addNewItemWithLeftAndRight(document,"12.0*1000","12000.0",titleStyle,valueStyle)

            addLineSeperator(document)

            //total
            addLineSpace(document)
            addLineSpace(document)

            addNewItemWithLeftAndRight(document,"Total","24000.0",titleStyle,valueStyle)

            //close
            document.close()

            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

            printPdf()
        }catch (e:Exception){
            Log.e(MainActivity::class.simpleName,""+e.message)
        }
    }

    private fun printPdf() {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        try {
            val printAdapter = PdfDocumentAdapter(this,Common.getAppPath(this)+file_name)
            printManager.print("Document",printAdapter,PrintAttributes.Builder().build())
        }catch (e:Exception){
            Log.e(MainActivity::class.simpleName,e.message?:"")
        }
    }

    @Throws(DocumentException::class)
    private fun addNewItemWithLeftAndRight(
        document: Document,
        textLeft: String,
        textRight: String,
        leftStyle: Font,
        rightStyle: Font
    ) {
        val chunkTextLeft = Chunk(textLeft,leftStyle)
        val chunkTextRight = Chunk(textRight,rightStyle)
        val p = Paragraph(chunkTextLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkTextRight)
        document.add(p)
    }

    @Throws(DocumentException::class)
    private fun addLineSeperator(document: Document) {
        val lineSeparator = LineSeparator()
        lineSeparator.lineColor= BaseColor(0,0,0,68)
        addLineSpace(document)
        document.add(Chunk(lineSeparator))
        addLineSpace(document)
    }

    @Throws(DocumentException::class)
    private fun addLineSpace(document: Document) {
        document.add(Paragraph(""))
    }

    @Throws(DocumentException::class)
    private fun addNewItem(document: Document, text: String, align: Int, style: Font) {
        val chunk = Chunk(text,style)
        val p = Paragraph(chunk)
        p.alignment = align
        document.add(p)
    }
}