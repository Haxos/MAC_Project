package models;

public class Ingredient {
    private Unit _unit;
    private double _quantity = 1.0;
    private String _name;

    public Ingredient(String name) {
        this(1, name);
    }

    public Ingredient(double quantity, String name) {
        this(Unit.NONE, quantity, name);
    }

    public Ingredient(Unit unit, double quantity, String name) {
        this._unit = unit;
        this._quantity = quantity;
        this._name = name;
    }

    public Unit getUnit() {
        return _unit;
    }

    public void setUnit(Unit unit) { this._unit = unit; }

    public double getQuantity() {
        return _quantity;
    }

    public void setQuantity(double quantity) { this._quantity = quantity; }

    public String getName() {
        return _name;
    }

    public String toString() {
        return this._quantity + " " + this._unit + " " + this._name;
    }
}
