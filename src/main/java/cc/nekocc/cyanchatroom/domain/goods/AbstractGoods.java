package cc.nekocc.cyanchatroom.domain.goods;

public abstract class AbstractGoods
{
    private String name_;
    private double width_, length_, height_, weight_;

    public double getBillableWeight()
    {
        double volumetric_weight = (width_ * length_ * height_) / 6000.0;
        return Math.max(volumetric_weight, weight_);
    }

    public double getPrice()
    {
        return getBillingRate() * getBillableWeight();
    }

    public abstract double getBillingRate();

    public String getName()
    {
        return name_;
    }

    public void setName(String name)
    {
        name_ = name;
    }

    public double getWidth()
    {
        return width_;
    }

    public void setWidth(double width)
    {
        width_ = width;
    }

    public double getLength()
    {
        return length_;
    }

    public void setLength(double length)
    {
        length_ = length;
    }

    public double getHeight()
    {
        return height_;
    }

    public void setHeight(double height)
    {
        height_ = height;
    }

    public double getWeight()
    {
        return weight_;
    }

    public void setWeight(double weight)
    {
        weight_ = weight;
    }

    public double getVolume()
    {
        return width_ * length_ * height_;
    }

    public abstract String getDisplayTypeName();
}