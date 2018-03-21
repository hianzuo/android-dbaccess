package com.flyhand.core.utils;

import java.util.HashMap;

/**
 * Created by Ryan
 * On 2016/8/12.
 */
public class ContentTypeUtil {
    private static final HashMap<String, String> map = new HashMap<String, String>() {
        {
            put("", "");
            put("ai", " application/postscript");
            put("apk", "application/vnd.android.package-archive");
            put("xap", "application/x-silverlight-app");
            put("ipa", "application/vnd.iphone");
            put("aif", "audio/x-aiff");
            put("aifc", "audio/x-aiff");
            put("aiff", "audio/x-aiff");
            put("asc", "text/plain");
            put("au", "audio/basic");
            put("avi", "video/x-msvideo");
            put("bcpio", "application/x-bcpio");
            put("bin", "application/octet-stream");
            put("bmp", "image/bmp");
            put("cdf", "application/x-netcdf");
            put("class", "application/octet-stream");
            put("cpio", "application/x-cpio");
            put("cpt", "application/mac-compactpro");
            put("csh", "application/x-csh");
            put("css", "text/css");
            put("dcr", "application/x-director");
            put("dir", "application/x-director");
            put("djv", "image/vnd.djvu");
            put("djvu", "image/vnd.djvu");
            put("dll", "application/octet-stream");
            put("dms", "application/octet-stream");
            put("doc", "application/msword");
            put("dvi", "application/x-dvi");
            put("dxr", "application/x-director");
            put("eps", "application/postscript");
            put("etx", "text/x-setext");
            put("exe", "application/octet-stream");
            put("ez", "application/andrew-inset");
            put("gif", "image/gif");
            put("gtar", "application/x-gtar");
            put("hdf", "application/x-hdf");
            put("hqx", "application/mac-binhex40");
            put("htm", "text/html");
            put("html", "text/html");
            put("ice", "x-conference/x-cooltalk");
            put("ief", "image/ief");
            put("iges", "model/iges");
            put("igs", "model/iges");
            put("jpe", "image/jpeg");
            put("jpeg", "image/jpeg");
            put("jpg", "image/jpeg");
            put("js", "application/x-javascript");
            put("kar", "audio/midi");
            put("latex", "application/x-latex");
            put("lha", "application/octet-stream");
            put("lzh", "application/octet-stream");
            put("m3u", "audio/x-mpegurl");
            put("man", "application/x-troff-man");
            put("me", "application/x-troff-me");
            put("mesh", "model/mesh");
            put("mid", "audio/midi");
            put("midi", "audio/midi");
            put("mif", "application/vnd.mif");
            put("mov", "video/quicktime");
            put("movie", "video/x-sgi-movie");
            put("mp2", "audio/mpeg");
            put("mp3", "audio/mpeg");
            put("mpe", "video/mpeg");
            put("mpeg", "video/mpeg");
            put("mpg", "video/mpeg");
            put("mpga", "audio/mpeg");
            put("ms", "application/x-troff-ms");
            put("msh", "model/mesh");
            put("mxu", "video/vnd.mpegurl");
            put("nc", "application/x-netcdf");
            put("oda", "application/oda");
            put("pbm", "image/x-portable-bitmap");
            put("pdb", "chemical/x-pdb");
            put("pdf", "application/pdf");
            put("pgm", "image/x-portable-graymap");
            put("pgn", "application/x-chess-pgn");
            put("png", "image/png");
            put("pnm", "image/x-portable-anymap");
            put("ppm", "image/x-portable-pixmap");
            put("ppt", "application/vnd.ms-powerpoint");
            put("ps", "application/postscript");
            put("qt", "video/quicktime");
            put("ra", "audio/x-realaudio");
            put("ram", "audio/x-pn-realaudio");
            put("ras", "image/x-cmu-raster");
            put("rgb", "image/x-rgb");
            put("rm", "audio/x-pn-realaudio");
            put("roff", "application/x-troff");
            put("rpm", "audio/x-pn-realaudio-plugin");
            put("rtf", "text/rtf");
            put("rtx", "text/richtext");
            put("sgm", "text/sgml");
            put("sgml", "text/sgml");
            put("sh", "application/x-sh");
            put("shar", "application/x-shar");
            put("silo", "model/mesh");
            put("sit", "application/x-stuffit");
            put("skd", "application/x-koan");
            put("skm", "application/x-koan");
            put("skp", "application/x-koan");
            put("skt", "application/x-koan");
            put("smi", "application/smil");
            put("smil", "application/smil");
            put("snd", "audio/basic");
            put("so", "application/octet-stream");
            put("spl", "application/x-futuresplash");
            put("src", "application/x-wais-source");
            put("sv4cpio", "application/x-sv4cpio");
            put("sv4crc", "application/x-sv4crc");
            put("swf", "application/x-shockwave-flash");
            put("t", "application/x-troff");
            put("tar", "application/x-tar");
            put("tcl", "application/x-tcl");
            put("tex", "application/x-tex");
            put("texi", "application/x-texinfo");
            put("texinfo", "application/x-texinfo");
            put("tif", "image/tiff");
            put("tiff", "image/tiff");
            put("tr", "application/x-troff");
            put("tsv", "text/tab-separated-values");
            put("txt", "text/plain");
            put("ustar", "application/x-ustar");
            put("vcd", "application/x-cdlink");
            put("vrml", "model/vrml");
            put("wav", "audio/x-wav");
            put("wbmp", "image/vnd.wap.wbmp");
            put("wbxml", "application/vnd.wap.wbxml");
            put("wml", "text/vnd.wap.wml");
            put("wmlc", "application/vnd.wap.wmlc");
            put("wmls", "text/vnd.wap.wmlscript");
            put("wmlsc", "application/vnd.wap.wmlscriptc");
            put("wrl", "model/vrml");
            put("xbm", "image/x-xbitmap");
            put("xht", "application/xhtml+xml");
            put("xhtml", "application/xhtml+xml");
            put("xls", "application/vnd.ms-excel");
            put("xml", "text/xml");
            put("xpm", "image/x-xpixmap");
            put("xsl", "text/xml");
            put("xwd", "image/x-xwindowdump");
            put("xyz", "chemical/x-xyz");
            put("zip", "application/zip");
        }
    };

    public static String getContentType(String format) {
        if (null != format) {
            if (format.contains(".")) {
                format = format.substring(format.lastIndexOf(".") + 1);
            }
            String contentType = map.get(format.toLowerCase());
            if (null != contentType) {
                return contentType;
            }
        }
        return "application/octet-stream";
    }
}
