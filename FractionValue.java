package database.project.f21621557;

public class FractionValue extends Value {
    private int numerator;
    private int denominator;


    public FractionValue(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }


    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }


    @Override
    public DataType getType() {
        return DataType.FRACTION;
    }

    @Override
    public Object getValue() {
        return this;
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

}