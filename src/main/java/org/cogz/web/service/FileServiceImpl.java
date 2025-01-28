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

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.name.Rename;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author altrax
 */
@Service
public class FileServiceImpl implements IFileService {

    Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public void writeImage(MultipartFile image, String basedir, Integer id, Integer parentIdAsDir, Integer size, boolean crop) throws IOException {

        if (image == null) return;

        if (ImageIO.read(image.getInputStream()) == null) return;

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
}
