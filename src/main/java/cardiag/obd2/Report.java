/**
 *
 */
package cardiag.obd2;

import java.util.Arrays;

/**
 * @author David Matějček
 */
public class Report {

  private boolean[] supportedPIDS;
  private MonitorStatus monitorStatus;
  private int distanceSinceErrorCodesCleared;
  private int distanceWithMalfunction;
  private int engineCoolantTemperature;
  private double engineLoad;
  private double fuelInjectionTiming;
  private double fuelLevelInput;
  private double fuelRate;
  private FuelStatus fuelStatus;
  private double fuelTrimPercentShortTerm;
  private double fuelTrimPercentLongTerm;
  private int intakeAirTemperature;


  public boolean[] getSupportedPIDS() {
    return Arrays.copyOf(supportedPIDS, supportedPIDS.length);
  }


  public void setSupportedPIDS(boolean[] supportedPIDS) {
    this.supportedPIDS = Arrays.copyOf(supportedPIDS, supportedPIDS.length);
  }


  public MonitorStatus getMonitorStatus() {
    return monitorStatus;
  }


  public void setMonitorStatus(MonitorStatus monitorStatus) {
    this.monitorStatus = monitorStatus;
  }


  public int getDistanceSinceErrorCodesCleared() {
    return distanceSinceErrorCodesCleared;
  }


  public void setDistanceSinceErrorCodesCleared(int distanceSinceErrorCodesCleared) {
    this.distanceSinceErrorCodesCleared = distanceSinceErrorCodesCleared;
  }


  public int getDistanceWithMalfunction() {
    return distanceWithMalfunction;
  }


  public void setDistanceWithMalfunction(int distanceWithMalfunction) {
    this.distanceWithMalfunction = distanceWithMalfunction;
  }


  public int getEngineCoolantTemperature() {
    return engineCoolantTemperature;
  }


  public void setEngineCoolantTemperature(int engineCoolantTemperature) {
    this.engineCoolantTemperature = engineCoolantTemperature;
  }


  public double getEngineLoad() {
    return engineLoad;
  }


  public void setEngineLoad(double engineLoad) {
    this.engineLoad = engineLoad;
  }


  public double getFuelInjectionTiming() {
    return fuelInjectionTiming;
  }


  public void setFuelInjectionTiming(double fuelInjectionTiming) {
    this.fuelInjectionTiming = fuelInjectionTiming;
  }


  public double getFuelLevelInput() {
    return fuelLevelInput;
  }


  public void setFuelLevelInput(double fuelLevelInput) {
    this.fuelLevelInput = fuelLevelInput;
  }


  public double getFuelRate() {
    return fuelRate;
  }


  public void setFuelRate(double fuelRate) {
    this.fuelRate = fuelRate;
  }


  public FuelStatus getFuelStatus() {
    return fuelStatus;
  }


  public void setFuelStatus(FuelStatus fuelStatus) {
    this.fuelStatus = fuelStatus;
  }


  public double getFuelTrimPercentShortTerm() {
    return fuelTrimPercentShortTerm;
  }


  public void setFuelTrimPercentShortTerm(double fuelTrimPercentShortTerm) {
    this.fuelTrimPercentShortTerm = fuelTrimPercentShortTerm;
  }


  public double getFuelTrimPercentLongTerm() {
    return fuelTrimPercentLongTerm;
  }


  public void setFuelTrimPercentLongTerm(double fuelTrimPercentLongTerm) {
    this.fuelTrimPercentLongTerm = fuelTrimPercentLongTerm;
  }


  public int getIntakeAirTemperature() {
    return intakeAirTemperature;
  }


  public void setIntakeAirTemperature(int intakeAirTemperature) {
    this.intakeAirTemperature = intakeAirTemperature;
  }

}
