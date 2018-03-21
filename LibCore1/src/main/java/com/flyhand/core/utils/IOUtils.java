/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flyhand.core.utils;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class IOUtils {
    public static final char DIR_SEPARATOR_UNIX = '/';
    public static final char DIR_SEPARATOR_WINDOWS = '\\';
    public static final char DIR_SEPARATOR = File.separatorChar;
    public static final String LINE_SEPARATOR_UNIX = "\n";
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
    public static final String LINE_SEPARATOR;

    static {
        StringWriter buf = new StringWriter(4);
        PrintWriter out = new PrintWriter(buf);
        out.println();
        LINE_SEPARATOR = buf.toString();
        out.close();
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int SKIP_BUFFER_SIZE = 2048;
    private static char[] SKIP_CHAR_BUFFER;
    private static byte[] SKIP_BYTE_BUFFER;


    public IOUtils() {
        super();
    }


    public static void closeQuietly(Reader input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(Writer output) {
        closeQuietly((Closeable) output);
    }


    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable) input);
    }


    public static void closeQuietly(OutputStream output) {
        closeQuietly((Closeable) output);
    }


    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static void closeQuietly(HttpURLConnection conn) {
        try {
            if (conn != null) {
                conn.disconnect();
            }
        } catch (Exception ioe) {
            // ignore
        }
    }

    public static void releaseLock(FileLock fileLock) {
        try {
            if (fileLock != null) {
                fileLock.release();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }


    public static void closeQuietly(Socket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException ioe) {
                // ignored
            }
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }


    public static byte[] toByteArray(InputStream input, long size) throws IOException {

        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Size cannot be greater than Integer max value: " + size);
        }

        return toByteArray(input, (int) size);
    }

    public static byte[] toByteArray(InputStream input, int size) throws IOException {

        if (size < 0) {
            throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
        }

        if (size == 0) {
            return new byte[0];
        }

        byte[] data = new byte[size];
        int offset = 0;
        int readed;

        while (offset < size && (readed = input.read(data, offset, (size - offset))) != -1) {
            offset += readed;
        }

        if (offset != size) {
            throw new IOException("Unexpected readed size. current: " + offset + ", excepted: " + size);
        }

        return data;
    }

    public static byte[] toByteArray(Reader input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }


    public static byte[] toByteArray(Reader input, String encoding)
            throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, encoding);
        return output.toByteArray();
    }


    public static byte[] toByteArray(String input) throws IOException {
        return input.getBytes();
    }


    public static char[] toCharArray(InputStream is) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        copy(is, output);
        return output.toCharArray();
    }


    public static char[] toCharArray(InputStream is, String encoding)
            throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        copy(is, output, encoding);
        return output.toCharArray();
    }


    public static char[] toCharArray(Reader input) throws IOException {
        CharArrayWriter sw = new CharArrayWriter();
        copy(input, sw);
        return sw.toCharArray();
    }


    public static String toString(InputStream input) throws IOException {
        return toString(input, null);
    }


    public static String toString(InputStream input, String encoding)
            throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }


    public static String toString(Reader input) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }


    public static String toString(URI uri) throws IOException {
        return toString(uri, null);
    }


    public static String toString(URI uri, String encoding) throws IOException {
        return toString(uri.toURL(), encoding);
    }


    public static String toString(URL url) throws IOException {
        return toString(url, null);
    }


    public static String toString(URL url, String encoding) throws IOException {
        InputStream inputStream = url.openStream();
        try {
            return toString(inputStream, encoding);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static String toString(byte[] input) throws IOException {
        return new String(input);
    }

    public static String toString(byte[] input, String encoding)
            throws IOException {
        if (encoding == null) {
            return new String(input);
        } else {
            return new String(input, encoding);
        }
    }

    public static List<String> readLines(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input);
        return readLines(reader);
    }

    public static List<String> readLines(InputStream input, String encoding) throws IOException {
        if (encoding == null) {
            return readLines(input);
        } else {
            InputStreamReader reader = new InputStreamReader(input, encoding);
            return readLines(reader);
        }
    }


    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        List<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

    public static InputStream toInputStream(CharSequence input) {
        return toInputStream(input.toString());
    }


    public static InputStream toInputStream(CharSequence input, String encoding) throws IOException {
        return toInputStream(input.toString(), encoding);
    }


    public static InputStream toInputStream(String input) {
        byte[] bytes = input.getBytes();
        return new ByteArrayInputStream(bytes);
    }


    public static InputStream toInputStream(String input, String encoding) throws IOException {
        byte[] bytes = encoding != null ? input.getBytes(encoding) : input.getBytes();
        return new ByteArrayInputStream(bytes);
    }


    public static void write(byte[] data, OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data);
        }
    }


    public static void write(byte[] data, Writer output) throws IOException {
        if (data != null) {
            output.write(new String(data));
        }
    }


    public static void write(byte[] data, Writer output, String encoding)
            throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            } else {
                output.write(new String(data, encoding));
            }
        }
    }


    public static void write(char[] data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }


    public static void write(char[] data, OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes());
        }
    }


    public static void write(char[] data, OutputStream output, String encoding)
            throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            } else {
                output.write(new String(data).getBytes(encoding));
            }
        }
    }


    public static void write(CharSequence data, Writer output) throws IOException {
        if (data != null) {
            write(data.toString(), output);
        }
    }


    public static void write(CharSequence data, OutputStream output)
            throws IOException {
        if (data != null) {
            write(data.toString(), output);
        }
    }


    public static void write(CharSequence data, OutputStream output, String encoding)
            throws IOException {
        if (data != null) {
            write(data.toString(), output, encoding);
        }
    }


    public static void write(String data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }


    public static void write(String data, OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data.getBytes());
        }
    }


    public static void write(String data, OutputStream output, String encoding)
            throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            } else {
                output.write(data.getBytes(encoding));
            }
        }
    }


    public static void writeLines(Collection<?> lines, String lineEnding,
                                  OutputStream output) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        for (Object line : lines) {
            if (line != null) {
                output.write(line.toString().getBytes());
            }
            output.write(lineEnding.getBytes());
        }
    }


    public static void writeLines(Collection<?> lines, String lineEnding,
                                  OutputStream output, String encoding) throws IOException {
        if (encoding == null) {
            writeLines(lines, lineEnding, output);
        } else {
            if (lines == null) {
                return;
            }
            if (lineEnding == null) {
                lineEnding = LINE_SEPARATOR;
            }
            for (Object line : lines) {
                if (line != null) {
                    output.write(line.toString().getBytes(encoding));
                }
                output.write(lineEnding.getBytes(encoding));
            }
        }
    }


    public static void writeLines(Collection<?> lines, String lineEnding,
                                  Writer writer) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        for (Object line : lines) {
            if (line != null) {
                writer.write(line.toString());
            }
            writer.write(lineEnding);
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }


    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }


    public static void copy(InputStream input, Writer output)
            throws IOException {
        InputStreamReader in = new InputStreamReader(input);
        copy(in, output);
    }


    public static void copy(InputStream input, Writer output, String encoding)
            throws IOException {
        if (encoding == null) {
            copy(input, output);
        } else {
            InputStreamReader in = new InputStreamReader(input, encoding);
            copy(in, output);
        }
    }


    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }


    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }


    public static void copy(Reader input, OutputStream output)
            throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(output);
        copy(input, out);
        out.flush();
    }


    public static void copy(Reader input, OutputStream output, String encoding)
            throws IOException {
        if (encoding == null) {
            copy(input, output);
        } else {
            OutputStreamWriter out = new OutputStreamWriter(output, encoding);
            copy(input, out);
            out.flush();
        }
    }


    public static boolean contentEquals(InputStream input1, InputStream input2)
            throws IOException {
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }

        int ch = input1.read();
        while (-1 != ch) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        int ch2 = input2.read();
        return (ch2 == -1);
    }


    public static boolean contentEquals(Reader input1, Reader input2)
            throws IOException {
        if (!(input1 instanceof BufferedReader)) {
            input1 = new BufferedReader(input1);
        }
        if (!(input2 instanceof BufferedReader)) {
            input2 = new BufferedReader(input2);
        }

        int ch = input1.read();
        while (-1 != ch) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        int ch2 = input2.read();
        return (ch2 == -1);
    }


    public static long skip(InputStream input, long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }

        if (SKIP_BYTE_BUFFER == null) {
            SKIP_BYTE_BUFFER = new byte[SKIP_BUFFER_SIZE];
        }
        long remain = toSkip;
        while (remain > 0) {
            long n = input.read(SKIP_BYTE_BUFFER, 0, (int) Math.min(remain, SKIP_BUFFER_SIZE));
            if (n < 0) { // EOF
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }

    public static long skip(Reader input, long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }

        if (SKIP_CHAR_BUFFER == null) {
            SKIP_CHAR_BUFFER = new char[SKIP_BUFFER_SIZE];
        }
        long remain = toSkip;
        while (remain > 0) {
            long n = input.read(SKIP_CHAR_BUFFER, 0, (int) Math.min(remain, SKIP_BUFFER_SIZE));
            if (n < 0) { // EOF
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }


    public static void skipFully(InputStream input, long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
        }
        long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
        }
    }

    public static void skipFully(Reader input, long toSkip) throws IOException {
        long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
        }
    }
}
