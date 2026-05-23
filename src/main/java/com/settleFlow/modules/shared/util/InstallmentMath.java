package com.settleFlow.modules.shared.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class InstallmentMath {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    /**
     * Monthly payment using standard amortisation formula:
     *
     *        P × r × (1 + r)^n
     * M  =  ─────────────────────
     *          (1 + r)^n  −  1
     *
     * Where:
     *   P = principal (loan amount)
     *   r = monthly interest rate (annual rate / 12)
     *   n = number of months
     *
     * Same formula used by every bank and mortgage calculator.
     */
    public static BigDecimal monthlyPayment(BigDecimal principal,
                                            BigDecimal annualRate,
                                            int months) {
        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            // Zero interest — simple division
            return principal.divide(BigDecimal.valueOf(months), SCALE, ROUNDING);
        }

        MathContext mc = new MathContext(10, ROUNDING);

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), mc);

        // (1 + r)^n
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate, mc);
        BigDecimal onePlusRPowN = onePlusR.pow(months, mc);

        // numerator: P × r × (1+r)^n
        BigDecimal numerator = principal.multiply(monthlyRate, mc)
                .multiply(onePlusRPowN, mc);

        // denominator: (1+r)^n − 1
        BigDecimal denominator = onePlusRPowN.subtract(BigDecimal.ONE, mc);

        return numerator.divide(denominator, SCALE, ROUNDING);
    }

    /**
     * Total repayment amount (monthly payment × number of months)
     */
    public static BigDecimal totalRepayment(BigDecimal principal,
                                            BigDecimal annualRate,
                                            int months) {
        return monthlyPayment(principal, annualRate, months)
                .multiply(BigDecimal.valueOf(months))
                .setScale(SCALE, ROUNDING);
    }

    /**
     * Total interest paid over the loan life
     */
    public static BigDecimal totalInterest(BigDecimal principal,
                                           BigDecimal annualRate,
                                           int months) {
        return totalRepayment(principal, annualRate, months)
                .subtract(principal)
                .setScale(SCALE, ROUNDING);
    }
}