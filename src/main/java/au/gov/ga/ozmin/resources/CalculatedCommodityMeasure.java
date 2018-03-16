package au.gov.ga.ozmin.resources;

import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;

public class CalculatedCommodityMeasure implements CommodityMeasure {

    private Quantity<Mass> ore;
    private Quantity<Dimensionless> grade;
    private Quantity<Mass> containedCommodity;
    private String commodity;

    public CalculatedCommodityMeasure(Quantity<Mass> ore, Quantity<Mass> containedCommodity, String commodity) {
        this.ore = ore;
        this.containedCommodity = containedCommodity;
        this.commodity = commodity;
    };

    @Override
    public Quantity<Mass> getOre() {
        return ore;
    }

    @Override
    public Quantity<Dimensionless> getGrade() {
        if (ore.getValue().intValue() == 0) {
            return Quantities.getQuantity(0, Units.PERCENT);
        }
        return ((Quantity<Dimensionless>) containedCommodity.divide(ore)).to(Units.PERCENT);
    }

    @Override
    public Quantity<Mass> getContainedCommodity() {
        return containedCommodity;
    }

    @Override
    public String getCommodity() {
        return commodity;
    }

    @Override
    public CommodityMeasure add(CommodityMeasure measure){
        if (!this.commodity.equals(measure.getCommodity())) {
            return null;
        }
        Quantity<Mass> calculatedOre = this.ore.add(measure.getOre());
        Quantity<Mass> calculatedContainedCommodity = this.getContainedCommodity().add(measure.getContainedCommodity());
        return new CalculatedCommodityMeasure(calculatedOre,calculatedContainedCommodity, this.commodity);
    }

    @Override
    public CommodityMeasure subtract(CommodityMeasure measure) {
        return null;
    }
}