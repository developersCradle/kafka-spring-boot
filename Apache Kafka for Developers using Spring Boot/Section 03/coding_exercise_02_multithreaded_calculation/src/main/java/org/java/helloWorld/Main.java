package org.java.helloWorld;

import java.math.BigInteger;

public class Main {

    public static void main(String[] args) {

        System.out.println("Staring the exercise 02, pow number calculation!");

        BigInteger result = new ComplexCalculation().calculateResult(BigInteger.TWO, BigInteger.TEN, BigInteger.TEN, BigInteger.TEN);

        System.out.println(result);
    }


}

class ComplexCalculation {
    public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) {

        /*
            Calculate result = ( base1 ^ power1 ) + (base2 ^ power2).
            Where each calculation in (..) is calculated on a different thread
        */

        PowerCalculatingThread calulation01 = new PowerCalculatingThread(base1, power1);
        PowerCalculatingThread calulation02 = new PowerCalculatingThread(base2, power2);

        calulation01.start();
        calulation02.start();

        try {
            calulation01.join();
            calulation02.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return calulation01.getResult().add(calulation02.getResult());
    }

    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private BigInteger base;
        private BigInteger power;

        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {

           /*
                Implement the calculation of result = base ^ power
           */
            /*
                power = 0 → ✅ allowed.
                power > 0 → ✅ allowed.
                power < 0 → ❌ NOT allowed.
            */
            if (power.compareTo(BigInteger.ZERO) < 0 || base.compareTo(BigInteger.ZERO) < 0)
            {
                throw new IllegalArgumentException("Base and Power needs to follow rule: base1 >= 0, base2 >= 0, power1 >= 0, power2 >= 0");
            }

            result = base.pow(power.intValue());
        }


        public BigInteger getResult() {
            return result;
        }
    }
}



