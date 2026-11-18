package com.simulador.util;

import java.util.HashMap;
import java.util.Map;

// Utilidades JSON minimas sin librerias externas
public final class Json {

    private Json() {
    }

    // Escapa un texto para insertarlo dentro de una cadena JSON (RFC 8259)
    public static String escape(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder(text.length() + 16);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    // Parsea estrictamente un objeto JSON plano cuyos valores son cadenas: {"clave": "valor", ...}
    public static Map<String, String> parseFlatObject(String json) {
        return new Parser(json).parseObject();
    }

    private static final class Parser {
        private final String src;
        private int pos;

        Parser(String src) {
            if (src == null) throw new IllegalArgumentException("Cuerpo JSON vacío.");
            this.src = src;
        }

        Map<String, String> parseObject() {
            Map<String, String> result = new HashMap<>();
            skipWhitespace();
            expect('{');
            skipWhitespace();
            if (peek() == '}') {
                pos++;
            } else {
                while (true) {
                    skipWhitespace();
                    String key = parseString();
                    skipWhitespace();
                    expect(':');
                    skipWhitespace();
                    String value = parseString();
                    if (result.put(key, value) != null) {
                        throw invalid();
                    }
                    skipWhitespace();
                    char c = next();
                    if (c == '}') break;
                    if (c != ',') throw invalid();
                }
            }
            skipWhitespace();
            if (pos != src.length()) throw invalid();
            return result;
        }

        private String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = next();
                if (c == '"') return sb.toString();
                if (c < 0x20) throw invalid();
                if (c != '\\') {
                    sb.append(c);
                    continue;
                }
                char esc = next();
                switch (esc) {
                    case '"':  sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/':  sb.append('/'); break;
                    case 'b':  sb.append('\b'); break;
                    case 'f':  sb.append('\f'); break;
                    case 'n':  sb.append('\n'); break;
                    case 'r':  sb.append('\r'); break;
                    case 't':  sb.append('\t'); break;
                    case 'u':
                        if (pos + 4 > src.length()) throw invalid();
                        try {
                            sb.append((char) Integer.parseInt(src.substring(pos, pos + 4), 16));
                        } catch (NumberFormatException e) {
                            throw invalid();
                        }
                        pos += 4;
                        break;
                    default: throw invalid();
                }
            }
        }

        private void skipWhitespace() {
            while (pos < src.length()) {
                char c = src.charAt(pos);
                if (c != ' ' && c != '\t' && c != '\n' && c != '\r') break;
                pos++;
            }
        }

        private char peek() {
            if (pos >= src.length()) throw invalid();
            return src.charAt(pos);
        }

        private char next() {
            char c = peek();
            pos++;
            return c;
        }

        private void expect(char expected) {
            if (next() != expected) throw invalid();
        }

        private IllegalArgumentException invalid() {
            return new IllegalArgumentException("El cuerpo de la petición no es un JSON válido.");
        }
    }
}
