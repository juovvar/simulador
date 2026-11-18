package com.simulador.logic;

import java.util.*;

public class Evaluator {

    // Solo se admiten letras latinas minúsculas y los simbolos de la UI
    public static void validateStrict(String expr) {
        if (expr == null || expr.trim().isEmpty()) {
            throw new IllegalArgumentException("La expresión no puede estar vacía.");
        }

        // Limite de longitud (Prevencion DoS)
        if (expr.length() > 50) {
            throw new IllegalArgumentException("La expresión excede el límite máximo de 50 caracteres.");
        }

        // Regex estricto: solo corchetes/llaves/parentesis, negacion, conectivos de la UI y variables a-z
        String regex = "^[a-z()\\[\\]{}~∧∨Δ→↔]+$";
        if (!expr.matches(regex)) {
            throw new IllegalArgumentException("Entrada no válida. Solo se permiten los símbolos del teclado virtual.");
        }

        validateDelimiterPairs(expr);
    }

    // Verifica que cada delimitador cierre con su pareja: ( ), [ ], { }
    private static void validateDelimiterPairs(String expr) {
        Deque<Character> open = new ArrayDeque<>();
        for (char c : expr.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                open.push(c);
            } else if (c == ')' || c == ']' || c == '}') {
                char expected = c == ')' ? '(' : c == ']' ? '[' : '{';
                if (open.isEmpty() || open.pop() != expected) {
                    throw new IllegalArgumentException("Delimitadores desbalanceados.");
                }
            }
        }
        if (!open.isEmpty()) {
            throw new IllegalArgumentException("Delimitadores desbalanceados.");
        }
    }

    // Normaliza solo delimitadores a parentesis para el parser
    public static String normalizeDelimiters(String expr) {
        return expr.replace("[", "(").replace("]", ")")
                .replace("{", "(").replace("}", ")")
                .replaceAll("\\s+", "");
    }

    // Extrae variables proposicionales unicas
    public static List<Character> extractVariables(String expr) {
        Set<Character> vars = new TreeSet<>();
        for (char c : expr.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                vars.add(c);
            }
        }
        return new ArrayList<>(vars);
    }

    // Algoritmo Shunting-Yard (Infija -> RPN)
    public static List<String> toRPN(String expr) throws IllegalArgumentException {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        int i = 0;
        int n = expr.length();
        // Gramatica infija: tras un operador o '(' se espera operando; tras un operando, operador binario o ')'
        boolean expectOperand = true;

        while (i < n) {
            char c = expr.charAt(i);
            boolean isOperandStart = (c >= 'a' && c <= 'z') || c == '(' || c == '~';
            if (expectOperand != isOperandStart) {
                throw new IllegalArgumentException(expectOperand
                        ? "Expresión mal formada: falta un operando antes de '" + c + "' (posición " + (i + 1) + ")."
                        : "Expresión mal formada: falta un conectivo antes de '" + c + "' (posición " + (i + 1) + ").");
            }

            if (c >= 'a' && c <= 'z') {
                output.add(String.valueOf(c));
                expectOperand = false;
                i++;
            } else if (c == '(') {
                stack.push("(");
                i++;
            } else if (c == ')') {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Delimitadores desbalanceados.");
                }
                stack.pop();
                i++;
            } else {
                String op = null;
                if (c == '~') { op = "~"; i++; }
                else if (c == '∧') { op = "∧"; i++; }
                else if (c == '∨') { op = "∨"; i++; }
                else if (c == 'Δ') { op = "Δ"; i++; }
                else if (c == '→') { op = "→"; i++; }
                else if (c == '↔') { op = "↔"; i++; }
                else {
                    throw new IllegalArgumentException("Carácter no reconocido: " + c);
                }

                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(op)) {
                    if (op.equals("~") && stack.peek().equals("~")) {
                        break;
                    }
                    output.add(stack.pop());
                }
                stack.push(op);
                // ~ es prefijo y sigue esperando operando; los binarios exigen uno nuevo
                expectOperand = true;
            }
        }

        if (expectOperand) {
            throw new IllegalArgumentException("Expresión mal formada: la expresión termina sin operando.");
        }

        while (!stack.isEmpty()) {
            String top = stack.pop();
            if (top.equals("(")) {
                throw new IllegalArgumentException("Delimitadores desbalanceados.");
            }
            output.add(top);
        }

        return output;
    }

    // Evalua RPN
    public static boolean evaluateRPN(List<String> rpn, Map<Character, Boolean> values) {
        Stack<Boolean> stack = new Stack<>();

        for (String token : rpn) {
            if (token.length() == 1 && token.charAt(0) >= 'a' && token.charAt(0) <= 'z') {
                char var = token.charAt(0);
                stack.push(values.get(var));
            } else if (token.equals("~")) {
                if (stack.isEmpty()) throw new IllegalArgumentException("Operando faltante para ~");
                boolean a = stack.pop();
                stack.push(!a);
            } else {
                if (stack.size() < 2) throw new IllegalArgumentException("Expresión mal formada.");
                boolean b = stack.pop();
                boolean a = stack.pop();

                switch (token) {
                    case "∧": stack.push(a && b); break;
                    case "∨": stack.push(a || b); break;
                    case "Δ": stack.push(a != b); break;
                    case "→": stack.push(!a || b); break;
                    case "↔": stack.push(a == b); break;
                    default: throw new IllegalArgumentException("Operador desconocido: " + token);
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Sintaxis de expresión inválida.");
        }

        return stack.pop();
    }

    private static int precedence(String op) {
        switch (op) {
            case "~": return 6;
            case "∧": return 5;
            case "∨": return 4;
            case "Δ": return 3;
            case "→": return 2;
            case "↔": return 1;
            default: return 0;
        }
    }
}