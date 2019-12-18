package models;

public class Ingredient {
    private Unit _unit;
    private double _quantity;
    private String _name;

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

    public double getQuantity() {
        return _quantity;
    }

    public String getName() {
        return _name;
    }
}
