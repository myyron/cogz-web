/*
 * Copyright 2025 Contractors of Ground Zero (CoGZ)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogz.web.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.cogz.web.dto.GameUserDto;
import org.cogz.web.dto.PdfPageDto;
import org.cogz.web.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author altrax
 */
@Service
public class FileServiceImpl implements IFileService {

    Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public void writeImage(MultipartFile image, String basedir, Integer id, Integer parentIdAsDir, Integer size, boolean crop) throws IOException {

        if (image == null) {
            return;
        }

        if (ImageIO.read(image.getInputStream()) == null) {
            return;
        }

        Path path = Paths.get(basedir);
        if (parentIdAsDir != null) {
            path = Files.createDirectories(Paths.get(basedir + parentIdAsDir));
        }
        Path fileNameAndPath = Paths.get(String.valueOf(path), id + ".jpg");

        if (crop) {
            Thumbnails.of(image.getInputStream())
                    .crop(Positions.CENTER)
                    .size(size, size)
                    .outputFormat("jpg")
                    .toFile(fileNameAndPath.toFile());
        } else {
            Thumbnails.of(image.getInputStream())
                    .size(size, size)
                    .outputFormat("jpg")
                    .toFile(fileNameAndPath.toFile());
        }

        logger.info("{} saved", fileNameAndPath.toString());
    }

    @Override
    public void deleteImage(String path, Integer id) {
        Path fileToDeletePath = Paths.get(path + id + ".jpg");
        try {
            Files.delete(fileToDeletePath);
        } catch (IOException e) {
        }
    }

    @Override
    public void generateGameUserListPdf(LocalDate gameSchedule, List<GameUserDto> gameUserDtoList) throws Exception {

        final List<String> fullnameList = getFullnameList(gameUserDtoList);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("data/pdf/cogz-plist-" + gameSchedule.toString() + ".pdf"));
        document.setPageSize(PageSize.LEGAL);
        document.setMargins(20f, 20f, 20f, 60f);

        Image img = Image.getInstance("data/pdf/header.png");
        img.scalePercent(20f);

