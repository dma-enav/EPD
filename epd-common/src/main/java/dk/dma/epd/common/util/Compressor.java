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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for doing compression and decompression
 */
public class Compressor {
    
    private static final Logger LOG =LoggerFactory.getLogger(Compressor.class);

    public static byte[] compress(byte[] content) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
              gzipOutputStream.write(content);
        }
        LOG.debug(String.format("Compressed ratio: %.2f percent\n", (double)content.length / (double)byteArrayOutputStream.size()));
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] decompress(byte[] contentBytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (
            GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(contentBytes))) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] res = out.toByteArray();
        LOG.debug(String.format("Decompress rate: %.2f\n", (double)res.length / (double)contentBytes.length));
        return res;
    }

}
