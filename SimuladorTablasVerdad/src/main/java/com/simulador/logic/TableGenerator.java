package com.simulador.logic;

import com.simulador.util.Json;

import java.util.*;

public class TableGenerator {

    public static String generateJsonTable(String rawExpression) {
        // Validaciones de seguridad estrictas antes de procesar
        Evaluator.validateStrict(rawExpression);

        // Normaliza unicamente delimitadores ([ { a ()
        String cleanExpr = Evaluator.normalizeDelimiters(rawExpression);

        // Extraccion de variables
        List<Character> vars = Evaluator.extractVariables(cleanExpr);
        if (vars.isEmpty()) {
            throw new IllegalArgumentException("La expresión debe contener al menos una variable proposicional.");
        }

        // Control de consumo de RAM (Maximo 5 variables = 32 filas)
        if (vars.size() > 5) {
            throw new IllegalArgumentException("El simulador admite un máximo de 5 variables proposicionales.");
        }

        List<String> rpn = Evaluator.toRPN(cleanExpr);
        int numRows = (int) Math.pow(2, vars.size());

        StringBuilder json = new StringBuilder();
        json.append("{");

        // Construye encabezados con escape JSON; el cliente los renderiza como texto, no como HTML
        json.append("\"headers\":[");
        for (int i = 0; i < vars.size(); i++) {
            json.append("\"").append(vars.get(i)).append("\",");
        }
        json.append("\"").append(Json.escape(rawExpression)).append("\"],");

        // Construccion de filas
        json.append("\"rows\":[");
        for (int i = 0; i < numRows; i++) {
            Map<Character, Boolean> varMap = new HashMap<>();
            json.append("[");

            for (int j = 0; j < vars.size(); j++) {
                int shift = vars.size() - 1 - j;
                boolean val = ((i >> shift) & 1) == 0;
                varMap.put(vars.get(j), val);

                json.append("\"").append(val ? "V" : "F").append("\",");
            }

            boolean result = Evaluator.evaluateRPN(rpn, varMap);
            json.append("\"").append(result ? "V" : "F").append("\"");

            json.append("]").append(i < numRows - 1 ? "," : "");
        }
        json.append("]}");

        return json.toString();
    }
}