        Font fontNormal = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Font.NORMAL);
        Font fontBold = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Font.BOLD);

        Paragraph paragraphSchedule = new Paragraph("Game Schedule: ", fontNormal);
        paragraphSchedule.add(new Chunk(gameSchedule.toString(), fontBold));
        paragraphSchedule.setIndentationLeft(30f);
        paragraphSchedule.setSpacingBefore(20f);

        PdfPageDto pdfPageDto = new PdfPageDto();
        pdfPageDto.setDocument(document);
        pdfPageDto.setImageHeader(img);
        pdfPageDto.setFontNormal(fontNormal);
        pdfPageDto.setFontBold(fontBold);
        pdfPageDto.setParagraphSchedule(paragraphSchedule);

        document.open();
        createGuardLogPage(pdfPageDto, gameSchedule, fullnameList);
        document.newPage();
        createPlayerListPage(pdfPageDto, gameSchedule, fullnameList);
        document.close();
    }

    private void createGuardLogPage(PdfPageDto pdfPageDto, LocalDate gameSchedule, List<String> fullnameList) throws DocumentException {

        pdfPageDto.getDocument().add(pdfPageDto.getImageHeader());
        pdfPageDto.getDocument().add(pdfPageDto.getParagraphSchedule());

        Paragraph paragraphTitle = new Paragraph("APPROVED GUEST LIST FOR ENTRY", pdfPageDto.getFontBold());
        paragraphTitle.setAlignment(Element.ALIGN_CENTER);
        pdfPageDto.getDocument().add(paragraphTitle);

        PdfPTable guestListTable = new PdfPTable(9);
        float[] playerListTableColumnsWidth = {25f, 140f, 55f, 55f, 10f, 25f, 140f, 55f, 55f};
        guestListTable.setWidthPercentage(playerListTableColumnsWidth, PageSize.LEGAL);
        guestListTable.setSpacingBefore(10f);
        addTableHeaderGuestListTable(guestListTable, pdfPageDto.getFontBold());
        addRowsPlayerListTable(guestListTable, fullnameList, pdfPageDto.getFontNormal());
        pdfPageDto.getDocument().add(guestListTable);

        Paragraph paragraphNotice = new Paragraph("# # # # #     SYSTEM GENERATED LIST     # # # # # ", pdfPageDto.getFontBold());
        paragraphNotice.setAlignment(Element.ALIGN_CENTER);
        paragraphNotice.setSpacingBefore(20f);
        pdfPageDto.getDocument().add(paragraphNotice);
    }

    private void createPlayerListPage(PdfPageDto pdfPageDto, LocalDate gameSchedule, List<String> fullnameList) throws DocumentException {

        pdfPageDto.getDocument().add(pdfPageDto.getImageHeader());
        pdfPageDto.getDocument().add(pdfPageDto.getParagraphSchedule());

        Paragraph paragraphTitle = new Paragraph("PLAYER LIST", pdfPageDto.getFontBold());
        paragraphTitle.setAlignment(Element.ALIGN_CENTER);
        pdfPageDto.getDocument().add(paragraphTitle);

        PdfPTable playerListTable = new PdfPTable(9);
        float[] playerListTableColumnsWidth = {25f, 140f, 70f, 40f, 10f, 25f, 140f, 70f, 40f};
        playerListTable.setWidthPercentage(playerListTableColumnsWidth, PageSize.LEGAL);
        playerListTable.setSpacingBefore(10f);
        addTableHeaderPlayerListTable(playerListTable, pdfPageDto.getFontBold());
        addRowsPlayerListTable(playerListTable, fullnameList, pdfPageDto.getFontNormal());
        pdfPageDto.getDocument().add(playerListTable);

        PdfPTable summaryTable = new PdfPTable(4);
        float[] summaryTableColumnsWidth = {140f, 140f, 140f, 140f};
        summaryTable.setWidthPercentage(summaryTableColumnsWidth, PageSize.LEGAL);
        summaryTable.setSpacingBefore(30f);
        addTableHeaderSummaryTable(summaryTable, pdfPageDto.getFontBold());
        addRowsSummaryTable(summaryTable);
        pdfPageDto.getDocument().add(summaryTable);
    }

    private void addTableHeaderGuestListTable(PdfPTable table, Font font) {
        Stream.of("#", "Fullname", "Time-IN", "Time-OUT", "", "#", "Fullname", "Time-IN", "Time-OUT")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(header);
                });
    }

    private void addTableHeaderPlayerListTable(PdfPTable table, Font font) {
        Stream.of("#", "Fullname", "Signature", "Chrono", "", "#", "Fullname", "Signature", "Chrono")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(header);
                });
    }

    private void addRowsPlayerListTable(PdfPTable table, List<String> fullnameList, Font font) {

        for (int i = 0, j = (fullnameList.size() / 2) + 1; i < (fullnameList.size() / 2) + 1; i++, j++) {

            PdfPCell colIdx = new PdfPCell();
            colIdx.setPhrase(new Phrase(Integer.toString(i + 1), font));
            table.addCell(colIdx);

            PdfPCell colFullName = new PdfPCell();
            colFullName.setPhrase(new Phrase(fullnameList.get(i), font));
            table.addCell(colFullName);

            table.addCell("");
            table.addCell("");

            PdfPCell colDivider = new PdfPCell();
            colDivider.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(colDivider);

            if (j < fullnameList.size()) {
                PdfPCell colIdx2 = new PdfPCell();
                colIdx2.setPhrase(new Phrase(Integer.toString(j + 1), font));
                table.addCell(colIdx2);

                PdfPCell colFullName2 = new PdfPCell();
                colFullName2.setPhrase(new Phrase(fullnameList.get(i), font));
                table.addCell(colFullName2);

                table.addCell("");
                table.addCell("");
            } else {
                PdfPCell colIdx2 = new PdfPCell();
                colIdx2.setPhrase(new Phrase(Integer.toString(j + 1), font));
                table.addCell(colIdx2);

                table.addCell("");
                table.addCell("");
                table.addCell("");
            }
        }
    }

    private void addTableHeaderSummaryTable(PdfPTable table, Font font) {
        Stream.of("Total Players", "Amount", "Prepared by CoGZ\n(Name and Signature)", "MJC Office on Duty\n(Name and Signature)")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setHorizontalAlignment(1);
                    header.setVerticalAlignment(1);
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(header);
                });
    }

    private void addRowsSummaryTable(PdfPTable table) {
        Stream.of("", "", "", "")
                .forEach(columnTitle -> {
                    PdfPCell blankCell = new PdfPCell();
                    blankCell.setPaddingBottom(50f);
                    blankCell.setPhrase(new Phrase(Chunk.NEWLINE));
                    table.addCell(blankCell);
                });
    }

    private List<String> getFullnameList(List<GameUserDto> gameUserDtoList) {
        List<String> result = new ArrayList<>();
        for (GameUserDto gameUserDto : gameUserDtoList) {
            UserDto userDto = gameUserDto.getUser();
            String fullname = userDto.getLastname() + ", " + userDto.getFirstname();
            result.add(fullname);
        }
        Collections.sort(result);
        return result;
    }
}
