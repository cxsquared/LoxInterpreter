package me.codyclaborn.lox;

import javax.management.RuntimeErrorException;
import java.util.DoubleSummaryStatistics;
import java.util.Objects;

/**
 * Created by cxsqu on 4/9/2017.
 */
public class Interpreter implements Expr.Visitor<Object> {

    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
           Lox.runtimeError(error);
        }
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        // Hack. Work around Java adding ".0" to integer-valued doubles.
        if (object instanceof Double) {
            return stringifyNumber((double)object);
        }

        return object.toString();
    }

    private String stringifyNumber(Double num) {
        String text = num.toString();
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
        }
        return text;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
       return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            case BANG:
                return !isTrue(right);
            case MINUS_MINUS:
                checkNumberOperand(expr.operator, right);
                return (double)right - 1;
            case PLUS_PLUS:
                checkNumberOperand(expr.operator, right);
                return (double)right + 1;
        }

        // Unreachable
        return null;
    }

    @Override
    public Object visitPostfixExpr(Expr.Postfix expr) {
        Object left = evaluate(expr.left);

        switch (expr.operator.type) {
            case PLUS_PLUS:
                checkNumberOperand(expr.operator, left);
                return (double)left + 1;
            case MINUS_MINUS:
                checkNumberOperand(expr.operator, left);
                return (double)left - 1;
        }

        // Unreachable
        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    @Override
    public Object visitConditionalExpr(Expr.Conditional expr) {
        return isTrue(evaluate(expr.left)) ? evaluate(expr.mid) : evaluate(expr.right);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof  Double) {
                    return (double)left + (double)right;
                }

                if (left instanceof  String && right instanceof String) {
                    return (String)left + right;
                }

                if (left instanceof  String || right instanceof String) {
                    if (left instanceof String && right instanceof Double) {
                        return left + stringifyNumber((double) right);
                    }
                    if (left instanceof Double && right instanceof String)
                    return stringifyNumber((double)left) + right;
                }

                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }

        // Unreachable
        return null;
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
       if (left instanceof Double && right instanceof Double) return;
       throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private boolean isTrue(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        if (object instanceof  Double && (double)object == 0) return false;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        // nil is only qual to nil
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }
}
