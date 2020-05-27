package net.jmorg.garbageenergy.utils;

public class EnergyConfig
{

    public int minPower = 8;
    public int maxPower = 80;
    public int maxEnergy = 40000;
    public int minPowerLevel = maxEnergy / 10;
    public int maxPowerLevel = 9 * maxEnergy / 10;
    public int energyRamp = maxPowerLevel / maxPower;

    public EnergyConfig() { }

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

    public boolean setParams(int minPower, int maxPower, int maxEnergy)
    {
        this.minPower = minPower;
        this.maxPower = maxPower;
        this.maxEnergy = maxEnergy;
        this.maxPowerLevel = maxEnergy * 8 / 10;
        this.energyRamp = maxPower > 0 ? maxPowerLevel / maxPower : 0;
        this.minPowerLevel = minPower * energyRamp;

        return true;
    }

    public boolean setParamsPower(int maxPower)
    {
        return setParams(maxPower / 4, maxPower, maxPower * 1200);
    }

    public boolean setParamsPower(int maxPower, int scale)
    {
        return setParams(maxPower / 4, maxPower, maxPower * 1200 * scale);
    }

    public boolean setParamsEnergy(int maxEnergy)
    {
        return setParams(maxEnergy / 4800, maxEnergy / 1200, maxEnergy);
    }

    public boolean setParamsEnergy(int maxEnergy, int scale)
    {
        maxEnergy *= scale;
        return setParams(maxEnergy / 4800, maxEnergy / 1200, maxEnergy);
    }

    public boolean setParamsDefault(int maxPower)
    {
        this.maxPower = maxPower;
        minPower = maxPower / 10;
        maxEnergy = maxPower * 500;
        minPowerLevel = maxEnergy / 10;
        maxPowerLevel = 9 * maxEnergy / 10;
        energyRamp = maxPowerLevel / maxPower;

        return true;
    }
}
