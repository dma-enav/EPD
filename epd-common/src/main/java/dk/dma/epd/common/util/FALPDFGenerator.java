/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.common.util;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import dk.dma.enav.model.fal.FALForm1;

public class FALPDFGenerator {

    public void generateFal1Form(FALForm1 fal1form, String filename) {

        try {

            PdfReader pdfReader = new PdfReader("FALForm1.pdf");

            FileOutputStream fileWriteStream = new FileOutputStream(filename);

            PdfStamper pdfStamper = new PdfStamper(pdfReader, fileWriteStream);

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

                PdfContentByte content = pdfStamper.getUnderContent(i);

                // Text over the existing page
                BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.WINANSI, BaseFont.EMBEDDED);
                content.beginText();
                content.setFontAndSize(bf, 8);

                int xFirstColum = 68;
                int xSecondColum = 314;

                int startYFirstColumn = 659;

                int startYSecondColumn = 659;

                // Arrival Depature
                if (fal1form.isArrival()) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", 316, 690, 0);
                } else {
                    // Departure
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", 380, 690, 0);
                }

                // Name and Type of ship
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getNameAndTypeOfShip(), xFirstColum, startYFirstColumn,
                        0);

                // IMO Number
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getImoNumber(), xSecondColum, startYSecondColumn, 0);

                // Call Sign
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getCallSign(), xFirstColum, startYFirstColumn - 30, 0);

                // Voyage Number
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getVoyageNumber(), xSecondColum,
                        startYSecondColumn - 30, 0);

                // Port of Arrival/depature
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getPortOfArrivalDeapture(), xFirstColum,
                        startYFirstColumn - 60, 0);

                // Date and time of arrival/depature
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getDateAndTimeOfArrivalDepature(), xSecondColum,
                        startYFirstColumn - 60, 0);

                // Flag State of ship
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getFlagStateOfShip(), xFirstColum,
                        startYFirstColumn - 90, 0);

                // Name of Master
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getNameOfMaster(), xFirstColum + 135,
                        startYFirstColumn - 90, 0);

                // Last port of call/next port of all
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getLastPortOfCall(), xSecondColum,
                        startYFirstColumn - 90, 0);

                // Certificate of registry
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getCertificateOfRegistry(), xFirstColum,
                        startYFirstColumn - 120, 0);

                String nameAndContact = fal1form.getNameAndContactDetalsOfShipsAgent();

                addMultiLine(nameAndContact, startYFirstColumn, xSecondColum, content, 54, 120);

                // Gross Tonnage
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getGrossTonnage(), xFirstColum,
                        startYFirstColumn - 150, 0);

                // Net Tonnage
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getNetTonnage(), xFirstColum + 135,
                        startYFirstColumn - 150, 0);

                // Position of the ship in the port
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getPositionOfTheShip(), xFirstColum,
                        startYFirstColumn - 180, 0);

                // Brief particulars of voyage
                String briefVoyageParticulars = fal1form.getBriefParticulars();

                addMultiLine(briefVoyageParticulars, startYFirstColumn, xFirstColum, content, 140, 210);

                // Brief particulars of cargo
                String briefCargoParticulars = fal1form.getBriefDescriptionOfCargo();

                addMultiLine(briefCargoParticulars, startYFirstColumn, xFirstColum, content, 140, 257);

                // Number of Crew
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getNumberOfCrew(), xFirstColum,
                        startYFirstColumn - 305, 0);

                // Number of Passengers
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, fal1form.getNumberOfPassengers(), xFirstColum + 130,
                        startYFirstColumn - 305, 0);

                // Remarks
                String remarks = fal1form.getRemarks();
                addMultiLine(remarks, startYFirstColumn, xSecondColum, content, 54, 305);

                // Ship waste requirements
                String wasteRequirements = fal1form.getShipWasteRequirements();
                addMultiLine(wasteRequirements, startYFirstColumn, xSecondColum, content, 54, 405);

                content.endText();
            }

            pdfStamper.close();
            fileWriteStream.close();
            fileWriteStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private void addMultiLine(String text, int startYFirstColumn, int xSecondColum, PdfContentByte content, int width, int modifier) {

        String parsedtext = "";
        int length = text.length();
        int startPosition = startYFirstColumn - modifier;

        while (text.length() > width) {
            parsedtext = text.substring(0, width);
            if (!parsedtext.subSequence(parsedtext.length() - 1, parsedtext.length()).equals(" ")) {
                parsedtext = parsedtext + "-";
            }

            // Name and contact detals of ships agent
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, parsedtext, xSecondColum, startPosition, 0);

            text = text.substring(width, length);

            length = text.length();
            startPosition = startPosition - 10;
        }

        // Name and contact detals of ships agent last part
        content.showTextAligned(PdfContentByte.ALIGN_LEFT, text, xSecondColum, startPosition, 0);
    }

}
