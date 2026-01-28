/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.logger.HytaleLogger$Api
 */
package com.natamus.hybrid.functions;

import com.hypixel.hytale.logger.HytaleLogger;
import com.natamus.hybrid.data.HybridConstants;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;

public class FileFunctions {
    public static void writeStringToFile(String text, File outputFile) throws IOException {
        FileFunctions.writeStringToFile(text, outputFile, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void writeStringToFile(String text, File outputFile, OpenOption openOption) throws IOException {
        Path parent = outputFile.toPath().getParent();
        if (parent != null) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        Files.writeString(outputFile.toPath(), (CharSequence)text, StandardCharsets.UTF_8, StandardOpenOption.CREATE, openOption);
    }

    public static void createDirectoryIfNotExists(Path path) {
        try {
            if (!Files.exists(path, new LinkOption[0])) {
                Files.createDirectories(path, new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            ((HytaleLogger.Api)HybridConstants.LOGGER.atWarning()).log("Unable to create directory for path: " + path.toString());
        }
    }
}
