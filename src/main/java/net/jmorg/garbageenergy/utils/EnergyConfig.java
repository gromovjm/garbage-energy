package net.jmorg.garbageenergy.utils;

public class EnergyConfig
{
    public int minPower = 1;
    public int maxPower = 16;
    public int maxEnergy = 1600;
    public int maxPowerLevel = 9 * maxEnergy / 10;
    public int energyRamp = maxPowerLevel / maxPower;
    public int minPowerLevel = energyRamp + maxPower;

    public EnergyConfig() { }

    public void setParams(int maxPower)
    {
        this.maxPower = maxPower;
        this.maxEnergy = maxPower * 100;
        this.maxPowerLevel = 9 * maxEnergy / 10;
        this.energyRamp = maxPowerLevel / maxPower;
        this.minPowerLevel = energyRamp + maxPower;
    }

    public EnergyConfig(EnergyConfig config)
    {
        this.minPower = config.minPower;
        this.maxPower = config.maxPower;
        this.maxEnergy = config.maxEnergy;
        this.minPowerLevel = config.minPowerLevel;
        this.maxPowerLevel = config.maxPowerLevel;
        this.energyRamp = config.energyRamp;
    }

    public EnergyConfig copy()
    {
        return new EnergyConfig(this);
    }
}
